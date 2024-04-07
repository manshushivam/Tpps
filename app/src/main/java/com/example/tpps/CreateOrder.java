package com.example.tpps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tpps.Config.MyDB;
import com.example.tpps.pdf.CreatePdf;
import com.example.tpps.sms.SendSMS;
import com.example.tpps.sms.SendWhatsAppSMS;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CreateOrder extends AppCompatActivity {

    EditText nameOfCustomer;
    EditText address;
    EditText orderContent;
    EditText mobileNo;
    EditText totalAmount;
    EditText paidAmount;
    Button submit;
    String orderDate;
    String dueDate;
    String imageURL;
    String orderType;

    String InvoiceNo;
    CircularProgressIndicator cpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_order);

            imageURL = getIntent().getStringExtra("ImageURL");
            orderType = getIntent().getStringExtra("orderType");
            dueDate = getIntent().getStringExtra("selectedDate");
            nameOfCustomer = findViewById(R.id.outlinedTextFieldNameCO);
            address = findViewById(R.id.outlinedTextField_AddressCO);
            orderContent = findViewById(R.id.outlinedTextFieldContentCO);
            mobileNo = findViewById(R.id.outlinedTextFieldMobileCO);
            totalAmount = findViewById(R.id.outlinedTextFieldTotalAmountCO);
            paidAmount = findViewById(R.id.outlinedTextFieldPaidAmountCO);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            orderDate = sdf.format(calendar.getTime());

            Timestamp timestamp = Timestamp.now();
            SimpleDateFormat sdff = new SimpleDateFormat("ssmmHHddMMyy", Locale.getDefault());
            InvoiceNo = sdff.format(timestamp.toDate());

            submit = findViewById(R.id.button_submit);
            submit.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    if (validateFields()) {


                        try {

                             cpi = findViewById(R.id.circularIndicatorPro);
                             cpi.setIndeterminate(true);


                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> order = new HashMap<>();
                            //order.put("invoiceNo" , invoiceNO);
                            order.put("imageUrl", imageURL);
                            order.put("name", nameOfCustomer.getText().toString().trim());
                            order.put("address" , address.getText().toString().trim());
                            order.put("orderType", orderType);
                            order.put("dueDate", dueDate) ;
                            order.put("orderDate", orderDate);
                            order.put("content", orderContent.getText().toString().trim());
                            order.put("mobileNo", mobileNo.getText().toString().trim());
                            order.put("totalAmount", totalAmount.getText().toString().trim());
                            order.put("paidAmount", paidAmount.getText().toString().trim());
                            order.put("stage", "Computer Work");


                            db.collection(MyDB.Collections).document(InvoiceNo).set(order)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            cpi.setIndeterminate(false);
                                            Toast.makeText(getApplicationContext(), "Order Submitted", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + InvoiceNo);

                                            sendsms(v);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            cpi.setIndeterminate(false);
                                            Toast.makeText(getApplicationContext(), "Error saving image URL to Firestore", Toast.LENGTH_LONG).show();
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        }
                        catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error in submitting", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error message", e);

        }
    }

    private boolean validateFields() {
        String name = nameOfCustomer.getText().toString().trim();
        String content = orderContent.getText().toString().trim();
        String userMobileNo = mobileNo.getText().toString().trim();
        String amount = totalAmount.getText().toString().trim();
        String paidAmountValue = paidAmount.getText().toString().trim();

        return !name.isEmpty() &&
                !content.isEmpty() &&
                isValidMobileNumber(userMobileNo)&&
                !amount.isEmpty() &&
                !paidAmountValue.isEmpty();
    }
    private boolean isValidMobileNumber(String mobileNumber) {
        // Check if the mobile number is exactly 10 digits
        if(!mobileNumber.isEmpty() && mobileNumber.length() == 10 && mobileNumber.matches("\\d{10}")){
            return true;
        }else{
            Toast.makeText(getApplicationContext() , "Incorrect Mobile No" , Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    private void sendsms(View v){
        String totalAmountString =totalAmount.getText().toString().trim();
        String paidAmountString =paidAmount.getText().toString().trim();

        int totalAmount = Integer.parseInt(totalAmountString);
        int paidAmount = Integer.parseInt(paidAmountString);

        int dueAmount = totalAmount - paidAmount;
        String dueAmountString = String.valueOf(dueAmount);

        String namasteEmoji = "\uD83D\uDE4F"; // Namaste emoji
        String smileyEmoji = "\uD83D\uDE0A"; // Smiley emoji

        String compnayNameInHindi = "तारकेश्वर प्रिंटिंग प्रेस, डुमराँव";



        String sms = "नमस्ते! तारकेश्वर प्रिंटिंग प्रेस, डुमराँव में पधारने के लिए धन्यवाद!\n"
                + "आपका " + orderType + " का ऑर्डर \n"
                + "वह" + dueDate + " तक तैयार हो जाएगा।\n"
                + "कुल राशि: " + totalAmountString+ "\n"
                + "आपने जमा किया राशि: " +paidAmountString+ "\n"
                + "बकाया राशि: " + dueAmountString;

        String engsms = "Namstey! \n"
                + "Your " + orderType + " order\n"
                + "will be ready by " + dueDate + ".\n"
                + "Total amount: " + totalAmountString + "\n"
                + "Amount paid: " + paidAmountString + "\n"
                + "Amount due: " + dueAmountString;



        String Wsms = " नमस्ते!" + namasteEmoji +"\n" +
                compnayNameInHindi + " में पधारने के लिए धन्यवाद।\n" +
                "--------------------------------------------------------" + "\n\n"+

                "आपका *"+ orderType +"* का ऑर्डर \n " +
                "जिसका विवरण है - \n *"+orderContent.getText().toString().trim()+ "* \n\n "+


                "                                             कुल राशि: *₹" + totalAmountString + "*\n" +
                "                                             जमा राशि: *₹" + paidAmountString + "*\n" +
                "                                       ---------------------------" + "\n"+
                "                                          बकाया राशि: *₹" + dueAmountString + "*\n"+
                "                                       ---------------------------" + "\n"+
                "वह लगभग *"+ dueDate + "* तक तैयार हो जाएगा ।\n\n" +
                "यदि आपके पास कोई सवाल या सहायता की आवश्यकता है, तो हमसे संपर्क करें।\n" +
                "+919572088920\n" +
                "हम आपकी सेवा करने के लिए तत्पर हैं! " + smileyEmoji + "\n\n" +
                "शुभकामनाएँ,\n" +
                compnayNameInHindi;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mobileNo.getText().toString(), null, engsms, null, null);
//        SendSMS smsSender = new SendSMS(getApplicationContext());
//        smsSender.sendSms(mobileNo.getText().toString(), sms);

        SendWhatsAppSMS whatsAppSMS = new SendWhatsAppSMS();
        whatsAppSMS.startWhatsAppChat(v.getContext(), mobileNo.getText().toString(), Wsms);

        //startMainActivityDelayed();

    }
}