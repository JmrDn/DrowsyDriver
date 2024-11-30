package com.example.drowsydriver.model;

public class AlertListModel {
    String fullName;
    String dateAndTime;
    String plateNum;
    String id;

    public AlertListModel(String fullName, String dateAndTime, String plateNum, String id) {
        this.fullName = fullName;
        this.dateAndTime = dateAndTime;
        this.plateNum = plateNum;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public String getId() {
        return id;
    }
}
