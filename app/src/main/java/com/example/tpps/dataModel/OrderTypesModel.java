package com.example.tpps.dataModel;

public class OrderTypesModel {
    private String orderType;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public OrderTypesModel(String orderType){
        this.orderType = orderType;
    }

}
