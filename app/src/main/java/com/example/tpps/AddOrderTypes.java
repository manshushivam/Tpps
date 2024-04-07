package com.example.tpps;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tpps.Config.MyDB;
import com.example.tpps.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddOrderTypes extends BottomSheetDialogFragment {
    EditText orderTypes;
    private FirebaseFirestore db;
    CircularProgressIndicator cpi;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_order_types, container, false);
        orderTypes = view.findViewById(R.id.outlinedTextFiedOrderType);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
//        cpi = view.findViewById(R.id.linearIndicatorAddTypes);
        view.findViewById(R.id.btn_AddOrderType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrderType();
            }
        });
    }

    private void addOrderType() {

        String orderType = orderTypes.getText().toString().trim();

//        cpi.setVisibility(View.VISIBLE);
//        cpi.setIndeterminate(true);

        if (!orderType.isEmpty()) {
            Map<String, Object> orderTypeMap = new HashMap<>();
            orderTypeMap.put("orderType", orderType);

            db.collection(MyDB.OrderTypes)
                    .add(orderTypeMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Toast.makeText(getActivity(), "Order type added successfully", Toast.LENGTH_SHORT).show();
                            dismiss(); // Dismiss the bottom sheet after successful addition

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(), "Error adding order type: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Please enter order type", Toast.LENGTH_SHORT).show();
        }
    }
}
