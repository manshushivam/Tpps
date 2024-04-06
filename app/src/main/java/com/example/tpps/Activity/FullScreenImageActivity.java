package com.example.tpps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {

    Button download ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);


        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);

        // Retrieve the image URL passed from the previous activity
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        if(imageUrl != null ){
            Picasso.get().load(imageUrl).fit().centerInside().into(fullScreenImageView);
        }else{
            Toast.makeText(getApplicationContext(), "Image Not available", Toast.LENGTH_SHORT).show();
        }
    }
}