package com.example.trojanplanner.model;


import java.util.ArrayList;

/**
 * Entrant class that is extended from User class that stores information relating to events.
 * An Entrant is tied in and tracks a participant's behavior while engaging in eventbuddy's events.
 * @contributors: Madelaine Dalangin, Dricmoy Bhattacharjee
 */
public class Entrant extends User {

    private ArrayList<Event> currentWaitlistedEvents;
    private ArrayList<Event> currentEnrolledEvents;
    private ArrayList<Event> currentPendingEvents;
    private ArrayList<Event> currentDeclinedEvents;

    /**
     * Constructor Method for Entrant
     *
     * @param lastName    String
     * @param firstName   String
     * @param email       String
     * @param phoneNumber String
     * @param deviceId    String
     * @param role        String
     * @param isOrganizer boolean
     * @param isAdmin     boolean
     * @author Madelaine Dalangin
     */
    public Entrant(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.currentWaitlistedEvents = new ArrayList<Event>();
        this.currentEnrolledEvents = new ArrayList<Event>();
        this.currentPendingEvents = new ArrayList<Event>();
        this.currentDeclinedEvents = new ArrayList<Event>();
    }

    /**
     * Method for adding event to Registered events array
     * @author Madelaine Dalangin
     * @param event
     */
    private void addRegisteredEvent(Event event){
        if(!currentWaitlistedEvents.contains(event)) {
            currentWaitlistedEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Entrant is already registered to event");
        }
    }

    /**
     * Method for removing an event an entrant registered in
     * @author Madelaine Dalangin
     * @param event
     */
    private void removeRegisteredEvent(Event event){
        if(currentWaitlistedEvents.contains(event)){
            currentWaitlistedEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in the list.");
        }
    }

    /**
     * Adding event to array of Entrant's joined events
     * @author Madelaine Dalangin
     * @param event
     */
    private void addJoinedEvent(Event event){
        if(!currentEnrolledEvents.contains(event)){
            currentEnrolledEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Entrant already confirmed to join event.");
        }
    }

    /**
     * Method for removing an event Entrant has joined in (picked on raffle and participant joined)
     * @author Madelaine Dalangin
     * @param event
     */
    private void removeJoinedEvent(Event event){
        if(currentEnrolledEvents.contains(event)){
            currentEnrolledEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in Entrant's joined events.");
        }
    }

    // Method for adding an event to the Pending events array (for events not confirmed yet)
    public void addPendingEvent(Event event) {
        if (!currentPendingEvents.contains(event)) {
            currentPendingEvents.add(event);
        } else {
            throw new IllegalArgumentException("Event is already in Entrant's Pending list.");
        }
        throw new IllegalArgumentException("Event in Entrant's Pending list.");
    }

    // Method for removing events Entrant was picked for but denied to join
    public void removePendingEvent(Event event) {
        if (currentPendingEvents.contains(event)) {
            currentPendingEvents.remove(event);
        } else {
            throw new IllegalArgumentException("Event does not exist in Entrant's pending events list.");
        }
    }


    public ArrayList<Event> getCurrentWaitlistedEvents() {
        return currentWaitlistedEvents;
    }

    public void setCurrentWaitlistedEvents(ArrayList<Event> currentWaitlistedEvents) {
        this.currentWaitlistedEvents = currentWaitlistedEvents;
    }

    public ArrayList<Event> getCurrentEnrolledEvents() {
        return currentEnrolledEvents;
    }

    public void setCurrentEnrolledEvents(ArrayList<Event> currentEnrolledEvents) {
        this.currentEnrolledEvents = currentEnrolledEvents;
    }

    public ArrayList<Event> getCurrentPendingEvents() {
        return currentPendingEvents;
    }

    public void setCurrentPendingEvents(ArrayList<Event> currentPendingEvents) {
        this.currentPendingEvents = currentPendingEvents;
    }

    public ArrayList<Event> getCurrentDeclinedEvents() {
        return currentDeclinedEvents;
    }

    public void setCurrentDeclinedEvents(ArrayList<Event> currentDeclinedEvents) {
        this.currentDeclinedEvents = currentDeclinedEvents;
    }
}
