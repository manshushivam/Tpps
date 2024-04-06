package com.example.tpps;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateOrderCalender extends BottomSheetDialogFragment {

    private String imageURL;
    private String orderType;



    public CreateOrderCalender() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_create_order_calender, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        // Set the default date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        // Set listener for date change
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Format the selected date
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            String selectedDateString = sdf.format(selectedDate.getTime());

            // Pass the selected date string to the CreateOrder activity
            Intent intent = new Intent(requireActivity(), CreateOrder.class);
            intent.putExtra("selectedDate", selectedDateString);
            intent.putExtra("orderType", orderType);
            intent.putExtra("ImageURL", imageURL);
            startActivity(intent);
            dismiss(); // Dismiss the Bottom Sheet after starting the new activity
        });

        // Retrieve data from the arguments bundle
        Bundle args = getArguments();
        if (args != null) {
            imageURL = args.getString("ImageURL");
            orderType = args.getString("orderType");
        }
    }
}
