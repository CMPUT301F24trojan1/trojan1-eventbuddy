package com.example.trojanplanner;


import java.util.ArrayList;

/**
 * Participant class that is extended from User class that stores information relating to events
 * a participant is tied in and tracks participant's behavior while engaging in eventbuddy's events.
 * @author Madelaine Dalangin
 */
public class Participant extends User{

    private boolean checkInStatus;
    private ArrayList<Event> currentRegisteredEvents;
    private ArrayList<Event> currentJoinedEvents;
    private ArrayList<Event> currentPendingEvents;

    /**
     * Constructor Method for User
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
    public Participant(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin, Event event, boolean checkInStatus) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.checkInStatus = checkInStatus;
        this.currentRegisteredEvents = new ArrayList<Event>();
    }

    /**
     * Method for adding event to Registered events array
     * @author Madelaine Dalangin
     * @param event
     */
    private void addRegisteredEvent(Event event){
        if(!currentRegisteredEvents.contains(event)) {
            currentRegisteredEvents.add(event);
        }
        throw new IllegalArgumentException("Participant is already registered to event");
    }

    /**
     * Method for removing an event a participant registered in
     * @author Madelaine Dalangin
     * @param event
     */
    private void removeRegisteredEvent(Event event){
        if(currentRegisteredEvents.contains(event)){
            currentRegisteredEvents.remove(event);
        }
        throw new IllegalArgumentException("Event doesn't exist in the list.");
    }

    /**
     * Adding event to array of Participant's joined events
     * @author Madelaine Dalangin
     * @param event
     */
    private void addJoinedEvent(Event event){
        if(!currentJoinedEvents.contains(event)){
            currentJoinedEvents.add(event);
        }
        throw new IllegalArgumentException("Participant already confirmed to join event.");
    }

    /**
     * Method for removing an event Participant has joined in (picked on raffle and participant joined)
     * @author Madelaine Dalangin
     * @param event
     */
    private void removeJoinedEvent(Event event){
        if(currentJoinedEvents.contains(event)){
            currentJoinedEvents.remove(event);
        }
        throw new IllegalArgumentException("Event doesn't exist in Participant's joined events.");
    }

    /**
     * Method for array of events participant has yet to confirm rsvp or deny
     * @author Madelaine Dalangin
     * @param event
     */
    private void addPendingEvent(Event event){
        if(!currentPendingEvents.contains(event)){
            currentPendingEvents.add(event);
        }
        throw new IllegalArgumentException("Event in Participant's Pending list.");
    }

    /**
     * Method for removing events participant was picked from but they denied to join
     * @author Madelaine Dalangin
     * @param event
     */
    private void removePendingEvent(Event event){
        if(currentPendingEvents.contains(event)){
            currentPendingEvents.remove(event);
        }
        throw new IllegalArgumentException("Event does not exist in Participant's pending events list.");
    }


}