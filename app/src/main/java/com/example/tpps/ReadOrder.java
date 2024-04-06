package com.example.tpps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.tpps.adapter.AdapterOrderBook;
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

public class OrderBook extends AppCompatActivity {
    private FirebaseFirestore db;
    private List<MoharDataModel> dataList;

    RecyclerView recyclerView;
    AdapterOrderBook adapterOrderBook;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_book);

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
                                        document.getString("dueDate"),
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


                            // Sort dataList based on the shortest duration between order date and due date
                            Collections.sort(dataList, new Comparator<MoharDataModel>() {
                                @Override
                                public int compare(MoharDataModel o1, MoharDataModel o2) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                                    try {
                                        Date orderDate1 = dateFormat.parse(o1.getOrderDate());
                                        Date dueDate1 = dateFormat.parse(o1.getDueDate());

                                        Date orderDate2 = dateFormat.parse(o2.getOrderDate());
                                        Date dueDate2 = dateFormat.parse(o2.getDueDate());

                                        // Calculate durations
                                        long duration1 = dueDate1.getTime() - orderDate1.getTime();
                                        long duration2 = dueDate2.getTime() - orderDate2.getTime();

                                        // Sort in ascending order based on duration (shortest duration first)
                                        return Long.compare(duration1, duration2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    return 0;
                                }
                            });




                            recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(OrderBook.this));
                            adapterOrderBook = new AdapterOrderBook(OrderBook.this, dataList);
                            recyclerView.setAdapter(adapterOrderBook);
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }



}