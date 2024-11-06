package com.example.trojanplanner.model;


import java.util.ArrayList;

/**
 * Entrant class that is extended from User class that stores information relating to events.
 * An Entrant is tied in and tracks a participant's behavior while engaging in eventbuddy's events.
 * @contributors: Madelaine Dalangin, Dricmoy Bhattacharjee
 */
public class Entrant extends User {

    private ArrayList<Event> currentRegisteredEvents;
    private ArrayList<Event> currentJoinedEvents;
    private ArrayList<Event> currentPendingEvents;

    /**
     * Constructor Method for Entrant
     *
     * @param lastName         String
     * @param firstName        String
     * @param email            String
     * @param phoneNumber      String
     * @param deviceId         String
     * @param isOrganizer      boolean
     * @param isAdmin          boolean
     * @param profilePictureUrl String
     * @author Madelaine Dalangin
     */
    public Entrant(String lastName, String firstName, String email, String phoneNumber, String deviceId, boolean isOrganizer, boolean isAdmin, String profilePictureUrl) {
        super(deviceId, email, firstName, isOrganizer, isAdmin, lastName, profilePictureUrl, phoneNumber);
        this.currentRegisteredEvents = new ArrayList<>();
        this.currentJoinedEvents = new ArrayList<>();
        this.currentPendingEvents = new ArrayList<>();
    }

    // Method for adding an event to the Registered events array
    public void addRegisteredEvent(Event event) {
        if (!currentRegisteredEvents.contains(event)) {
            currentRegisteredEvents.add(event);
        } else {
            throw new IllegalArgumentException("Entrant is already registered to this event.");
        }
    }

    // Method for removing an event the entrant registered for
    public void removeRegisteredEvent(Event event) {
        if (currentRegisteredEvents.contains(event)) {
            currentRegisteredEvents.remove(event);
        } else {
            throw new IllegalArgumentException("Event doesn't exist in the registered list.");
        }
    }

    // Adding event to array of Entrant's joined events
    public void addJoinedEvent(Event event) {
        if (!currentJoinedEvents.contains(event)) {
            currentJoinedEvents.add(event);
        } else {
            throw new IllegalArgumentException("Entrant already confirmed to join this event.");
        }
    }

    // Method for removing an event Entrant has joined
    public void removeJoinedEvent(Event event) {
        if (currentJoinedEvents.contains(event)) {
            currentJoinedEvents.remove(event);
        } else {
            throw new IllegalArgumentException("Event doesn't exist in Entrant's joined events list.");
        }
    }

    // Method for adding an event to the Pending events array (for events not confirmed yet)
    public void addPendingEvent(Event event) {
        if (!currentPendingEvents.contains(event)) {
            currentPendingEvents.add(event);
        } else {
            throw new IllegalArgumentException("Event is already in Entrant's Pending list.");
        }
    }

    // Method for removing events Entrant was picked for but denied to join
    public void removePendingEvent(Event event) {
        if (currentPendingEvents.contains(event)) {
            currentPendingEvents.remove(event);
        } else {
            throw new IllegalArgumentException("Event does not exist in Entrant's pending events list.");
        }
    }

    // Getters and setters for the lists of events, if needed.
    public ArrayList<Event> getCurrentRegisteredEvents() {
        return currentRegisteredEvents;
    }

    public ArrayList<Event> getCurrentJoinedEvents() {
        return currentJoinedEvents;
    }

    public ArrayList<Event> getCurrentPendingEvents() {
        return currentPendingEvents;
    }

    public void setCurrentRegisteredEvents(ArrayList<Event> currentRegisteredEvents) {
        this.currentRegisteredEvents = currentRegisteredEvents;
    }

    public void setCurrentJoinedEvents(ArrayList<Event> currentJoinedEvents) {
        this.currentJoinedEvents = currentJoinedEvents;
    }

    public void setCurrentPendingEvents(ArrayList<Event> currentPendingEvents) {
        this.currentPendingEvents = currentPendingEvents;
    }
}