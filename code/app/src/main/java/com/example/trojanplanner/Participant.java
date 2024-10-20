package com.example.trojanplanner;



public class Participant extends User{

    private Event event;
    private boolean checkInStatus;

    //TODO:
    //method for participant


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(boolean checkInStatus) {
        this.checkInStatus = checkInStatus;
    }
}
