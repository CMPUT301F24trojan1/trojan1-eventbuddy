package com.example.trojanplanner.model;

import java.util.ArrayList;

/**
 * Organizer class that extends User class that stores information relating to entrants who have
 * created an Event for people to participate in
 * @author Madelaine Dalangin, Jared Gourley
 */
public class Organizer extends User {
    private ArrayList<Event> createdEvents;
    private ArrayList<Facility> createdFacility;

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
     * @param createdFacility
     */
    public Organizer(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin, ArrayList<Event> createdEvents, ArrayList<Facility> createdFacility) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.createdEvents = createdEvents;
        this.createdFacility = createdFacility;
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
     * Method to get a facility created by Organizer
     * @author Madelaine Dalangin
     * @return createdFacility, ArrayList
     */
    public ArrayList<Facility> getCreatedFacility() {
        return createdFacility;
    }

    /**
     * Method for Organizer to create Facility
     * @author Madelaine Dalangin
     * @param facility, Facility
     */
    public void createFacility(Facility facility) {
        if (!createdFacility.contains(facility)){
            createdFacility.add(facility);
        } else {
            throw new IllegalArgumentException("Facility already created.");
        }
    }

    /**
     * Method for removing Facility using the object Facility
     * @author Madelaine Dalangin
     * @param facility, Facility
     */
    public void removeFacility(Facility facility) {
        if (createdFacility.contains(facility)){
            createdFacility.remove(facility);
        }
        else {
            throw new IllegalArgumentException("Facility doesn't exist in the list.");
        }
    }

    /**
     * Method to remove Facility by Organizer using its index
     * @author Madelaine Dalangin
     * @param index, int
     */
    public void removeFacility(int index){
        if(index < createdFacility.size() && index >= 0){
            createdFacility.remove(index);
        } else {
            throw new IllegalArgumentException("Facility does not exist in the list.");
        }
    }

}
