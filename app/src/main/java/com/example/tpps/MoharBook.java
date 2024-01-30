package com.example.tpps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpps.dataModel.MoharDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoharBook extends AppCompatActivity {
    private FirebaseFirestore db;
    private List<MoharDataModel> dataList;

    RecyclerView recyclerView;
    Adapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mohar_book);

        db = FirebaseFirestore.getInstance();

        dataList = new ArrayList<>();

        db.collection("Mohars").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                MoharDataModel data = new MoharDataModel(
                                        document.getString("imageUrl"),
                                        document.getString("orderDate"),
                                        document.getString("mobileNo"),
                                        document.getString("content"),
                                        document.getString("totalAmount"),
                                        document.getString("paidAmount"),
                                        document.getString("stage"),
                                        document.getId()
                                );

                                dataList.add(data);
                            }
                            Toast.makeText(getApplicationContext(), "Total Pending Work : "+dataList.size() , Toast.LENGTH_SHORT).show();


                            // Sort dataList based on order date in descending order
                            Collections.sort(dataList, new Comparator<MoharDataModel>() {
                                @Override
                                public int compare(MoharDataModel o1, MoharDataModel o2) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

                                    try {
                                        Date date1 = dateFormat.parse(o1.getOrderDate());
                                        Date date2 = dateFormat.parse(o2.getOrderDate());

                                        // Sort in descending order
                                        if (date1 != null && date2 != null) {
                                            return date2.compareTo(date1);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    return 0;
                                }
                            });



                            recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MoharBook.this));
                            adapter = new Adapter(MoharBook.this, dataList);
                            recyclerView.setAdapter(adapter);
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }



}