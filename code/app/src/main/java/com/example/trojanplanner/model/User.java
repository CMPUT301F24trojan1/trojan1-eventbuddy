package com.example.trojanplanner.model;
import android.graphics.Bitmap;
import android.provider.Settings;

import java.io.Serializable;

/**
 * Contains and stores information tied to a user.
 * @author Madelaine Dalangin modified by Dricmoy Bhattacharjee
 */
public abstract class User implements Serializable {
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String deviceId;
    private String pfpFilePath;
    private Bitmap pfpBitmap;
    private String role; //Entrant, Organizer, Admin
    private boolean isOrganizer;
    private boolean isAdmin;

    /**
     * Constructor Method for User
     * @author Madelaine Dalangin, modified by Dricmoy Bhattacharjee
     * @param lastName String
     * @param firstName String
     * @param email String
     * @param phoneNumber String
     * @param deviceId String
     * @param role String
     * @param isOrganizer boolean
     * @param isAdmin boolean
     */
    public User(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin){
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceId = deviceId;
        this.role = role;
        this.isOrganizer = isOrganizer;
        this.isAdmin = isAdmin;
    }

    /**
     * Alternate constructor that also sets pfp file path
     * @param deviceId
     * @param email
     * @param firstName
     * @param hasAdminRights
     * @param hasOrganizerRights
     * @param lastName
     * @param pfp
     * @param phone
     */
    public User(String deviceId, String email, String firstName, boolean hasAdminRights,
                boolean hasOrganizerRights, String lastName, String pfp, String phone){
        this.deviceId = deviceId;
        this.email = email;
        this.firstName = firstName;
        this.isAdmin = hasAdminRights;
        this.isOrganizer = hasOrganizerRights;
        this.lastName = lastName;
        this.pfpFilePath = pfp;
        this.phoneNumber = phone;
    }

    /**
     * Getter method for user's last name
     * @author Madelaine Dalangin
     * @return lastName string
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter method for user's last name
     * @author Madelaine Dalangin
     * @param lastName String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter method for user's first name
     * @author Madelaine dalangin
     * @return firstName String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter method for user's first name
     * @author Madelaine Dalangin
     * @param firstName String
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * String method for User name
     * @author Madelaine Dalangin
     * @return email string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter method for user's email address
     * @author Madelaine Dalangin
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter method for user's phone number
     * @author Madelaine Dalangin
     * @return phoneNumber string
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setter method for user's phone number
     * @author Madelaine Dalangin
     * @param phoneNumber String
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Getter method for User's device ID
     * @author Madelaine Dalangin
     * @return deviceId String
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Setter method for user's device ID
     * @author Madelaine Dalangin
     * @param deviceId String
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Getter method for user's role
     * @author Madelaine Dalangin
     * @return role String
     */
    public String getRole() {
        return role;
    }

    /**
     * Setter method for User's role
     * @author Madelaine Dalangin
     * @param role string
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Boolean method if user is also an organizer
     * @author Madelaine Dalangin
     * @return isOrganizer boolean
     */
    public boolean isOrganizer(){
        return isOrganizer;
    }

    /**
     * Boolean method if user is also an admin
     * @author Madelaine Dalangin
     * @return isAdmin boolean
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    public String getPfpFilePath() {
        return pfpFilePath;
    }

    public void setPfpFilePath(String pfpFilePath) {
        this.pfpFilePath = pfpFilePath;
    }

    public Bitmap getPfpBitmap() {
        return pfpBitmap;
    }

    public void setPfpBitmap(Bitmap pfpBitmap) {
        this.pfpBitmap = pfpBitmap;
    }
}

