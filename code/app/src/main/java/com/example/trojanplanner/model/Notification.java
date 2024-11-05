package com.example.trojanplanner.model;


import java.util.List;

// TO DO
/**
 * Notification class to manage and send notifications to users, organizers, and admins
 * based on different event and facility triggers.
 */
public class Notification {

    /**
     * Notifies entrants about the event they registered for.
     *
     * @param user The entrant to notify
     * @param event The event the user registered for
     */
    public void notifyEntrantAboutEvent(User user, Event event) {
        // Implementation for notifying the entrant about their registered event
    }

    /**
     * Sends notification to organizers when the event capacity is full.
     *
     * @param organizer The organizer to notify
     * @param event The event that reached capacity
     */
    public void notifyOrganizerEventFull(User organizer, Event event) {
        // Implementation for notifying the organizer when the event capacity is full
    }

    /**
     * Notifies users about new events.
     *
     * @param users List of users to notify
     * @param event The new event to notify users about
     */
    public void notifyUsersNewEvent(List<User> users, Event event) {
        // Implementation for notifying users about new events
    }

    /**
     * Notifies users for modified event information.
     *
     * @param users List of users to notify
     * @param event The modified event
     */
    public void notifyUsersEventModified(List<User> users, Event event) {
        // Implementation for notifying users about modified event information
    }

    /**
     * Notifies users when an event is finished.
     *
     * @param users List of users to notify
     * @param event The finished event
     */
    public void notifyUsersEventFinished(List<User> users, Event event) {
        // Implementation for notifying users when an event is finished
    }

    /**
     * Notifies users about changes in facility information.
     *
     * @param users List of users to notify
     * @param facility The facility with updated information
     */
    public void notifyUsersFacilityChange(List<User> users, Facility facility) {
        // Implementation for notifying users about changes in facility information
    }
}
