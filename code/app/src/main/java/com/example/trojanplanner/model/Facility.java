package com.example.trojanplanner.model;

import android.graphics.Bitmap;

/**
 * Facility class that keeps the location of the Event
 * @author Madelaine Dalangin
 */
public class Facility {
    private String facilityId;
    private String name;
    private String location;
    private Organizer owner;
    private String pfpFacilityFilePath;
    private Bitmap pfpFacilityBitmap;


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
        this.pfpFacilityBitmap = pfpFacilityBitmap;
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
     * Method for getting pfp Facility
     * @author Madelaine Dalangin
     * @return pfpFacilityBitmap, Bitmap
     */
    public Bitmap getPfpFacilityBitmap() {
        return pfpFacilityBitmap;
    }

    /**
     * Method for setting pfp Facility
     * @author Madelaine Dalangin
     * @param pfpFacilityBitmap, Bitmap
     */
    public void setPfpFacilityBitmap(Bitmap pfpFacilityBitmap) {
        this.pfpFacilityBitmap = pfpFacilityBitmap;
    }
}
