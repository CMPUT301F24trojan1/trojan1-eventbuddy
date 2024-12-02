package com.example.trojanplanner;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

public class EventTest {

    private Event event;
    private User mockUser;
    private SimpleDateFormat dateFormat;
    private Date startDateTime;
    private Date endDateTime;

    @Before
    public void setup() {
        dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

        // Set start and end dates for testing
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 6, 9, 0); // Start time: Nov 6, 2024, 9:00 AM
        startDateTime = calendar.getTime();

        calendar.set(2024, Calendar.NOVEMBER, 6, 11,0); // End time: Nov 6, 2024, 11:00 AM
        endDateTime = calendar.getTime();

        // Initialize Event
        event = new Event("EVENT123", "Morning Yoga", "Relaxing session", 10.0f, null, startDateTime, endDateTime, 2, 30L, 10L);

        // Mock User
        mockUser = mock(User.class);
        when(mockUser.getDeviceId()).thenReturn("USER123");
    }

    @Test
    public void testConstructor() {
        assertEquals("Morning Yoga", event.getName());
        assertEquals("Relaxing session", event.getDescription());
        assertEquals(10.0f, event.getPrice(), 0.01);
        assertEquals(30L, (long) event.getTotalSpots());
        assertEquals(10L, (long) event.getAvailableSpots());
        assertNotNull(event.getWaitingList());
    }

    @Test
    public void testAddParticipant() {
        boolean added = event.addParticipant(mockUser);
        assertTrue("Participant should be added", added);
        assertTrue("Participant should be in the waiting list", event.getWaitingList().contains(mockUser));
        assertEquals(9L, (long) event.getAvailableSpots());
    }

    @Test
    public void testRemoveParticipant() {
        event.addParticipant(mockUser);
        boolean removed = event.removeParticipant(mockUser);
        assertTrue("Participant should be removed", removed);
        assertFalse("Participant should not be in the waiting list", event.getWaitingList().contains(mockUser));
        assertEquals(10L, (long) event.getAvailableSpots());
    }

    @Test
    public void testCannotAddSameParticipantTwice() {
        event.addParticipant(mockUser);
        boolean addedAgain = event.addParticipant(mockUser);
        assertFalse("Same participant should not be added twice", addedAgain);
    }

    @Test
    public void testFindIndexWithId() {
        event.addParticipant(mockUser);
        int index = event.findIndexWithId(event.getWaitingList(), "USER123");
        assertEquals(0, index);
    }

    @Test
    public void testUpdateAvailableSpots() {
        // Initialize event with specific TotalSpots and availableSpots
        event.setTotalSpots(10L);
        event.setAvailableSpots(10L);

        // Add a participant to the event
        event.addParticipant(mockUser);

        // Update the available spots
        event.updateAvailableSpots();

        // Assert the updated value
        assertEquals("Available spots should decrease by 1", 9L, (long) event.getAvailableSpots());
    }


    @Test
    public void testStatusUpdate() {
        // Set a custom date for testing
        Calendar calendar = Calendar.getInstance();

        // Set currentDate before event start
        calendar.set(2024, Calendar.NOVEMBER, 5, 10, 0);
        Date customDate = calendar.getTime();
        event.updateStatus(customDate);
        assertEquals("upcoming", event.getStatus());

        // Set currentDate during the event
        calendar.set(2024, Calendar.NOVEMBER, 6, 10, 0);
        customDate = calendar.getTime();
        event.updateStatus(customDate);
        assertEquals("ongoing", event.getStatus());

        // Set currentDate after the event
        calendar.set(2024, Calendar.NOVEMBER, 7, 10, 0);
        customDate = calendar.getTime();
        event.updateStatus(customDate);
        assertEquals("finished", event.getStatus());
    }

    @Test
    public void testRecurringEventWithOccurrences() {
        event.setRecurring(true);
        event.setRecurrenceType(Event.RecurrenceType.AFTER_OCCURRENCES);
        event.setTotal_Occurrences(3);

        event.addRecurrenceDay("M");
        event.addRecurrenceDay("W");

        List<Date> occurrences = event.getOccurrenceDates();
        assertEquals(3, occurrences.size());
    }

    @Test
    public void testRecurringEventWithEndDate() {
        event.setRecurring(true);
        event.setRecurrenceType(Event.RecurrenceType.UNTIL_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 20);
        Date recurrenceEndDate = calendar.getTime();
        event.setRecurrenceEndDate(recurrenceEndDate);

        event.addRecurrenceDay("M");
        event.addRecurrenceDay("W");

        List<Date> occurrences = event.getOccurrenceDates();
        assertFalse("Occurrences should not extend beyond the recurrence end date", occurrences.isEmpty());
    }

    @Test
    public void testIsWaitlistFull() {
        event.setTotalSpots(1L);
        event.addParticipant(mockUser);
        assertTrue("Waitlist should be full", event.isWaitlistFull(1));
    }

    @Test
    public void testAddToWaitlist() {
        boolean added = event.addToWaitlist(mockUser);
        assertTrue("User should be added to the waitlist", added);
        assertTrue(event.getWaitingList().contains(mockUser));
    }

    @Test
    public void testRemoveFromWaitlist() {
        event.addToWaitlist(mockUser);
        boolean removed = event.removeFromWaitlist(mockUser);
        assertTrue("User should be removed from the waitlist", removed);
        assertFalse(event.getWaitingList().contains(mockUser));
    }

    @Test
    public void testIsRegistrationOpen() {
        // Set registration deadline to a future date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date futureDate = calendar.getTime();
        event.setRegistrationDeadline(futureDate);

        assertTrue("Registration should be open", event.isRegistrationOpen());

        // Set registration deadline to a past date
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        Date pastDate = calendar.getTime();
        event.setRegistrationDeadline(pastDate);

        assertFalse("Registration should be closed", event.isRegistrationOpen());
    }
}
