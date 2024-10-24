package com.example.trojanplanner;


import java.util.ArrayList;

/**
 * Entrant class that is extended from User class that stores information relating to events
 * an Entrant is tied in and tracks participant's behavior while engaging in eventbuddy's events.
 * @author Madelaine Dalangin
 */
public class Entrant extends User{

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
    public Entrant(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.currentRegisteredEvents = new ArrayList<Event>();
        this.currentJoinedEvents = new ArrayList<Event>();
        this.currentPendingEvents = new ArrayList<Event>();
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
        throw new IllegalArgumentException("Entrant is already registered to event");
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
     * Adding event to array of Entrant's joined events
     * @author Madelaine Dalangin
     * @param event
     */
    private void addJoinedEvent(Event event){
        if(!currentJoinedEvents.contains(event)){
            currentJoinedEvents.add(event);
        }
        throw new IllegalArgumentException("Entrant already confirmed to join event.");
    }

    /**
     * Method for removing an event Entrant has joined in (picked on raffle and participant joined)
     * @author Madelaine Dalangin
     * @param event
     */
    private void removeJoinedEvent(Event event){
        if(currentJoinedEvents.contains(event)){
            currentJoinedEvents.remove(event);
        }
        throw new IllegalArgumentException("Event doesn't exist in Entrant's joined events.");
    }

    /**
     * Method for array of events Entrant has yet to confirm rsvp or deny
     * @author Madelaine Dalangin
     * @param event
     */
    private void addPendingEvent(Event event){
        if(!currentPendingEvents.contains(event)){
            currentPendingEvents.add(event);
        }
        throw new IllegalArgumentException("Event in Entrant's Pending list.");
    }

    /**
     * Method for removing events Entrant was picked from but they denied to join
     * @author Madelaine Dalangin
     * @param event
     */
    private void removePendingEvent(Event event){
        if(currentPendingEvents.contains(event)){
            currentPendingEvents.remove(event);
        }
        throw new IllegalArgumentException("Event does not exist in Entrant's pending events list.");
    }


}
