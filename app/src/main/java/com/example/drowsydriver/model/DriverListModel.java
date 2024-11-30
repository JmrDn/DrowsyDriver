package com.example.drowsydriver.model;

public class DriverListModel {
    String fullName;
    String plateNum;
    String emergencyContact;
    String id;
    public DriverListModel(String fullName, String plateNum, String emergencyContact, String id) {
        this.fullName = fullName;
        this.plateNum = plateNum;
        this.emergencyContact = emergencyContact;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }
    public String getId() {
        return id;
    }
    @Override
    public String toString() {
        return fullName; // Display the full name in the dropdown
    }
}
