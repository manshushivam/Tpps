package com.example.tpps.dataModel;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MoharDataModel {

    private String name;
    private String address;
    private String imageUrl;
    private String orderDate;
    private String dueDate;
    private String mobileNo;
    private String content;
    private String totalAmount;

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getGetId() {
        return getId;
    }

    private String paidAmount;

    private String stage;
    private String getId;
    private String orderType;

    public MoharDataModel(String name , String address, String imageUrl, String orderDate, String dueDate ,String orderType, String mobileNo, String content, String totalAmount, String paidAmount,String stage, String getID) {
       this.name = name;
       this.address = address;
        this.imageUrl = imageUrl;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.orderType = orderType;
        this.mobileNo = mobileNo;
        this.content = content;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.stage = stage;
        this.getId = getID;
    }

    public String getOrderType() {
        return orderType;
    }
    public String getDueDate() {
        return dueDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getMobileNo() {
        return mobileNo;
    }


    public String getStage() {
        return stage;
    }

    public String getContent() {
        return content;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getPaidAmount() {
        return paidAmount;
    }



}
