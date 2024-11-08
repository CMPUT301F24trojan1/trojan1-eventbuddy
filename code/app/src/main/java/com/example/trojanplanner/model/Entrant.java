package com.example.trojanplanner.model;


import java.util.ArrayList;

/**
 * Entrant class that is extended from User class that stores information relating to events.
 * An Entrant is tied in and tracks a participant's behavior while engaging in eventbuddy's events.
 * @author Madelaine Dalangin, Dricmoy Bhattacharjee
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
     * Method for adding event to waitlisted events array
     * @author Madelaine Dalangin
     * @param event
     */
    public void addWaitlistedEvent(Event event){
        if(!currentWaitlistedEvents.contains(event)) {
            currentWaitlistedEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Entrant is already registered to event");
        }
    }

    /**
     * Method for removing an event an entrant is waitlisted in
     * @author Madelaine Dalangin
     * @param event
     */
    public void removeWaitlistedEvent(Event event){
        if(currentWaitlistedEvents.contains(event)){
            currentWaitlistedEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in the list.");
        }
    }

    /**
     * Adding event to array of Entrant's enrolled events
     * @author Madelaine Dalangin
     * @param event
     */
    public void addEnrolledEvent(Event event){
        if(!currentEnrolledEvents.contains(event)){
            currentEnrolledEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Entrant already confirmed to join event.");
        }
    }

    /**
     * Method for removing an event Entrant has enrolled in (picked in raffle and participant accepted)
     * @author Madelaine Dalangin
     * @param event
     */
    public void removeEnrolledEvent(Event event){
        if(currentEnrolledEvents.contains(event)){
            currentEnrolledEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in Entrant's joined events.");
        }
    }

    // Method for adding an event to the Pending events array (for events not accepted yet by entrant)
    public void addPendingEvent(Event event) {
        if (!currentPendingEvents.contains(event)) {
            currentPendingEvents.add(event);
        } else {
            throw new IllegalArgumentException("Event is already in Entrant's Pending list.");
        }
    }

    // Method for removing events Entrant was picked for but did not respond to
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

    /**
     * Returns the same user as an Organizer class.
     * <br>
     * isOrganizer must be true otherwise an exception is thrown.
     * @return The equivalent organizer class for this entrant
     */
    public Organizer returnOrganizer() {
        // TODO enforce the isOrganizer check
        Organizer organizer = new Organizer(this.getLastName(), this.getFirstName(), this.getEmail(), this.getPhoneNumber(), this.getDeviceId(), this.getRole(), true, this.isAdmin(), new ArrayList<Event>(), null);

        return organizer;
    }



}
