package com.example.trojanplanner;

import com.example.trojanplanner.model.ConcreteEvent;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the functionality of the event's waitlist in the {@link Event} class.
 * This test class ensures the correct handling of entrants in the waitlist,
 * verifying that entrants are added, and the list size and specific entrants are accurately recorded.
 *
 */
public class WaitlistTest {

    private ConcreteEvent testEvent;

    /**
     * Setup method to initialize a sample event and a list of entrants for the waitlist.
     * This method is executed before each test to ensure the event and waitlist are set up.
     */
    @Before
    public void setUp() {
        // Initialize the Event with required fields
        String name = "Test Event";
        String description = "This is a test event description.";
        Date startDateTime = new Date();
        Date endDateTime = new Date(startDateTime.getTime() + 3600000);

        // Define test facility and event details
        float price = 10.0f;
        Facility facility = new Facility("Gym", "1", "34.0522,-118.2437", null, null, null);
        int daysLeftToRegister = 2;
        Long totalSpots = 30L;
        Long availableSpots = 10L;

        // Create Event object using the correct constructor
        testEvent = new Event(name, description, price, facility, startDateTime, endDateTime, daysLeftToRegister, totalSpots, availableSpots);

        // Add sample entrants to the waitlist
        List<Entrant> waitlistEntrants = new ArrayList<>();
        waitlistEntrants.add(new Entrant("Johnson", "Alice", "alice@example.com", "123-456-7890", "Device123", "Guest", false, false));
        waitlistEntrants.add(new Entrant("Smith", "Bob", "bob@example.com", "234-567-8901", "Device456", "Guest", false, false));
        waitlistEntrants.add(new Entrant("Brown", "Charlie", "charlie@example.com", "345-678-9012", "Device789", "Guest", false, false));
        waitlistEntrants.add(new Entrant("Taylor", "Dana", "dana@example.com", "456-789-0123", "Device101", "Guest", false, false));
        waitlistEntrants.add(new Entrant("Adams", "Eve", "eve@example.com", "567-890-1234", "Device102", "Guest", false, false));
        waitlistEntrants.add(new Entrant("Williams", "Frank", "frank@example.com", "678-901-2345", "Device103", "Guest", false, false));

        testEvent.setWaitingList(new ArrayList<>(waitlistEntrants));
    }

    /**
     * Test case to verify the correct handling of waitlist entries.
     * This test ensures that the number of entrants in the waitlist matches expectations,
     * and that specific entrants are correctly present in the list based on their first name and device ID.
     */
    @Test
    public void testWaitlistEntries() {
        // Verify that the event has the correct number of waitlisted entrants
        assertEquals("The number of waitlist entries should be 6", 6, testEvent.getWaitingList().size());

        // Verify the presence of specific entrants by name and device ID
        assertTrue("Waitlist should contain Alice with Device123", testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Alice") && entrant.getDeviceId().equals("Device123")
        ));

        assertTrue("Waitlist should contain Bob with Device456", testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Bob") && entrant.getDeviceId().equals("Device456")
        ));

        assertTrue("Waitlist should contain Charlie with Device789", testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Charlie") && entrant.getDeviceId().equals("Device789")
        ));

        assertTrue("Waitlist should contain Dana with Device101", testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Dana") && entrant.getDeviceId().equals("Device101")
        ));

        assertTrue("Waitlist should contain Eve with Device102", testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Eve") && entrant.getDeviceId().equals("Device102")
        ));

        assertTrue("Waitlist should contain Frank with Device103", testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Frank") && entrant.getDeviceId().equals("Device103")
        ));
    }
}
