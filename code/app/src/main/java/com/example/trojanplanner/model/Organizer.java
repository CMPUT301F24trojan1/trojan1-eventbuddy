package com.example.trojanplanner.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Organizer class that extends User class that stores information relating to entrants who have
 * created an Event for people to participate in
 * @author Madelaine Dalangin, Jared Gourley
 */
public class Organizer extends User {
    private ArrayList<Event> createdEvents;
    private Facility facility;

    /**
     * Constructor for Organizer
     * @author Madelaine Dalangin, Jared Gourley
     * @param lastName
     * @param firstName
     * @param email
     * @param phoneNumber
     * @param deviceId
     * @param role
     * @param isOrganizer
     * @param isAdmin
     * @param createdEvents
     * @param facility
     */
    public Organizer(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin, ArrayList<Event> createdEvents, Facility facility) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.createdEvents = createdEvents;
        this.facility = facility;
    }

    /**
     * Method for getting the array of Events created
     * @author Jared Gourley
     * @return createdEvents, ArrayList
     */
    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    /**
     * Method for adding event to list
     * @author Jared Gourley
     * @param event, Event
     */
    public void addEvent(Event event) {
        if (!createdEvents.contains(event)) {
            createdEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Event is already in organizer list");
        }
    }

    /**
     * method for removing an event from the array using object Event as parameter
     * @author Jared Gourley
     * @param event, Event
     */
    public void removeEvent(Event event) {
        if (createdEvents.contains(event)){
            createdEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in the list.");
        }
    }

    /**
     * Method for removing an Event using index
     * @author Jared Gourley
     * @param index, int
     */
    public void removeEvent(int index) {
        if (index < createdEvents.size() && index >= 0) {
            createdEvents.remove(index);
        }
        else {
            throw new IllegalArgumentException("Invalid index on event delete");
        }
    }

    /**
     * Method to create Facility by the Organizer as per User story US 02.01.03 As an organizer, I want to create and manage my facility profile.
     * @param name
     * @param facilityId
     * @param location
     * @param owner
     * @param pfpFacilityFilePath
     * @param pfpFacilityBitmap
     * @return
     */
    public Facility createFacility(String name, String facilityId, String location, Organizer owner, String pfpFacilityFilePath, Bitmap pfpFacilityBitmap){
        if(facility == null){
            facility = new Facility(name, facilityId, location, owner, pfpFacilityFilePath, pfpFacilityBitmap);
            return facility;
        } else {
            return null;
        }
    }

    /**
     * Method to remove Facility
     * @author Madelaine Dalangin
     */

    public void removeFacility(){
        if(facility != null){
            facility = null;
        } else {
            throw new IllegalArgumentException("Facility doesn't exist.");
        }
    }

    /**
     * method to get facility
     * @author Madelaine Dalangin
     * @return
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Method to set facility by Organizer
     * @author Madelaine Dalangin
     * @param facility, Facility
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
