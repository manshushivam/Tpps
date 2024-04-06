package com.example.tpps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button createOrder;

    private Button readOrder;

    private  Button DeliveredItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createOrder = findViewById(R.id.button_createOrder);
        createOrder.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                if(isNetworkConnected()){
                Intent intent = new Intent(MainActivity.this, CreateOrderTypes.class);
                startActivity(intent);
            }
            }
        });


        readOrder = findViewById(R.id.button_readOrder);
        readOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , ReadOrder.class);
                startActivity(intent);
            }
        });


    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}