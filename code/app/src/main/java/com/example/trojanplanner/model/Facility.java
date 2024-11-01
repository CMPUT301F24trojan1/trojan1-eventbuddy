package com.example.trojanplanner.model;

import android.graphics.Bitmap;

public class Facility {
    private Organizer owner;
    private Event event;
    private String dateOccupied;
    private int capacityOfEvent;
    private int maxCapacityOfFacility;
    private boolean isOccupied;
    private String pfpFacilityFilePath;
    private Bitmap pfpFacilityBitmap;

    public Facility(Organizer owner, Event event, String dateOccupied, int capacityOfEvent, int maxCapacityOfFacility, boolean isOccupied, String pfpFacilityFilePath, Bitmap pfpFacilityBitmap) {
        this.owner = owner;
        this.event = event;
        this.dateOccupied = dateOccupied;
        this.capacityOfEvent = capacityOfEvent;
        this.maxCapacityOfFacility = maxCapacityOfFacility;
        this.isOccupied = isOccupied;
        this.pfpFacilityFilePath = pfpFacilityFilePath;
        this.pfpFacilityBitmap = pfpFacilityBitmap;
    }

    public Organizer getOwner() {
        return owner;
    }

    public Event getEvent() {
        return event;
    }

    public String getDateOccupied() {
        return dateOccupied;
    }

    public int getCapacityOfEvent() {
        return capacityOfEvent;
    }

    public int getMaxCapacityOfFacility() {
        return maxCapacityOfFacility;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public String getPfpFacilityFilePath() {
        return pfpFacilityFilePath;
    }

    public void setPfpFacilityFilePath(String pfpFacilityFilePath) {
        this.pfpFacilityFilePath = pfpFacilityFilePath;
    }

    public Bitmap getPfpFacilityBitmap() {
        return pfpFacilityBitmap;
    }

    public void setPfpFacilityBitmap(Bitmap pfpFacilityBitmap) {
        this.pfpFacilityBitmap = pfpFacilityBitmap;
    }
}
