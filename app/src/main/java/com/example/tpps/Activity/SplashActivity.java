package com.example.tpps.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.tpps.MainActivity;
import com.example.tpps.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //this will take from SplashActivity to the MainActivity
        Intent iHome = new Intent(SplashActivity.this , MainActivity.class);

        //A thread to run main activity and splash activity at same time.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(iHome);
                //to not come back to the splash activity we will use finish method
                finish();
            }
        }, 1500);
    }
}