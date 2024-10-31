package com.example.trojanplanner.model;

public class Organizer extends User{
    private Entrant nameOfOrganizer;
    private Event nameOfEvent; //should this be Event class and not string?

    public Organizer(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin, Entrant nameOfOrganizer, Event nameOfEvent) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.nameOfOrganizer = nameOfOrganizer;
        this.nameOfEvent = nameOfEvent;
    }

}
