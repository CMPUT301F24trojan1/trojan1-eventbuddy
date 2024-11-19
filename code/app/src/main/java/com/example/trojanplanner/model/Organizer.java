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
     * Alternate constructor to create an INCOMPLETE Organizer object to allow
     * setting attributes after object creation.
     *
     * @param deviceId The deviceId of the organizer account
     * @author Jared Gourley
     */
    public Organizer(String deviceId) {
        super(deviceId);
        facility = null;
        createdEvents = new ArrayList<Event>();
    }


    /**
     * Method for getting the array of Events created
     * @author Jared Gourley
     * @return createdEvents, ArrayList
     */
    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(ArrayList<Event> createdEvents) {
        this.createdEvents = createdEvents;
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
     * @param event The event object to remove
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
     * @param index The index of the event to remove
     * @author Jared Gourley
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
     * Method for overwriting the event at a given index with a new index.
     * @param event The event to assign to the new index
     * @param index The index to assign the event to
     * @author Jared Gourley
     */
    public void setEventAtIndex(Event event, int index) {
        if (index < createdEvents.size() && index >= 0) {
            createdEvents.set(index, event);
        }
        else {
            throw new IllegalArgumentException("Invalid index on event insert");
        }
    }

    /**
     * Method which returns the index in which the event matches the given event ID.
     * @param eventId The event ID to search for in the array.
     * @return The index if found or -1 otherwise.
     * @author Jared Gourley
     */
    public int findIndexWithEventId(String eventId) {
        for (int i = 0; i < createdEvents.size(); i++) {
            if (createdEvents.get(i).getEventId().equals(eventId)) {
                return i;
            }
        }
        return -1;
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
