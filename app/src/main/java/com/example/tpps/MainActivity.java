package com.example.tpps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button anyOrder;
    private Button mohar;

    private Button moharBook;

    private  Button DeliveredItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        DeliveredItems = findViewById(R.id.deliveredItems);
//        DeliveredItems.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this , DeliveredItems.class);
//                startActivity(intent);
//            }
//        });
//
//
//        anyOrder = findViewById(R.id.anyOrder_button);
//        anyOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // transfering this to mohar class will see it later.
//                Intent intent = new Intent(MainActivity.this , mohar.class);
//                startActivity(intent);
//            }
//        });


        mohar = findViewById(R.id.button_mohar);
        mohar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , mohar.class);
                startActivity(intent);
            }
        });


        moharBook = findViewById(R.id.button_moharBook);
        moharBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , MoharBook.class);
                startActivity(intent);
            }
        });


    }
}