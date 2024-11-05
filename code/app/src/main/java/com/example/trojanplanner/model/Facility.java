package com.example.trojanplanner.model;

import android.graphics.Bitmap;

/**
 * Facility class that keeps the location of the Event
 * @author Madelaine Dalangin
 */
public class Facility {
    private Organizer owner;
    private Event event;
    private String dateOccupied;
    private int capacityOfEvent;
    private int maxCapacityOfFacility;
    private boolean isFacilityOccupied;
    private String pfpFacilityFilePath;
    private Bitmap pfpFacilityBitmap;

    /**
     * Constructor for Facility
     * @author Madelaine Dalangin
     * @param owner
     * @param event
     * @param dateOccupied
     * @param capacityOfEvent
     * @param maxCapacityOfFacility
     * @param isFacilityOccupied
     * @param pfpFacilityFilePath
     * @param pfpFacilityBitmap
     */
    public Facility(Organizer owner, Event event, String dateOccupied, int capacityOfEvent, int maxCapacityOfFacility, boolean isFacilityOccupied, String pfpFacilityFilePath, Bitmap pfpFacilityBitmap) {
        this.owner = owner;
        this.event = event;
        this.dateOccupied = dateOccupied;
        this.capacityOfEvent = capacityOfEvent;
        this.maxCapacityOfFacility = maxCapacityOfFacility;
        this.isFacilityOccupied = isFacilityOccupied;
        this.pfpFacilityFilePath = pfpFacilityFilePath;
        this.pfpFacilityBitmap = pfpFacilityBitmap;
    }

    /**
     * Method for getting the name of the Owner
     * @author Madelaine Dalangin
     * @return
     */
    public Organizer getOwner() {
        return owner;
    }

    /**
     * method for getting the event
     * @author Madelaine Dalangin
     * @return event, Event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Method for getting the date facility is occupied
     * @author Madelaine Dalangin
     * @return dateOccupied, a String
     */
    public String getDateOccupied() {
        return dateOccupied;
    }

    /**
     * method for getting the capacity of event
     * @author Madelaine Dalangin
     * @return capacityOfEvent, int
     */
    public int getCapacityOfEvent() {
        return capacityOfEvent;
    }

    /**
     * Method for getting the max capacity of facility
     * @author Madelaine Dalangin
     * @return maxCapacityOfFacility, int
     */
    public int getMaxCapacityOfFacility() {
        return maxCapacityOfFacility;
    }

    /**
     * Method for checking if facility is occupied
     * @author Madelaine Dalangin
     * @return isFacilityOccupied, boolean
     */
    public boolean isFacilityOccupied() {
        return isFacilityOccupied;
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
