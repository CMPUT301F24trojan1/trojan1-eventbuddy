package com.example.trojanplanner;

import com.example.trojanplanner.model.ConcreteEvent;
import com.example.trojanplanner.model.Entrant;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WaitlistTest {

    private ConcreteEvent testEvent;

    @Before
    public void setUp() {
        // Initialize the Event with required fields
        String name = "Test Event";
        String description = "This is a test event description.";
        String facility = "Test Facility";
        Date startDateTime = new Date();
        Date endDateTime = new Date(startDateTime.getTime() + 3600000);

        testEvent = new ConcreteEvent(name, description, 10.0f, facility, startDateTime, endDateTime);

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

    @Test
    public void testWaitlistEntries() {
        // Verify that the event has the correct number of waitlisted entrants
        assertEquals(6, testEvent.getWaitingList().size());

        // Verify the presence of specific entrants by name and device ID
        assertTrue(testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Alice") && entrant.getDeviceId().equals("Device123")
        ));

        assertTrue(testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Bob") && entrant.getDeviceId().equals("Device456")
        ));

        assertTrue(testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Charlie") && entrant.getDeviceId().equals("Device789")
        ));

        assertTrue(testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Dana") && entrant.getDeviceId().equals("Device101")
        ));

        assertTrue(testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Eve") && entrant.getDeviceId().equals("Device102")
        ));

        assertTrue(testEvent.getWaitingList().stream().anyMatch(
                entrant -> entrant.getFirstName().equals("Frank") && entrant.getDeviceId().equals("Device103")
        ));
    }
}
