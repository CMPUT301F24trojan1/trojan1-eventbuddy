package com.example.trojanplanner.model;
import android.provider.Settings;

/**
 * user class contains and stores information tied to a user.
 * @author Madelaine Dalangin
 */
public abstract class User implements UserProfile {
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String deviceId;
    private String role; //Entrant, Organizer, Admin
    private boolean isOrganizer;
    private boolean isAdmin;

    /**
     * Constructor Method for User
     * @author Madelaine Dalangin
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
     * User method using deviceID differently
     * @author Madelaine Dalangin
     * @param lastName
     * @param firstName
     * @param email
     * @param phoneNumber
     * @param role
     * @param isOrganizer
     * @param isAdmin
     */
    public User(String lastName, String firstName, String email, String phoneNumber, String role, boolean isOrganizer, boolean isAdmin){
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceId = Settings.Secure.ANDROID_ID;
        this.role = role;
        this.isOrganizer = isOrganizer;
        this.isAdmin = isAdmin;
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
}

