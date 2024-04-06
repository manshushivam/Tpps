package com.example.tpps.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.example.tpps.dataModel.MoharDataModel;
import com.example.tpps.sms.SendWhatsAppSMS;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreatePdf {


    public  File generatePdf(Context context , Drawable d, MoharDataModel order ) throws FileNotFoundException {
        try {
            File pdfPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),  order.getName()+" "+order.getAddress()+".pdf");


            OutputStream outputStream = new FileOutputStream(pdfPath);

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
//          PageSize halfA4 = new PageSize(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight()/2);
//          pdfDocument.setDefaultPageSize(halfA4);
            Document document = new Document(pdfDocument);


            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapData = stream.toByteArray();

            ImageData imageData = ImageDataFactory.create(bitmapData);
            Image image = new Image(imageData);
            image.setHeight(60);
            image.setHeight(60);
            image.setFixedPosition(20, 750);
            document.add(image);
            // Add header information
            addHeader(document);
            addBody(document,order);
            document.close();

            return pdfPath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
    private void addHeader(Document document) {
        // Add Heading
        Paragraph heading = new Paragraph("Shivam Pustak Lok");
        heading.setTextAlignment(TextAlignment.CENTER);
        heading.setFontSize(25f);
        document.add(heading);

        // Add Address
        Paragraph address = new Paragraph("Dumraon (Buxar) Pin Code - 802119");
        address.setTextAlignment(TextAlignment.CENTER);
        address.setFontSize(12f);
        document.add(address);

        // Add Date, Invoice No, and Mobile No
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Create a paragraph with aligned text
        Paragraph date = new Paragraph(String.format("Date: %-20s", currentDate));
        date.setTextAlignment(TextAlignment.LEFT);
        document.add(date);


        // Add Underline
        Paragraph underline = new Paragraph("_________________________________________________________________________");
        underline.setTextAlignment(TextAlignment.CENTER);
        document.add(underline);

        // Add some space after the header
        document.add(new Paragraph("\n"));
    }

    private void addBody(Document document,MoharDataModel order ){

        Paragraph name = new Paragraph("Name - "+order.getName());
        name.setTextAlignment(TextAlignment.LEFT);
        name.setFontSize(12f);
        document.add(name);

        float pageWidth = PageSize.A4.getWidth();
        float x = pageWidth - 200f; // 200f from the right

        Paragraph Address = new Paragraph("Address - "+order.getAddress());
        Address.setTextAlignment(TextAlignment.LEFT);
        document.add(Address);

        Paragraph Content = new Paragraph(order.getContent());
        Content.setTextAlignment(TextAlignment.LEFT);
        Content.setFontSize(20f);
        Content.setFixedPosition(40f,450f,450f);
        document.add(Content);

        Paragraph TotalAmount = new Paragraph("Total Amount    Rs " +order.getTotalAmount());
        TotalAmount.setTextAlignment(TextAlignment.LEFT);
        TotalAmount.setFixedPosition(x,120f,PageSize.A4.getWidth());
        document.add(TotalAmount);



        Paragraph PaidAmount = new Paragraph("Paid Amount    Rs "+order.getPaidAmount());
        PaidAmount.setTextAlignment(TextAlignment.LEFT);
        PaidAmount.setFixedPosition(x,100f,PageSize.A4.getWidth());
        document.add(PaidAmount);

        int totalAmount = Integer.parseInt(order.getTotalAmount());
        int paidAmount = Integer.parseInt(order.getPaidAmount());
        int dueAmount = totalAmount - paidAmount;


        Paragraph DueAmount = new Paragraph("Due Amount: Rs " + dueAmount);
        DueAmount.setTextAlignment(TextAlignment.LEFT);
        DueAmount.setFixedPosition(x, 80f, PageSize.A4.getWidth());
        document.add(DueAmount);


        Paragraph dueDate = new Paragraph("Expected Delivery Date "+order.getDueDate());
        dueDate.setTextAlignment(TextAlignment.LEFT);
        dueDate.setFixedPosition(40f,60f,PageSize.A4.getWidth());
        document.add(dueDate);



        Paragraph underline = new Paragraph("_________________________________________________________________________");
        underline.setTextAlignment(TextAlignment.CENTER);
        underline.setFixedPosition(0f,20f,PageSize.A4.getWidth());
        document.add(underline);


    }

}
