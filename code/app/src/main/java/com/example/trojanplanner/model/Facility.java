package com.example.trojanplanner.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;

import java.io.Serializable;

/**
 * Facility class that keeps the location of the Event
 * @author Madelaine Dalangin
 */
public class Facility implements Serializable {
    private String facilityId;
    private String name;
    private String location;
    private Organizer owner;
    private String pfpFacilityFilePath;
    private SerialBitmap pfpFacilityBitmap;


    /**
     * Constructor for Facility
     * @param name The name of the facility
     * @param facilityId The unique ID of the facility
     * @param location
     * @param owner
     * @param pfpFacilityFilePath
     * @author Jared Gourley
     */
    public Facility(String name, String facilityId, String location, Organizer owner, String pfpFacilityFilePath) {
        this.name = name;
        this.facilityId = facilityId;
        this.location = location;
        this.owner = owner;
        this.pfpFacilityFilePath = pfpFacilityFilePath;
    }


    /**
     * Alternate constructor for Facility explicitly setting pfp bitmap
     * @param name The name of the facility
     * @param facilityId The unique ID of the facility
     * @param location
     * @param owner
     * @param pfpFacilityFilePath
     * @param pfpFacilityBitmap
     * @author Madelaine Dalangin
     */
    public Facility(String name, String facilityId, String location, Organizer owner, String pfpFacilityFilePath, Bitmap pfpFacilityBitmap) {
        this.name = name;
        this.facilityId = facilityId;
        this.location = location;
        this.owner = owner;
        this.pfpFacilityFilePath = pfpFacilityFilePath;
        this.setPfpFacilityBitmap(pfpFacilityBitmap);
    }


    /**
     * Alternate constructor to create an INCOMPLETE Facility object to allow
     * setting attributes after object creation.
     *
     * @param facilityId The facilityId of the event
     * @author Jared Gourley
     */
    public Facility(String facilityId) {
        this.facilityId = facilityId;
    }


    @Override
    public String toString() {
        return "Facility: " + name + " (" + facilityId + ")";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method for getting the name of the Owner
     * @author Madelaine Dalangin
     * @return
     */
    public Organizer getOwner() {
        return owner;
    }


    public void setOwner(Organizer owner) {
        this.owner = owner;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Method for getting the facility profile photo file path
     * @author Madelaine Dalangin
     * @return pfpFacilityFilePath, String
     */
    public String getPfpFacilityFilePath() {
        return pfpFacilityFilePath;
    }

    /**
     * Method for setting facility pfp filepath
     * @author Madelaine Dalangin
     * @param pfpFacilityFilePath, String
     */
    public void setPfpFacilityFilePath(String pfpFacilityFilePath) {
        this.pfpFacilityFilePath = pfpFacilityFilePath;
    }

    /**
     * Returns the bitmap picture for the facility. If null, assigns the default picture and
     * returns it to avoid null errors
     * @return The current bitmap for the facility or the default bitmap
     */
    @NonNull
    public Bitmap getPfpFacilityBitmap() {
        // If the picture attribute is null, assign it the default value since it should have a value
        if (pfpFacilityBitmap == null) {
            pfpFacilityBitmap = new SerialBitmap(getDefaultPicture());
        }
        return pfpFacilityBitmap.getBitmap();
    }

    /**
     * Method for setting pfp Facility
     * @param pfpFacilityBitmap Bitmap to set
     * @author Jared Gourley
     */
    public void setPfpFacilityBitmap(Bitmap pfpFacilityBitmap) {
        if (pfpFacilityBitmap == null) {
            // Assign a default picture if the provided one is null
            this.pfpFacilityBitmap = new SerialBitmap(getDefaultPicture());
        } else {
            this.pfpFacilityBitmap = new SerialBitmap(pfpFacilityBitmap);
        }
    }


    /**
     * Returns the default facility bitmap for the app. This is used by any facility that doesn't
     * have a photo of its own set yet.
     * @return The default facility bitmap
     */
    @NonNull
    public static Bitmap getDefaultPicture() {
        // load a default image resource as a Bitmap
        return BitmapFactory.decodeResource(App.activity.getResources(), R.drawable.default_facility_pic2);
    }

}
