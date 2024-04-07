package com.example.tpps.adapter;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpps.Activity.FullScreenImageActivity;
//import com.example.tpps.Activity.FullScreenPDFViwer;
import com.example.tpps.Config.MyDB;
import com.example.tpps.CreateOrder;
import com.example.tpps.MainActivity;
import com.example.tpps.R;
import com.example.tpps.pdf.CreatePdf;
import com.example.tpps.sms.SendSMS;
import com.example.tpps.sms.SendWhatsAppSMS;
import com.example.tpps.dataModel.MoharDataModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterReadOrder extends RecyclerView.Adapter<AdapterReadOrder.ViewHolder>  {

    private LayoutInflater layoutInflater;
    private List<MoharDataModel> dataList;

    private List<MoharDataModel> originalDataList;
    private String filterOrderType;  // Variable to store the selected order type for filtering
    private String filterStage;



    public AdapterReadOrder(Context context, List<MoharDataModel> dataList){
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.originalDataList = new ArrayList<>(dataList);
        this.filterOrderType = ""; // Initialize filterOrderType
        this.filterStage = "";

    }

    public void filterByMobileNumber(String mobileNumber) {
        dataList.clear();

        if (mobileNumber.isEmpty()) {
            dataList.addAll(originalDataList);
        } else {
            for (MoharDataModel item : originalDataList) {
                if (item.getMobileNo().toLowerCase(Locale.getDefault()).contains(mobileNumber.toLowerCase(Locale.getDefault()))) {
                    dataList.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }


    public void setFilterByOrderType(String orderType) {
        filterOrderType = orderType;
        notifyDataSetChanged();
    }

    public void setFilterByStage(String stage) {
        filterStage = stage;
        notifyDataSetChanged();
    }

    public void setFilters(String orderType, String stage) {
        filterOrderType = orderType;
        filterStage = stage;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.items_card,viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            MoharDataModel currentItem = dataList.get(position);


            if ((filterOrderType.isEmpty() || currentItem.getOrderType().equalsIgnoreCase(filterOrderType))
                    && (filterStage.isEmpty() || currentItem.getStage().equalsIgnoreCase(filterStage))) {
                // Update the views with the data
                // ...
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                // If order type or stage doesn't match, hide the view
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }


            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUrl = currentItem.getImageUrl();
                    if (imageUrl == null) {
                        Toast.makeText(v.getContext(), "Image Not available", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(v.getContext(), FullScreenImageActivity.class);
                        intent.putExtra("IMAGE_URL", imageUrl);
                        v.getContext().startActivity(intent);
                    }
                }
            });
            String nameOfCustomer = currentItem.getName();
            nameOfCustomer=Character.toUpperCase(nameOfCustomer.charAt(0)) + nameOfCustomer.substring(1);

            holder.name.setText(nameOfCustomer);
            holder.address.setText(currentItem.getAddress());
            holder.headDueDate.setText(currentItem.getDueDate());
            holder.moharContent.setText(currentItem.getContent());
            holder.invoiceNo.setText("Invoice No : " + currentItem.getGetId());
            holder.orderType.setText("Order Type : " + currentItem.getOrderType());
            holder.orderDate.setText("Order Date : " + currentItem.getOrderDate());
            holder.dueDate.setText("Due Date :    " + currentItem.getDueDate());
            holder.mobileNo.setText("Mobile No : " + currentItem.getMobileNo());
            holder.totalAmount.setText("Total : Rs " + currentItem.getTotalAmount());
            holder.paidAmount.setText("Paid : Rs " + currentItem.getPaidAmount());

            double totalAmount = Double.parseDouble(currentItem.getTotalAmount());
            double paidAmount = Double.parseDouble(currentItem.getPaidAmount());
            double dueAmount = totalAmount - paidAmount;
            holder.dueAmount.setText("Due: Rs " + dueAmount);


            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle phone number click
                    String phoneNumber = currentItem.getMobileNo();
                    startPhoneCall(v.getContext(), phoneNumber);
                }
            });

            holder.stage.setText(currentItem.getStage());
            holder.setStageBackgroundColor(currentItem.getStage());
            holder.stage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click
                    showYesNoDialog(currentItem);

                    // Toast.makeText(v.getContext(), "pop" , Toast.LENGTH_SHORT).show();
                }
            });

            holder.whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = currentItem.getMobileNo();
                    startWhatsAppChat(v.getContext(), phoneNumber);
                    SendWhatsAppSMS whatsAppSMS = new SendWhatsAppSMS();
                    whatsAppSMS.startWhatsAppChat(v.getContext(), phoneNumber, " ");
                }
            });

            holder.pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                    Drawable d = v.getContext().getDrawable(R.drawable.tpps_logo);
                    CreatePdf createPdf = new CreatePdf();
                    File pdfPath = createPdf.generatePdf(v.getContext(), d, currentItem);
                    Toast.makeText(v.getContext(), "Saving in Storage/Documents"  , Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(pdfPath.toString()), "application/pdf");
                    // Ask the user to choose an app to open the PDF file
                    Intent chooserIntent = Intent.createChooser(intent, "Open PDF with");
                    // Verify that the intent will resolve to an activity
                    if (v.getContext() != null && chooserIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(chooserIntent);
                    }


