package com.example.trojanplanner.model;

public class Facility {
    private Organizer owner;
    private Event event;
    private String dateOccupied;
    private int capacityOfEvent;
    private int maxCapacityOfFacility;
    private boolean isOccupied;

    public Facility(Organizer owner, Event event, String dateOccupied, int capacityOfEvent, int maxCapacityOfFacility, boolean isOccupied) {
        this.owner = owner;
        this.event = event;
        this.dateOccupied = dateOccupied;
        this.capacityOfEvent = capacityOfEvent;
        this.maxCapacityOfFacility = maxCapacityOfFacility;
        this.isOccupied = isOccupied;
    }

    public Organizer getOwner() {
        return owner;
    }

    public Event getEvent() {
        return event;
    }

    public String getDateOccupied() {
        return dateOccupied;
    }

    public int getCapacityOfEvent() {
        return capacityOfEvent;
    }

    public int getMaxCapacityOfFacility() {
        return maxCapacityOfFacility;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
