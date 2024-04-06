package com.example.tpps.adapter;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpps.Activity.FullScreenImageActivity;
import com.example.tpps.R;
import com.example.tpps.sms.SendSMS;
import com.example.tpps.sms.SendWhatsAppSMS;
import com.example.tpps.dataModel.MoharDataModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterOrderBook extends RecyclerView.Adapter<AdapterOrderBook.ViewHolder>  {

    private LayoutInflater layoutInflater;
    private List<MoharDataModel> dataList;
    private String filterOrderType;  // Variable to store the selected order type for filtering


    public AdapterOrderBook(Context context, List<MoharDataModel> dataList){
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.filterOrderType = ""; // Initialize filterOrderType
    }

    public void setFilterByOrderType(String orderType) {

        filterOrderType = orderType;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.items_card,viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MoharDataModel currentItem = dataList.get(position);

        if (filterOrderType.isEmpty() || currentItem.getOrderType().equalsIgnoreCase(filterOrderType)) {
            // Update the views with the data
            // ...
            holder.itemView.setVisibility(View.VISIBLE);  // Set visibility to visible
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            // If order type doesn't match, hide the view
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }






        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = currentItem.getImageUrl();
                if(imageUrl == null){
                    Toast.makeText(v.getContext(), "Image Not available", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(v.getContext(), FullScreenImageActivity.class);
                    intent.putExtra("IMAGE_URL", imageUrl);
                    v.getContext().startActivity(intent);
                }
            }
        });


        holder.moharContent.setText(currentItem.getContent());
        holder.invoiceNo.setText("Invoice No : " + currentItem.getGetId());
        holder.orderType.setText("Order Type : " +currentItem.getOrderType());
        holder.orderDate.setText("Order Date : " + currentItem.getOrderDate());
        holder.dueDate.setText("Due Date :    " + currentItem.getDueDate());
        holder.mobileNo.setText("Mobile No : " + currentItem.getMobileNo());
        holder.totalAmount.setText("Total : Rs " + currentItem.getTotalAmount());
        holder.paidAmount.setText("Paid : Rs " + currentItem.getPaidAmount());

        double totalAmount = Double.parseDouble(currentItem.getTotalAmount());
        double paidAmount = Double.parseDouble(currentItem.getPaidAmount());
        double dueAmount = totalAmount - paidAmount;
        holder.dueAmount.setText("Due: Rs " + dueAmount);


        holder.mobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle phone number click
                String phoneNumber = currentItem.getMobileNo();
                startPhoneCall(v.getContext(), phoneNumber);
            }
        });

        holder.stage.setText(currentItem.getStage());
        holder.setStageBackgroundColor(currentItem.getStage());
        holder.stage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                showYesNoDialog(currentItem);

               // Toast.makeText(v.getContext(), "pop" , Toast.LENGTH_SHORT).show();
            }
        });

        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = currentItem.getMobileNo();
                startWhatsAppChat(v.getContext(), phoneNumber);
                SendWhatsAppSMS whatsAppSMS = new SendWhatsAppSMS();
                whatsAppSMS.startWhatsAppChat(v.getContext(), phoneNumber , " ");
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();

    }



    // ... Other methods

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Declare views from your card layout

        public ImageView image;
        public TextView moharContent;

        public TextView invoiceNo ;
        public TextView orderType;
        public TextView orderDate;

        public TextView dueDate;
        public TextView mobileNo;
        public TextView totalAmount;
        public TextView paidAmount;

        public TextView dueAmount;

        public Button stage;

        public Button whatsapp;


        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize views from your card layout
            // textViewTitle = itemView.findViewById(R.id.textViewTitle);
            image = itemView.findViewById(R.id.imageView_moharBook);
            moharContent = itemView.findViewById(R.id.textContent_moharBook);
            invoiceNo = itemView.findViewById(R.id.textInvoiceNo_moharBook);
            orderType = itemView.findViewById(R.id.textOrderType_moharBook);
            orderDate = itemView.findViewById(R.id.textOrderDate_moharBook);
            dueDate = itemView.findViewById(R.id.textDueDate_moharBook);
            mobileNo = itemView.findViewById(R.id.textMobileNo_moharBook);
            totalAmount = itemView.findViewById(R.id.textTotalAmount_moharBook);
            paidAmount = itemView.findViewById(R.id.textPaidAmount_moharBook);
            dueAmount = itemView.findViewById(R.id.textDueAmount_moharBook);
            stage = itemView.findViewById(R.id.btnStage_moharBook);
            whatsapp = itemView.findViewById(R.id.button_Whatsapp);
        }

        public void setStageBackgroundColor(String s) {
            int colorResId;
            switch (s) {
                case "Start":
                    colorResId = R.color.startColor; // Replace with your color resource
                    break;
                case "Formating Done?":
                    colorResId = R.color.formatingColor; // Replace with your color resource
                    break;
                case "Printing Done?":
                    colorResId = R.color.printingColor; // Replace with your color resource
                    break;
                case "Dilevered":
                    colorResId = R.color.deliveredColor; // Replace with your color resource
                    break;
                default:
                    colorResId = R.color.defaultColor; // Replace with your default color resource
            }
                int color = ContextCompat.getColor(itemView.getContext(), colorResId);
                stage.setBackgroundColor(color);

        }

    }


    // Update the phase and move to "Delivered Items" collection in the database


    // Move document to "Delivered Items" collection
    private void moveDocumentToDeliveredItems(MoharDataModel currentItem) {
        try {
            // Get a reference to the document in the "Orders" collection
            DocumentReference sourceDocRef = FirebaseFirestore.getInstance().collection("Orders").document(currentItem.getGetId());
            // Get a reference to the destination document in the "Delivered Items" collection
            //DocumentReference destDocRef = FirebaseFirestore.getInstance().collection("DeliveredItems").document(currentItem.getGetId());
          //  Toast.makeText(layoutInflater.getContext(), "ting tongg", Toast.LENGTH_SHORT).show();
            // Copy the document to the new location
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("DeliveredItems")
                    .add(currentItem)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Toast.makeText(getApplicationContext(), "Order Submitted", Toast.LENGTH_SHORT).show();
                            Toast.makeText(layoutInflater.getContext(), "Item moved to Delivered Items", Toast.LENGTH_SHORT).show();
//
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), "Error saving image URL to Firestore", Toast.LENGTH_LONG).show();
                            Toast.makeText(layoutInflater.getContext(), "Item failed to Delivered Items", Toast.LENGTH_SHORT).show();


                            Log.w(TAG, "Error adding document", e);
                        }
                    });


