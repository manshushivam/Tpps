package com.example.tpps.dataModel;

public class MoharDataModel {
    private String imageUrl;
    private String orderDate;
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

    public MoharDataModel(String imageUrl, String orderDate, String mobileNo, String content, String totalAmount, String paidAmount,String stage, String getID) {
        this.imageUrl = imageUrl;
        this.orderDate = orderDate;
        this.mobileNo = mobileNo;
        this.content = content;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.stage = stage;
        this.getId = getID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOrderDate() {
        return orderDate;
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
