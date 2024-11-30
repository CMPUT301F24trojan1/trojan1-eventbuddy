package com.example.trojanplanner.model;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.BitmapGenerator;

import java.io.Serializable;
import java.util.Optional;

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
    private SerialBitmap pfpBitmap;
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
     * Alternate constructor to create an INCOMPLETE User object to allow
     * setting attributes after object creation.
     *
     * @param deviceId The deviceId of the user account
     * @author Jared Gourley
     */
    public User(String deviceId) {
        this.deviceId = deviceId;
    }


    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + deviceId + ")";
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

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setIsOrganizer(boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public String getPfpFilePath() {
        return pfpFilePath;
    }

    public void setPfpFilePath(String pfpFilePath) {
        this.pfpFilePath = pfpFilePath;
    }

    /**
     * Returns the bitmap picture for the user. If null, assigns the default picture and
     * returns it to avoid null errors
     * @return The current bitmap for the user or the default bitmap
     */
    @NonNull
    public Bitmap getPfpBitmap() {
        if (this.pfpBitmap == null) {
            // Assign a default picture if the provided one is null
            this.pfpBitmap = new SerialBitmap(getDefaultPicture());
        }
        return pfpBitmap.getBitmap();
    }

    public void setPfpBitmap(Bitmap pfpBitmap) {
        if (pfpBitmap == null) {
            this.pfpBitmap = new SerialBitmap(getDefaultPicture());
        }
        else {
            this.pfpBitmap = new SerialBitmap(pfpBitmap);
        }
    }

    /**
     * Returns the default user bitmap for the app. This is used by any user that doesn't
     * have a photo of its own set yet.
     * @return The default user bitmap
     */
    @NonNull
    public static Bitmap getDefaultPicture() {
        return getDefaultPicture("");
    }

    public static Bitmap getDefaultPicture(String name) {
        String deviceID = name;

        Bitmap defaultPicture = BitmapFactory.decodeResource(App.activity.getResources(), R.drawable.profile_avatar);

        BitmapGenerator generator = new BitmapGenerator(deviceID, defaultPicture);
        return generator.generate();
    }
}

