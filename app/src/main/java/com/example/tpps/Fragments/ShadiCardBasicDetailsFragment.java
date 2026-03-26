package com.example.tpps.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tpps.Activity.ShadiCardOrderActivity;
import com.example.tpps.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ShadiCardBasicDetailsFragment extends Fragment {

    private TextInputEditText editTextGroomName;
    private TextInputEditText editTextBrideName;
    private TextInputEditText editTextGroomParentsName;
    private TextInputEditText editTextBrideParentsName;
    private MaterialButton buttonNext;

    public ShadiCardBasicDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shadi_card_basic_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextGroomName = view.findViewById(R.id.editTextGroomName);
        editTextBrideName = view.findViewById(R.id.editTextBrideName);
        editTextGroomParentsName = view.findViewById(R.id.editTextGroomParentsName);
        editTextBrideParentsName = view.findViewById(R.id.editTextBrideParentsName);
        buttonNext = view.findViewById(R.id.buttonNext);

        buttonNext.setOnClickListener(v -> {
            // For now, just show a toast with the collected data
            String groomName = editTextGroomName.getText().toString().trim();
            String brideName = editTextBrideName.getText().toString().trim();
            String groomParents = editTextGroomParentsName.getText().toString().trim();
            String brideParents = editTextBrideParentsName.getText().toString().trim();

            if (groomName.isEmpty() || brideName.isEmpty() || groomParents.isEmpty() || brideParents.isEmpty()) {
                Toast.makeText(getContext(), "कृपया सभी विवरण भरें।", Toast.LENGTH_SHORT).show(); // Please fill all details
            } else {
                Toast.makeText(getContext(), "नाम: " + groomName + ", दुल्हन: " + brideName, Toast.LENGTH_SHORT).show();

                // In a real scenario, you would pass this data to the next fragment
                // For now, let's navigate to a placeholder next fragment.
//                if (getActivity() instanceof ShadiCardOrderActivity) {
//                    ((ShadiCardOrderActivity) getActivity()).navigateToFragment(new ShadiCardWeddingDetailsFragment());
//                }
            }
        });
    }
}