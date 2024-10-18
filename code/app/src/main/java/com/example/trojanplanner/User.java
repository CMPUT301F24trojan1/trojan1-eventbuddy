package com.example.trojanplanner;

public class User {
    private String name;
    private String email;
    private String phoneNumber;
    private String deviceID;
    private String role;

    //Constructor
    public User(String name, String email, String phoneNumber, String deviceID, String role){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceID = deviceID;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
