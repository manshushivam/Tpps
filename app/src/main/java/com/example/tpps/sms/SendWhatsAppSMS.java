package com.example.tpps.sms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.net.URLEncoder;

public class SendWhatsAppSMS {
    public void startWhatsAppChat(Context context, String phoneNumber, String message) {
        try {
            String url = "https://api.whatsapp.com/send?phone=91" + phoneNumber + "&text=" + URLEncoder.encode(message, "UTF-8");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);

        } catch (Exception e) {
            // Handle case where WhatsApp is not installed
            Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendPdfThroughWhatsApp(Context context, String phoneNumber, String pdfPath) {
        try {
            // Create a new Intent with the action SEND
            Intent sendIntent = new Intent("android.intent.action.SEND");

            // Set the MIME type of the content to "application/pdf"
            sendIntent.setType("application/pdf");

            // Set the path of the PDF file
            File file = new File(pdfPath);

            // Correct authority used here
            Uri uri = FileProvider.getUriForFile(context, "com.example.tpps.pdfprovider", file);

            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

            // Set the recipient's phone number (optional)
            sendIntent.putExtra("jid", phoneNumber + "@s.whatsapp.net");

            // Set the package to WhatsApp
            sendIntent.setPackage("com.whatsapp");

            // Grant temporary read permission to the content URI
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the activity
            context.startActivity(sendIntent);
        } catch (Exception e) {
            // Handle exceptions, e.g., if WhatsApp is not installed
            Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