//                    SendWhatsAppSMS whatsappSender = new SendWhatsAppSMS();
//                    whatsappSender.sendPdfThroughWhatsApp(layoutInflater.getContext(), currentItem.getMobileNo(), pdfFile.getAbsolutePath());
//                        //String pdfFileName = "your_pdf_file.pdf";
                        // Get the URI of the PDF file
                        //Uri pdfUri = Uri.parse("file:///android_asset/" + pdfFile);
                        // Create an intent to view the PDF using DrivePDF Viewer
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(pdfUri, "application/pdf");
//                        intent.setPackage("com.google.android.apps.pdfviewer"); // Package name of DrivePDF Viewer
//
//



//                    if(pdfFile != null) {
//                        Intent intent = new Intent(v.getContext(), FullScreenPDFViwer.class);
//                        intent.putExtra("PDFFilePath", pdfFile.getAbsolutePath());
//                        v.getContext().startActivity(intent);
//                    }else{
//                        Toast.makeText(v.getContext(), "PDF File Not Created" + pdfFile , Toast.LENGTH_SHORT).show();
//                    }

                    }catch(Exception e){
                        Toast.makeText(v.getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                }
            });


            holder.headerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle visibility of the content section
                    if (holder.body.getVisibility() == View.VISIBLE) {
                        holder.body.setVisibility(View.GONE);
                    } else {
                        holder.body.setVisibility(View.VISIBLE);
                    }
                }
            });




        }catch (Exception e){
            Toast.makeText(layoutInflater.getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();

    }



    // ... Other methods

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Declare views from your card layout
        LinearLayout headerLayout ;
        LinearLayout body ;
        LinearLayout bottom ;

        public TextView name;
        public TextView address;
        public ImageView image;

        public TextView headDueDate;
        public TextView moharContent;

        public TextView invoiceNo ;
        public TextView orderType;
        public TextView orderDate;

        public TextView dueDate;
        public TextView mobileNo;
        public TextView totalAmount;
        public TextView paidAmount;

        public TextView dueAmount;

        public Button stage;

        public FloatingActionButton pdf;
        public FloatingActionButton whatsapp;
        public FloatingActionButton call;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize views from your card layout
            // textViewTitle = itemView.findViewById(R.id.textViewTitle);
            name = itemView.findViewById(R.id.name_card);
            address = itemView.findViewById(R.id.Address_card);
            headerLayout = itemView.findViewById(R.id.headCard);
            body = itemView.findViewById(R.id.body);
            bottom = itemView.findViewById(R.id.body);
            image = itemView.findViewById(R.id.imageView_moharBook);
            moharContent = itemView.findViewById(R.id.textContent_moharBook);
            invoiceNo = itemView.findViewById(R.id.textInvoiceNo_moharBook);
            orderType = itemView.findViewById(R.id.textOrderType_moharBook);
            orderDate = itemView.findViewById(R.id.textOrderDate_moharBook);
            dueDate = itemView.findViewById(R.id.textDueDate_moharBook);
            mobileNo = itemView.findViewById(R.id.textMobileNo_moharBook);
            totalAmount = itemView.findViewById(R.id.textTotalAmount_moharBook);
            paidAmount = itemView.findViewById(R.id.textPaidAmount_moharBook);
            dueAmount = itemView.findViewById(R.id.textDueAmount_moharBook);
            stage = itemView.findViewById(R.id.btnStage_moharBook);
            pdf  = itemView.findViewById(R.id.pdf);
            whatsapp = itemView.findViewById(R.id.button_Whatsapp);
            call = itemView.findViewById(R.id.button_call);
            headDueDate = itemView.findViewById(R.id.headerDueDate);
        }

        public void setStageBackgroundColor(String s) {
            int colorResId;
            GradientDrawable gradientDrawable = null;
            switch (s) {
                case "Computer Work":
                    gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{Color.parseColor("#099773"), Color.parseColor("#43b692")});
                    gradientDrawable.setCornerRadius(0f); // Set corner radius if needed
                    colorResId = R.color.GreenOfLogo; // Use color resource
                    break;
                case "Printing Work":
                    gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{Color.parseColor("#2feaa8"), Color.parseColor("#028cf3")});
                    gradientDrawable.setCornerRadius(0f); // Set corner radius if needed
                    colorResId = R.color.BlueOfLogo; // Use color resource
                    break;
                case "Delivery":
                    gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{Color.parseColor("#f40752"), Color.parseColor("#f9ab8f")});
                    gradientDrawable.setCornerRadius(0f); // Set corner radius if needed
                    colorResId = R.color.RedOfLogo; // Use color resource
                    break;
                default:
                    gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{Color.parseColor("#e52d27"), Color.parseColor("#b31217")});
                    gradientDrawable.setCornerRadius(0f); // Set corner radius if needed
                    colorResId = R.color.defaultColor; // Use default color resource
            }

            int color = ContextCompat.getColor(itemView.getContext(), colorResId);
            LinearLayout linearLayout = itemView.findViewById(R.id.headCard);
            linearLayout.setBackground(gradientDrawable); // Set the background drawable
        }
    }


    // Update the phase and move to "Delivered Items" collection in the database


    // Move document to "Delivered Items" collection
    private void moveDocumentToDeliveredItems(MoharDataModel currentItem) {
        try {
            // Get a reference to the document in the "Orders" collection
            DocumentReference sourceDocRef = FirebaseFirestore.getInstance().collection(MyDB.Collections).document(currentItem.getGetId());
            // Get a reference to the destination document in the "Delivered Items" collection
            //DocumentReference destDocRef = FirebaseFirestore.getInstance().collection("DeliveredItems").document(currentItem.getGetId());
          //  Toast.makeText(layoutInflater.getContext(), "ting tongg", Toast.LENGTH_SHORT).show();
            // Copy the document to the new location
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(MyDB.Collection_DeliveredItems)
                    .add(currentItem)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Toast.makeText(getApplicationContext(), "Order Submitted", Toast.LENGTH_SHORT).show();
                            Toast.makeText(layoutInflater.getContext(), "Item moved to Delivered Items", Toast.LENGTH_SHORT).show();
//
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), "Error saving image URL to Firestore", Toast.LENGTH_LONG).show();
                            Toast.makeText(layoutInflater.getContext(), "Item failed to Delivered Items", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            // Delete the original document from the "Orders" collection
            sourceDocRef.delete();
        } catch (Exception e) {
            Toast.makeText(layoutInflater.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Handle button click and update phase in the database
    private void ChangeStage(MoharDataModel currentItem) {

        try {
            String currentItemStage = currentItem.getStage();

            switch (currentItemStage) {
                case "Computer Work":
                    currentItem.setStage("Printing Work");
                    updateDatabase(currentItem);
                    break;

                case "Printing Work":
                    currentItem.setStage("Delivery");
                    AskToSendSMS(currentItem);
                    updateDatabase(currentItem);
                    break;

                case "Delivery":
                    currentItem.setStage("Dilevered");
                    moveDocumentToDeliveredItems(currentItem);
                    notifyDataSetChanged();
                    break;
            }
        }catch (Exception e){
            Toast.makeText(layoutInflater.getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
        // Notify the adapterOrderBook that data has changed
        notifyDataSetChanged();
    }

    private void AskToSendSMS(MoharDataModel currentItem) {

        try{
            String sms ="Your order is now ready, please come to our store and pick it up! Best wishes, Tarkeshwar Printing Press, Dumraon";

            String DeliveryMessage = "आपका ऑर्डर अब तैयार है, कृपया हमारे स्टोर पर आ जाएं और उसे ले जाए!  \uD83D\uDECD\uFE0F\uD83C\uDF89 \n\n" +
                "शुभकामनाएँ \n"+
                "तारकेश्वर प्रिंटिंग प्रेस, डुमराँव";

            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            builder.setTitle("Choose Communication Method")
                    .setMessage("Inform Customer to Pick Up")
                    .setPositiveButton("SMS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle SMS option
                            SendSMS smsSender = new SendSMS(layoutInflater.getContext());
                            smsSender.sendSms(currentItem.getMobileNo(), sms);

                        }
                    })
                    .setNegativeButton("WhatsApp", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle WhatsApp option
                            SendWhatsAppSMS whatsAppSMS = new SendWhatsAppSMS();
                            whatsAppSMS.startWhatsAppChat(layoutInflater.getContext(), currentItem.getMobileNo(), DeliveryMessage);

                        }
                    })
                    .setNeutralButton("Both", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle the case where no message is sent
                            SendSMS smsSender = new SendSMS(layoutInflater.getContext());
                            smsSender.sendSms(currentItem.getMobileNo(), sms);

                            SendWhatsAppSMS whatsAppSMS = new SendWhatsAppSMS();
                            whatsAppSMS.startWhatsAppChat(layoutInflater.getContext(), currentItem.getMobileNo(), DeliveryMessage);
                        }
                    })
                    .setCancelable(false)
                    .show();


        }catch (Exception e){
            Toast.makeText(layoutInflater.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    // Update the phase in the database (replace this with your actual database update code)
    private void updateDatabase(MoharDataModel currentItem) {

        try {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection(MyDB.Collections).document(currentItem.getGetId());
            //FirebaseFirestore.getInstance().collection("your_collection").document(documentId);
            docRef.update("stage", currentItem.getStage())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Database update successful
                            notifyDataSetChanged(); // Notify RecyclerView
                            Toast.makeText(layoutInflater.getContext(), "updated" , Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Toast.makeText(layoutInflater.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){

            Toast.makeText(layoutInflater.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }



    private void showYesNoDialog(MoharDataModel currentItem) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            builder.setTitle("Are you sure?");
            builder.setMessage(currentItem.getStage() + " Has Been Completed ?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked Yes
                    // Perform your action here
                    ChangeStage(currentItem);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked No
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Exception e){
            Toast.makeText(layoutInflater.getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void startPhoneCall(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    private void startWhatsAppChat(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=91" + phoneNumber;
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            // Handle case where WhatsApp is not installed
            Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show();
        }
    }


}
