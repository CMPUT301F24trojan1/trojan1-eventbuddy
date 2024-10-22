package com.example.trojanplanner;


import java.util.ArrayList;

public class Participant extends User{


    private boolean checkInStatus;
    private ArrayList<Event> currentEventsRegistered;

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
        this.currentEventsRegistered = new ArrayList<Event>();
    }

    public void addEvent(Event event){
        if(!currentEventsRegistered.contains(event)) {
            currentEventsRegistered.add(event);
        }
        throw new IllegalArgumentException("Participant is already registered to event");
    }

    public void removeEvent(Event event){
        if(currentEventsRegistered.contains(event)){
            currentEventsRegistered.remove(event);
        }
        throw new IllegalArgumentException("Event doesn't exist in the list.");
    }

    public boolean isCheckInStatus() {
        return checkInStatus;
    }

}
