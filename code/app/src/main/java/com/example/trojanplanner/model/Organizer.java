package com.example.trojanplanner.model;

import java.util.ArrayList;

public class Organizer extends User {
    private ArrayList<Event> createdEvents;
    private ArrayList<Facility> createdFacility;

    public Organizer(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin, ArrayList<Event> createdEvents, ArrayList<Facility> createdFacility) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.createdEvents = createdEvents;
        this.createdFacility = createdFacility;
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

    public ArrayList<Facility> getCreatedFacility() {
        return createdFacility;
    }

    public void createFacility(Facility facility) {
        if (!createdFacility.contains(facility)){
            createdFacility.add(facility);
        } else {
            throw new IllegalArgumentException("Facility already created.");
        }
    }

    public void removeFacility(int index){
        if(index < createdFacility.size() && index >= 0){
            createdFacility.remove(index)
        } else {
            throw new IllegalArgumentException("Facility does not exist in the list.");
        }
    }

}
