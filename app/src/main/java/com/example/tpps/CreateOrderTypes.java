package com.example.tpps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tpps.Config.MyDB;
import com.example.tpps.adapter.AdapterReadOrder;
import com.example.tpps.adapter.AdapterReadOrderTypes;
import com.example.tpps.dataModel.MoharDataModel;
import com.example.tpps.dataModel.OrderTypesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateOrderTypes extends AppCompatActivity {


    private int selectedRadioButtonId;

    ImageView image;
    Button btnCamera;
    private String currentPhotoPath;
    private String ImageURL;
    FloatingActionButton fabOrderTypes;
    private FirebaseFirestore db;
    private List<OrderTypesModel> dataList;

    private RecyclerView recyclerView;
    AdapterReadOrderTypes adapterReadOrderTypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order_types);

        //RadioGroup radioGroupOrderTypes = findViewById(R.id.radioGroupOrderTypes);
        image = findViewById(R.id.imageView_bigCOT);
        btnCamera = findViewById(R.id.button_uploadImageCOT);
        btnCamera.setOnClickListener(v -> {
            if(isNetworkConnected()) {
                try {
                    String fileName = "photo";
                    File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();
                    Uri imageURI = FileProvider.getUriForFile(CreateOrderTypes.this, "com.example.tpps.fileprovider", imageFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Please turn on Internet Data", Toast.LENGTH_LONG).show();
            }
        });

//        radioGroupOrderTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId != -1) {
//                    RadioButton selectedRadioButton = findViewById(checkedId);
//                    // Replace the Intent with showing the Bottom Sheet
//                    showCalendarBottomSheet(selectedRadioButton.getText().toString().trim());
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please select an order type", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        db = FirebaseFirestore.getInstance();
        ReadOrderFromFirestore();
        fabOrderTypes = findViewById(R.id.fabAddOrderTypes);
        fabOrderTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderTypesBottomSheet();
            }
        });

    }
    public void showCalendarBottomSheet(FragmentManager fragmentManager, String orderType) {
        CreateOrderCalender bottomSheetFragment = new CreateOrderCalender();
        // Pass data to the Bottom Sheet Fragment using arguments
        Bundle args = new Bundle();
        args.putString("orderType", orderType);
        args.putString("ImageURL", ImageURL);
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
    }

    private void showOrderTypesBottomSheet() {
        AddOrderTypes bottomSheetFragment = new AddOrderTypes();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

    }
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
                Toast.makeText(getApplicationContext(), "Failed To Upload Image", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
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
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
//    private void ReadOrderFromFirestore(){
//
//        db.collection(MyDB.OrderTypes).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        dataList = new ArrayList<>();
//                        if(task.isSuccessful()){
//                            for (QueryDocumentSnapshot document : task.getResult()){
//                                OrderTypesModel data = new OrderTypesModel(
//                                        document.getString("orderType")
//                                );
//                                dataList.add(data);
//                            }
//                            Toast.makeText(getApplicationContext(), dataList.size(), Toast.LENGTH_SHORT).show();
//
//
//                            // Sort dataList based on the shortest duration between order date and due date
//                            recyclerView = findViewById(R.id.recyclerViewOrderTypes);
//                            recyclerView.setLayoutManager(new LinearLayoutManager(CreateOrderTypes.this));
//                            adapterReadOrderTypes = new AdapterReadOrderTypes(CreateOrderTypes.this, dataList);
//                            recyclerView.setAdapter(adapterReadOrderTypes);
//
//                        }
//
//                        else{
//                            Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    private void ReadOrderFromFirestore(){
        dataList = new ArrayList<>(); // Initialize dataList here

        db.collection(MyDB.OrderTypes).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                OrderTypesModel data = new OrderTypesModel(
                                        document.getString("orderType")
                                );
                                dataList.add(data);
                            }

                            // Sort dataList based on the shortest duration between order date and due date
                            recyclerView = findViewById(R.id.recyclerViewOrderTypes);
                            recyclerView.setLayoutManager(new LinearLayoutManager(CreateOrderTypes.this));
                            adapterReadOrderTypes = new AdapterReadOrderTypes(CreateOrderTypes.this, dataList, getSupportFragmentManager());

                            recyclerView.setAdapter(adapterReadOrderTypes);
                            recyclerView.setLayoutManager(new LinearLayoutManager(CreateOrderTypes.this));

                        } else {
                            Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}