//            sourceDocRef.get().addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.exists()) {
//                    destDocRef.set(documentSnapshot.getData())
//                            .addOnSuccessListener(aVoid -> {
//                                // Document successfully moved
//                                Toast.makeText(layoutInflater.getContext(), "Item moved to Delivered Items", Toast.LENGTH_SHORT).show();
//                            })
//                            .addOnFailureListener(e -> {
//                                // Handle failure
//                                Toast.makeText(layoutInflater.getContext(), "Failed to move item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            });
//                }
//            });

            // Delete the original document from the "Orders" collection
            sourceDocRef.delete();
        } catch (Exception e) {
            Toast.makeText(layoutInflater.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Handle button click and update phase in the database
    private void handleButtonClick(MoharDataModel currentItem) {

        try {
            String currentItemStage = currentItem.getStage();

            switch (currentItemStage) {
                case "Start":
                    // Update phase to "Phase 1 Started" and update in the database
                    currentItem.setStage("Formating Done?");
                    // Toast.makeText(layoutInflater.getContext(), currentItem.getGetId() , Toast.LENGTH_SHORT).show();
                    updateDatabase(currentItem);
                    break;
                case "Formating Done?":
                    // Update phase to "Phase 2 Started" and update in the database
                    currentItem.setStage("Printing Done?");
                    updateDatabase(currentItem);
                    break;
                case "Printing Done?":
                    // Update phase to "Completed" and update in the database

                    SendSMS sendSMS = new SendSMS(layoutInflater.getContext());
                    sendSMS.sendSms(currentItem.getMobileNo(), "Your Items is ready Now, Please visit our store to pick up!");

                    SendWhatsAppSMS sendWhatsAppSMS = new SendWhatsAppSMS();

                    String DeliveryMessage = "आपका ऑर्डर अब तैयार है, कृपया हमारे स्टोर पर आ जाएं और उसे ले जाए!  \uD83D\uDECD\uFE0F\uD83C\uDF89 \n\n" +
                                              "शुभकामनाएँ \n"+
                                              "तारकेश्वर प्रिंटिंग प्रेस, डुमराँव";
                            ;
                    sendWhatsAppSMS.startWhatsAppChat(layoutInflater.getContext(), currentItem.getMobileNo() , DeliveryMessage);



                    currentItem.setStage("Delivery Done?");

                    updateDatabase(currentItem);
                    break;
                case "Delivery Done?":
                    // Update phase to "Completed" and update in the database
                    currentItem.setStage("Dilevered");
                    updateDatabase(currentItem);
                    notifyDataSetChanged();
                    moveDocumentToDeliveredItems(currentItem);


                    break;
                case "Dilevered":
                    // Update phase to "Completed" and update in the database
                    notifyDataSetChanged();
                    moveDocumentToDeliveredItems(currentItem);


                    break;
                // Add more cases if needed
            }
        }catch (Exception e){
            Toast.makeText(layoutInflater.getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }

        // Notify the adapterOrderBook that data has changed
        notifyDataSetChanged();
    }



    // Update the phase in the database (replace this with your actual database update code)
    private void updateDatabase(MoharDataModel currentItem) {

        try {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Orders").document(currentItem.getGetId());
            //FirebaseFirestore.getInstance().collection("your_collection").document(documentId);
            docRef.update("stage", currentItem.getStage())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Database update successful
                            notifyDataSetChanged(); // Notify RecyclerView
                            Toast.makeText(layoutInflater.getContext(), "updated" , Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Toast.makeText(layoutInflater.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){

            Toast.makeText(layoutInflater.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }



    private void showYesNoDialog(MoharDataModel currentItem) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked Yes
                    // Perform your action here
                    handleButtonClick(currentItem);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked No
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Exception e){
            Toast.makeText(layoutInflater.getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void startPhoneCall(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    private void startWhatsAppChat(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=91" + phoneNumber;
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            // Handle case where WhatsApp is not installed
            Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show();
        }
    }


}
