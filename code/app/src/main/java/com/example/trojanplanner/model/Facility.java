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
    //private Event event;
    //private String dateOccupied;
    //private int capacityOfEvent;
    //private int maxCapacityOfFacility;
    //private boolean hasOwner;
    private String pfpFacilityFilePath;
    private Bitmap pfpFacilityBitmap;


    /**
     * Constructor for Facility
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
//        this.event = event;
//        this.dateOccupied = dateOccupied;
//        this.capacityOfEvent = capacityOfEvent;
//        this.maxCapacityOfFacility = maxCapacityOfFacility;
//        this.hasOwner = hasOwner;
        this.pfpFacilityFilePath = pfpFacilityFilePath;
        this.pfpFacilityBitmap = pfpFacilityBitmap;
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
//    /**
//     * method for getting the event
//     * @author Madelaine Dalangin
//     * @return event, Event
//     */
//    public Event getEvent() {
//        return event;
//    }

//    /**
//     * Method for getting the date facility is occupied
//     * @author Madelaine Dalangin
//     * @return dateOccupied, a String
//     */
//    public String getDateOccupied() {
//        return dateOccupied;
//    }
//
//    /**
//     * method for getting the capacity of event
//     * @author Madelaine Dalangin
//     * @return capacityOfEvent, int
//     */
//    public int getCapacityOfEvent() {
//        return capacityOfEvent;
//    }
//
//    /**
//     * Method for getting the max capacity of facility
//     * @author Madelaine Dalangin
//     * @return maxCapacityOfFacility, int
//     */
//    public int getMaxCapacityOfFacility() {
//        return maxCapacityOfFacility;
//    }

//    /**
//     * Method for checking if facility has owner organizer
//     * @author Madelaine Dalangin
//     * @return isFacilityOccupied, boolean
//     */
//    public boolean hasOwner() {
//        return hasOwner;
//    }


//    /**
//     * Method for setting if facility has owner organizer
//     * @param value
//     */
//    public void setHasOwner(boolean value) {
//        hasOwner = value;
//    }


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
