package com.example.tpps.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tpps.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullScreenPDFViwer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_pdfviwer);
        String pdfFilePath = getIntent().getStringExtra("PDFFilePath");
        PDFView pdfView =(PDFView) findViewById(R.id.pdfView);
        File pdfFile = new File(pdfFilePath);

        pdfView.fromFile(pdfFile)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .defaultPage(0)
                // Add other configurations as needed
                .load();

    }
}