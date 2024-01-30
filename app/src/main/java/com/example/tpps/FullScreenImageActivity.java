package com.example.tpps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {

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

        // Load the image into the full-screen ImageView

    }
}