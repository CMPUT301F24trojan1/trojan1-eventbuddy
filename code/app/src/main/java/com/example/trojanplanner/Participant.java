package com.example.trojanplanner;



public class Participant extends User{

    private Event event;
    private boolean checkInStatus;

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
        this.event = event;
        this.checkInStatus = checkInStatus;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isCheckInStatus() {
        return checkInStatus;
    }

}
