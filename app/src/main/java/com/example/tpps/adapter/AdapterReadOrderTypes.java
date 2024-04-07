package com.example.tpps.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpps.CreateOrderCalender;
import com.example.tpps.CreateOrderTypes;
import com.example.tpps.R;
import com.example.tpps.dataModel.OrderTypesModel;

import java.util.List;

public class AdapterReadOrderTypes extends RecyclerView.Adapter<AdapterReadOrderTypes.ViewHolder> {

    private Context context;
    private List<OrderTypesModel> dataList;
    private String selectedOrderType = "";


    private FragmentManager fragmentManager;

    public AdapterReadOrderTypes(Context context, List<OrderTypesModel> dataList , FragmentManager fragmentManager) {
        this.context = context;
        this.dataList = dataList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderTypesModel data = dataList.get(position);
        holder.radioButton.setText(data.getOrderType());
        holder.radioButton.setChecked(selectedOrderType.equals(data.getOrderType()));

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOrderType = data.getOrderType();
                notifyDataSetChanged();
                // Show the bottom sheet here
                try {
                    CreateOrderTypes cot = new CreateOrderTypes();
                    cot.showCalendarBottomSheet(fragmentManager, selectedOrderType);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public String getSelectedOrderType() {
        return selectedOrderType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radioButtonOrderTypes);
        }
    }
}
