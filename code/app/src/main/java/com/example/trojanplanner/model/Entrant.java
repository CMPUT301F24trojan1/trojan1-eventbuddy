package com.example.trojanplanner.model;


import java.util.ArrayList;

/**
 * Entrant class that is extended from User class that stores information relating to events.
 * An Entrant is tied in and tracks a participant's behavior while engaging in eventbuddy's events.
 * @author Madelaine Dalangin, Dricmoy Bhattacharjee
 */
public class Entrant extends User {

    private ArrayList<Event> currentWaitlistedEvents;
    private ArrayList<Event> currentEnrolledEvents;
    private ArrayList<Event> currentPendingEvents;
    private ArrayList<Event> currentDeclinedEvents;

    /**
     * Constructor Method for a new Entrant (that has no events yet)
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
    public Entrant(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.currentWaitlistedEvents = new ArrayList<Event>();
        this.currentEnrolledEvents = new ArrayList<Event>();
        this.currentPendingEvents = new ArrayList<Event>();
        this.currentDeclinedEvents = new ArrayList<Event>();
    }


    /**
     * Constructor Method for an existing Entrant that already has events set
     *
     * @param lastName    String
     * @param firstName   String
     * @param email       String
     * @param phoneNumber String
     * @param deviceId    String
     * @param role        String
     * @param isOrganizer boolean
     * @param isAdmin     boolean
     * @param currentWaitlistedEvents ArrayList<Event>
     * @param currentEnrolledEvents ArrayList<Event>
     * @param currentWaitlistedEvents ArrayList<Event>
     * @param currentPendingEvents ArrayList<Event>
     * @param currentDeclinedEvents ArrayList<Event>
     * @author Jared Gourley
     */
    public Entrant(String lastName, String firstName, String email, String phoneNumber, String deviceId, String role, boolean isOrganizer, boolean isAdmin, ArrayList<Event> currentWaitlistedEvents, ArrayList<Event> currentEnrolledEvents, ArrayList<Event> currentPendingEvents, ArrayList<Event> currentDeclinedEvents) {
        super(lastName, firstName, email, phoneNumber, deviceId, role, isOrganizer, isAdmin);
        this.currentWaitlistedEvents = currentWaitlistedEvents;
        this.currentEnrolledEvents = currentEnrolledEvents;
        this.currentPendingEvents = currentPendingEvents;
        this.currentDeclinedEvents = currentDeclinedEvents;
    }

    /**
     * Alternate constructor to create an INCOMPLETE Entrant object to allow
     * setting attributes after object creation.
     *
     * @param deviceId The deviceId of the entrant account
     * @author Jared Gourley
     */
    public Entrant(String deviceId) {
        super(deviceId);
        this.currentWaitlistedEvents = new ArrayList<Event>();
        this.currentEnrolledEvents = new ArrayList<Event>();
        this.currentPendingEvents = new ArrayList<Event>();
        this.currentDeclinedEvents = new ArrayList<Event>();
    }


    /**
     * A method to return one of the four arrays using an int, making looping over them easier.
     * @param index 0 = currentEnrolledEvents, 1 = currentWaitlistedEvents, 2 = currentPendingEvents, 3 = currentDeclinedEvents
     * @return One of the above arrays
     * @author Jared Gourley
     */
    public ArrayList<Event> returnEventsArrayByIndex(int index) {
        if (index == 0) {
            return currentEnrolledEvents;
        }
        if (index == 1) {
            return currentWaitlistedEvents;
        }
        if (index == 2) {
            return currentPendingEvents;
        }
        if (index == 3) {
            return currentDeclinedEvents;
        }
        else {
            throw new IndexOutOfBoundsException("Index must be between 0-3");
        }
    }


