package com.example.trojanplanner.model;

public class Event {
    private String eventName;       // The name of the event
    private String eventDescription; // A brief description of the event
    private int imageResourceId;     // Resource ID for the event image

    // Constructor with no image args
    public Event(String eventName, String eventDescription) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
    }

    // Constructor with image args
    public Event(String eventName, String eventDescription, int imageResourceId) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.imageResourceId = imageResourceId;
    }

    // Getters
    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}