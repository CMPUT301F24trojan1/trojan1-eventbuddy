package com.example.trojanplanner;
import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class EventDetailsLogicTest {

    private Event testEvent;
    private Entrant testEntrant;
    private Database database;
    private EventDetailsFragment fragment;

    @Before
    public void setUp() {
        // Instantiate the Database directly
        database = Database.getDB();

        // Set up test event and entrant objects
        Facility testFacility = new Facility("Gym", "F001", "Main Street", null, "facility_image_path", null);
        testEvent = new Event("Morning Yoga", "Relaxing yoga session", "Morning Yoga type thing", 15.0f, testFacility,
                new Date(), new Date(System.currentTimeMillis() + 3600000),
                2, 30L, 10L);

        testEntrant = new Entrant("Doe", "Jane", "jane.doe@example.com", "123-456-7890", "Device001", "Guest", false, false);

        // Initialize fragment and set database
        fragment = EventDetailsFragment.newInstance(testEvent, testEntrant);
        fragment.setDatabase(database);
    }

    @Test
    public void testJoinWaitlist() {
        // Ensure the entrant is not on the waitlist initially
        assertFalse(testEvent.getWaitingList().contains(testEntrant));

        // Simulate joining the waitlist
        fragment.setEntrant(testEntrant);
        fragment.setEvent(testEvent);
        fragment.joinWaitlist();

        // Verify that the entrant is added to the waitlist
        assertTrue(testEvent.getWaitingList().contains(testEntrant));
    }

    @Test
    public void testLeaveWaitlist() {
        // Add entrant to the event's waiting list to simulate joining first
        testEvent.addParticipant(testEntrant);
        assertTrue(testEvent.getWaitingList().contains(testEntrant));

        // Simulate leaving the waitlist
        fragment.setEntrant(testEntrant);
        fragment.setEvent(testEvent);
        fragment.leaveWaitlist();

        // Verify that the entrant is removed from the waitlist
        assertFalse(testEvent.getWaitingList().contains(testEntrant));
    }
}
