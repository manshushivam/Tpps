package com.example.tpps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpps.Config.MyDB;
import com.example.tpps.adapter.AdapterReadOrder;
import com.example.tpps.dataModel.MoharDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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

public class ReadOrder extends AppCompatActivity {

    int computerWorkCounter = 0;
    int printingWorkCounter = 0;
    int deliveryCounter = 0;

    private FirebaseFirestore db;
    private List<MoharDataModel> dataList;

    private EditText editTextSearch;
    private RadioGroup radioGroup;

    private RadioGroup stageRadioGroup;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    AdapterReadOrder adapterReadOrder;

    TextView ComputerWork;
    TextView PrintingWork;
    TextView Delivery;
    LinearProgressIndicator Lpi;

    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_read_order);
            db = FirebaseFirestore.getInstance();
            dataList = new ArrayList<>();
            ReadOrderFromFirestore();
            ComputerWork = findViewById(R.id.radioButton_Self_ComputerWork);
            PrintingWork = findViewById(R.id.radioButton_PrintingWork);
            Delivery = findViewById(R.id.radioButton_Deliver);

            Lpi = findViewById(R.id.linearIndicator);
            radioGroup = findViewById(R.id.radioGroup);
            stageRadioGroup = findViewById(R.id.radioGroupStage);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = findViewById(checkedId);
                    if (radioButton != null) {
                        // Update the filter when a radio button is selected
                        adapterReadOrder.setFilterByOrderType(radioButton.getText().toString());
                    }
                }
            });

            stageRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = findViewById(checkedId);
                    if (radioButton != null) {
                        // Update the filter when a radio button is selected
                        String originalString = radioButton.getText().toString();
                        String strippedString = originalString.substring(0, originalString.length() - 4);
                        System.out.println("Stripped String: " + strippedString);
                        adapterReadOrder.setFilterByStage(strippedString);
                    }
                }
            });


            searchView = findViewById(R.id.searchView);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Handle search query submission if needed
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Handle search query changes
                    adapterReadOrder.filterByMobileNumber(newText);
                    return true;
                }
            });

            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Initialize swipeRefreshLayout
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Perform refresh operations here
                    // Fetch bills from Firestore
                    dataList.clear(); // Clear existing data
                    adapterReadOrder.notifyDataSetChanged(); // Notify the adapter about the data change
                    ReadOrderFromFirestore();
                    // Stop the refresh animation
                    swipeRefreshLayout.setRefreshing(false);

                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        // Uncheck the currently checked RadioButton
                        radioGroup.clearCheck();
                    }


                    if (stageRadioGroup.getCheckedRadioButtonId() != -1) {
                        // Uncheck the currently checked RadioButton
                        stageRadioGroup.clearCheck();
                    }


                }
            });


        }catch (Exception E){
            Toast.makeText(getApplicationContext(), E.getMessage() , Toast.LENGTH_SHORT).show();
        }

    }


    private void ReadOrderFromFirestore(){

        db.collection(MyDB.Collections).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String stage = "";
                                MoharDataModel data = new MoharDataModel(
                                        document.getString("name"),
                                        document.getString("address"),
                                        document.getString("imageUrl"),
                                        document.getString("orderDate"),
                                        document.getString("dueDate"),
                                        document.getString("orderType"),
                                        document.getString("mobileNo"),
                                        document.getString("content"),
                                        document.getString("totalAmount"),
                                        document.getString("paidAmount"),
                                        stage = document.getString("stage"),
                                        document.getId()
                                );
                                switch (stage) {
                                    case "Computer Work":
                                        computerWorkCounter++;
                                        break;
                                    case "Printing Work":
                                        printingWorkCounter++;
                                        break;
                                    case "Delivery":
                                        deliveryCounter++;
                                        break;
                                    default:
                                        System.out.println("Unknown stage: " + stage);
                                }
                                dataList.add(data);
                            }
                           // Toast.makeText(getApplicationContext(), "Total Pending Work : "+ (computerWorkCounter+printingWorkCounter), Toast.LENGTH_SHORT).show();
                            ComputerWork.setText("Computer Work (" + computerWorkCounter+ ")");
                            PrintingWork.setText("Printing Work (" + printingWorkCounter+ ")");
                            Delivery.setText("Delivery (" + deliveryCounter+ ")");

                            // Sort dataList based on the shortest duration between order date and due date
                            Collections.sort(dataList, new Comparator<MoharDataModel>() {
                                @Override
                                public int compare(MoharDataModel o1, MoharDataModel o2) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                                    try {
                                        Date dueDate1 = dateFormat.parse(o1.getDueDate());
                                        Date dueDate2 = dateFormat.parse(o2.getDueDate());

                                        // Sort in ascending order based on due date (nearest due date first)
                                        return dueDate1.compareTo(dueDate2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    return 0;
                                }
                            });

                            recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ReadOrder.this));
                            adapterReadOrder = new AdapterReadOrder(ReadOrder.this, dataList);
                            recyclerView.setAdapter(adapterReadOrder);
                            Lpi.setIndeterminate(false);
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}