package com.example.tpps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tpps.sms.SendSMS;
import com.example.tpps.sms.SendWhatsAppSMS;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CashMemo extends AppCompatActivity {

    ImageView image;
    Button btnCamera;

    EditText moharContent;
    Button submit;

    EditText dateText;
    Button btnChangeDate;

    EditText totalAmount;
    EditText paidAmount;
    EditText mobileNo;
    private String currentPhotoPath;

    String orderDate;

    private String ImageURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_memo);

        image = findViewById(R.id.imageView_big);
        btnCamera = findViewById(R.id.button_uplodImage);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fileName = "photo";
                    File    storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File imageFile = File.createTempFile(fileName, ".jpg" , storageDirectory);


                    currentPhotoPath = imageFile.getAbsolutePath();
                    Uri imageURI = FileProvider.getUriForFile(CashMemo.this , "com.example.tpps.fileprovider" , imageFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    startActivityForResult(intent , 1)  ;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        //date start
        dateText = findViewById(R.id.editText_dateMohar);
        btnChangeDate = findViewById(R.id.button_dateMohar);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        updateEditTextDate(calendar.getTime());

        // Set OnClickListener for the Change Date button
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        //oder date

//        Calendar anotherCalendar = Calendar.getInstance();
//        anotherCalendar.add(Calendar.DAY_OF_MONTH, 0);
//        orderDate = anotherCalendar.getTime();
        //date end

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date currentDate = new Date();
        String formattedDate = sdf.format(currentDate);
        orderDate = formattedDate.toString();


        moharContent = findViewById(R.id.editTextMultiLine_moharContent);
        mobileNo = findViewById(R.id.editText_mobileNumber_mohar);
        totalAmount = findViewById(R.id.editText_totalAmountofMohar);
        paidAmount = findViewById(R.id.editText_paidAmountofMohar);


        submit = findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    CreateOrder(v);
                }else
                Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_LONG).show();
            }
        });

    }

    
    private void CreateOrder(View v) {
        try {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Create a new user with a first and last name
            Map<String, Object> Mohar = new HashMap<>();
            //Mohar.put("invoiceNo" , invoiceNO);
            Mohar.put("imageUrl", ImageURL);
            Mohar.put("content", moharContent.getText().toString().trim());
            Mohar.put("mobileNo", mobileNo.getText().toString().trim());
            Mohar.put("dueDate", dateText.getText().toString().trim());
            Mohar.put("orderDate", orderDate.toString().trim());
            Mohar.put("totalAmount", totalAmount.getText().toString().trim());
            Mohar.put("paidAmount", paidAmount.getText().toString().trim());
            Mohar.put("stage", "Start");


            // Add a new document with a generated ID
            db.collection("Mohars")
                    .add(Mohar)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Order Submitted", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error saving image URL to Firestore", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "Error adding document", e);
                        }
                    });


            String message = "Thank you for your order at Tarkeshwar Printing Press, Dumraon !\n"
                    + "Your items will be ready by " + dateText.getText().toString().trim() + "\n"
                    + "Total amount: " + totalAmount.getText().toString().trim() + "\n"
                    + "Amount you have paid: " + paidAmount.getText().toString().trim();

//                    String company = "Tarkeshwar Printing Press, Dumraon "; // Replace with your actual company name

            String namasteEmoji = "\uD83D\uDE4F"; // Namaste emoji
            String smileyEmoji = "\uD83D\uDE0A"; // Smiley emoji

//                    String message2 = namasteEmoji + " Namaste!\n" +
//                            "Thank you for choosing " + company + ". We appreciate your order!\n\n" +
//                            "Your items will be ready by *" + dateText.getText().toString().trim() + "*\n" +
//                            "Total amount: " + totalAmount.getText().toString().trim() + "\n" +
//                            "Amount you have paid: " + paidAmount.getText().toString().trim() + "\n\n" +
//                            "If you have any questions or need further assistance, feel free to contact us.\n" +
//                            "We look forward to serving you! " + smileyEmoji + "\n\n" +
//                            "Best regards,\n" +
//                            company;

            String compnayNameInHindi = "तारकेश्वर प्रिंटिंग प्रेस, डुमराँव";

            String HindiMessage = " नमस्ते!" + namasteEmoji + "\n" +
                    compnayNameInHindi + " में पधारने के लिए धन्यवाद।\n\n" +
                    "आपका ऑर्डर  *" + dateText.getText().toString().trim() + "* तक तैयार हो जाएगा ।\n" +
                    "जिसका कुल राशि: *₹" + totalAmount.getText().toString().trim() + "*\n" +
                    "आपने जमा किया राशि: *₹" + paidAmount.getText().toString().trim() + "*\n\n" +
                    "यदि आपके पास कोई सवाल या सहायता की आवश्यकता है, तो हमसे संपर्क करें।\n" +
                    "+919572088920\n" +
                    "हम आपकी सेवा करने के लिए तत्पर हैं! " + smileyEmoji + "\n\n" +
                    "शुभकामनाएँ,\n" +
                    compnayNameInHindi;

            SendSMS smsSender = new SendSMS(getApplicationContext());
            smsSender.sendSms(mobileNo.getText().toString(), message);

            SendWhatsAppSMS whatsAppSMS = new SendWhatsAppSMS();
            whatsAppSMS.startWhatsAppChat(v.getContext(), mobileNo.getText().toString(), HindiMessage);


            finish();

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Error in submitting", Toast.LENGTH_LONG).show();
        }
    }



    private boolean validateFields() {
        String content = moharContent.getText().toString().trim();
        String userMobileNo = mobileNo.getText().toString().trim();
        String orderDate = dateText.getText().toString().trim();
        String amount = totalAmount.getText().toString().trim();
        String paidAmountValue = paidAmount.getText().toString().trim();

        return !content.isEmpty() &&
                isValidMobileNumber(userMobileNo) &&
                !orderDate.isEmpty() &&
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


    private void updateEditTextDate(Date date) {
        // Format the date and set it to the EditText
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        dateText.setText(sdf.format(date));
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog with the current date and a listener to get the selected date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the EditText with the selected date
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        updateEditTextDate(selectedDate.getTime());
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );


        calendar.add(Calendar.DAY_OF_MONTH, 0);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    //date end

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            if (requestCode == 1) {
                Bitmap bitmap2 = BitmapFactory.decodeFile(currentPhotoPath);
                image.setImageBitmap(bitmap2); // Use the 'image' variable instead of creating a new one

                // Optionally, you may want to add the following lines to notify the gallery about the new image
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);

            }
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();


        //image to bitmap
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();


        //upload image

        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child("images/" + imageName);


        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg") // Set content type to JPEG
                .setCustomMetadata("quality", "original") // Custom metadata to indicate original quality
                .build();




        UploadTask uploadTask = imageRef.putBytes(imageData);



        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful upload
                Toast.makeText(getApplicationContext(), "Error uploading image", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful upload
                Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        // Save Image URL to Firestore (or your database)
                        // Save Image URL to Firestore (or your database)
                       ImageURL = downloadUrl.toString();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to get download URL
                        Toast.makeText(getApplicationContext(), "Error getting download URL", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });





    }


}