    /**
     * Method which returns the index in which the event matches the given event ID.
     * @param array The array to search through
     * @param eventId The event ID to search for in the array.
     * @return The index if found or -1 otherwise.
     * @author Jared Gourley
     */
    public int findIndexWithId(ArrayList<Event> array, String eventId) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getEventId().equals(eventId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Replaces an event object from any of the event arrays with the new event object
     * if the event ids match. Only replaces one instance under the assumption that no
     * duplicates should ever appear.
     * @param event The new event object to replace if ids match
     * @return true if a replace happened, false if not
     * @author Jared Gourley
     */
    public boolean replaceEventMatchingId(Event event) {
        String eventId = event.getEventId();
        int foundIndex = -1;
        ArrayList<Event> array;
        for (int i = 0; i < 4; i++) {
            array = returnEventsArrayByIndex(i);
            foundIndex = findIndexWithId(array, eventId);
            if (foundIndex != -1) {
                array.set(foundIndex, event);
                return true;
            }
        }
        return false;
    }





    /**
     * Method for adding event to waitlisted events array
     * @author Madelaine Dalangin
     * @param event
     */
    public void addWaitlistedEvent(Event event){
        if(!currentWaitlistedEvents.contains(event)) {
            currentWaitlistedEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Entrant is already registered to event");
        }
    }

    /**
     * Method for removing an event an entrant is waitlisted in
     * @author Madelaine Dalangin
     * @param event
     */
    public void removeWaitlistedEvent(Event event){
        if(currentWaitlistedEvents.contains(event)){
            currentWaitlistedEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in the list.");
        }
    }

    /**
     * Adding event to array of Entrant's enrolled events
     * @author Madelaine Dalangin
     * @param event
     */
    public void addEnrolledEvent(Event event){
        if(!currentEnrolledEvents.contains(event)){
            currentEnrolledEvents.add(event);
        }
        else {
            throw new IllegalArgumentException("Entrant already confirmed to join event.");
        }
    }

    /**
     * Method for removing an event Entrant has enrolled in (picked in raffle and participant accepted)
     * @author Madelaine Dalangin
     * @param event
     */
    public void removeEnrolledEvent(Event event){
        if(currentEnrolledEvents.contains(event)){
            currentEnrolledEvents.remove(event);
        }
        else {
            throw new IllegalArgumentException("Event doesn't exist in Entrant's joined events.");
        }
    }

    // Method for adding an event to the Pending events array (for events not accepted yet by entrant)
    public void addPendingEvent(Event event) {
        if (!currentPendingEvents.contains(event)) {
            currentPendingEvents.add(event);
        } else {
            throw new IllegalArgumentException("Event is already in Entrant's pending list.");
        }
    }

    // Method for removing events Entrant was picked for but did not respond to
    public void removePendingEvent(Event event) {
        if (currentPendingEvents.contains(event)) {
            currentPendingEvents.remove(event);
        } else {
            throw new IllegalArgumentException("Event does not exist in Entrant's pending events list.");
        }
    }

    public void addDeclinedEvent(Event event) {
        if (!currentDeclinedEvents.contains(event)) {
            currentDeclinedEvents.add(event);
        } else {
            throw new IllegalArgumentException("Event is already in Entrant's declined list.");
        }
    }

    public void removeDeclinedEvent(Event event) {
        if (currentDeclinedEvents.contains(event)) {
            currentDeclinedEvents.remove(event);
        } else {
            throw new IllegalArgumentException("Event does not exist in Entrant's declined events list.");
        }
    }

    public ArrayList<Event> getCurrentWaitlistedEvents() {
        return currentWaitlistedEvents;
    }

    public void setCurrentWaitlistedEvents(ArrayList<Event> currentWaitlistedEvents) {
        this.currentWaitlistedEvents = currentWaitlistedEvents;
    }

    public ArrayList<Event> getCurrentEnrolledEvents() {
        return currentEnrolledEvents;
    }

    public void setCurrentEnrolledEvents(ArrayList<Event> currentEnrolledEvents) {
        this.currentEnrolledEvents = currentEnrolledEvents;
    }

    public ArrayList<Event> getCurrentPendingEvents() {
        return currentPendingEvents;
    }

    public void setCurrentPendingEvents(ArrayList<Event> currentPendingEvents) {
        this.currentPendingEvents = currentPendingEvents;
    }

    public ArrayList<Event> getCurrentDeclinedEvents() {
        return currentDeclinedEvents;
    }

    public void setCurrentDeclinedEvents(ArrayList<Event> currentDeclinedEvents) {
        this.currentDeclinedEvents = currentDeclinedEvents;
    }

    /**
     * Returns the same user as an Organizer class.
     * <br>
     * isOrganizer must be true otherwise an exception is thrown.
     * @return The equivalent organizer class for this entrant
     */
    public Organizer returnOrganizer() {
        // TODO enforce the isOrganizer check
        Organizer organizer = new Organizer(this.getLastName(), this.getFirstName(), this.getEmail(), this.getPhoneNumber(), this.getDeviceId(), this.getRole(), true, this.isAdmin(), new ArrayList<Event>(), null);

        return organizer;
    }



}
