package com.example.trojanplanner.model;

import java.util.ArrayList;

public class Organizer extends User {
    private ArrayList<Event> createdEvents;

    public Organizer(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin, ArrayList<Event> createdEvents) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.createdEvents = createdEvents;
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void addEvent(Event event) {
        if (!createdEvents.contains(event)) {
            createdEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Event is already in organizer list");
        }
    }

    public void removeEvent(Event event) {
        if (createdEvents.contains(event)){
            createdEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in the list.");
        }
    }

    public void removeEvent(int index) {
        if (index < createdEvents.size() && index >= 0) {
            createdEvents.remove(index);
        }
        else {
            throw new IllegalArgumentException("Invalid index on event delete");
        }
    }

}
