package com.example.trojanplanner;

import org.junit.Before;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;

public class EventTest {

    private SimpleDateFormat dateFormat;
    private Date startDateTime;
    private Date endDateTime;
    private Facility facility;

    @Before
    public void setup() {
        dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

        // Set start and end date
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 6, 9, 0); // Start time: Nov 6, 2024, 9:00 AM
        startDateTime = calendar.getTime();

        calendar.set(2024, Calendar.NOVEMBER, 6, 11, 0); // End time: Nov 6, 2024, 11:00 AM
        endDateTime = calendar.getTime();

        // Set up facility
        facility = new Facility("Gym", "1", "34.0522,-118.2437", null, null, null);
    }

    @Test
    public void testDailyRecurringEventWithEndDateAndOccurrences() {
        // Initialize two instances of the same recurring event, with different recurrence settings
        Event dailyEventWithEndDate = new Event("Daily Workout", "Gym session", 0.0f, facility, startDateTime, endDateTime, 2, 100L, 100L);
        Event dailyEventWithOccurrences = new Event("Daily Workout", "Gym session", 0.0f, facility, startDateTime, endDateTime, 2, 100L, 100L);

        // Set both events to recur on weekdays (Monday - Friday)
        for (String day : new String[]{"M", "T", "W", "R", "F"}) {
            dailyEventWithEndDate.addRecurrenceDay(day);
            dailyEventWithOccurrences.addRecurrenceDay(day);
        }

        // Scenario 1: Set dailyEventWithEndDate to end on a specific date
        dailyEventWithEndDate.setRecurring(true);
        dailyEventWithEndDate.setRecurrenceType(Event.RecurrenceType.UNTIL_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 20); // Recurrence end date: Nov 20, 2024
        Date recurrenceEndDate = calendar.getTime();
        dailyEventWithEndDate.setRecurrenceEndDate(recurrenceEndDate);

        // Generate occurrence dates based on end date
        List<Date> datesFromEndDate = dailyEventWithEndDate.getOccurrenceDates();

        // Scenario 2: Set dailyEventWithOccurrences to recur a specific number of times
        dailyEventWithOccurrences.setRecurring(true);
        dailyEventWithOccurrences.setRecurrenceType(Event.RecurrenceType.AFTER_OCCURRENCES);
        dailyEventWithOccurrences.setTotal_Occurrences(datesFromEndDate.size());

        // Get occurrence dates for both scenarios
        List<Date> datesFromOccurrences = dailyEventWithOccurrences.getOccurrenceDates();

        // Assertions
        assertEquals("Occurrence counts do not match", datesFromEndDate.size(), datesFromOccurrences.size());
        assertEquals("End dates do not match",
                dateFormat.format(dailyEventWithEndDate.getRecurrenceEndDate()),
                dateFormat.format(dailyEventWithOccurrences.getRecurrenceEndDate()));
        assertEquals("Occurrence dates do not match", datesFromEndDate, datesFromOccurrences);
        assertEquals("Recurrence types do not match", dailyEventWithEndDate.getRecurrenceType(), dailyEventWithOccurrences.getRecurrenceType());
    }

    @Test
    public void testAddAndRemoveParticipant() {
        Event event = new Event("Morning Yoga", "Relaxing session", 0.0f, facility, startDateTime, endDateTime, 2, 30L, 10L);
        Entrant entrant = new Entrant("Doe", "John", "johndoe@example.com", "1234567890", "device123", "participant", false, false);

        // Test adding a participant
        boolean added = event.addParticipant(entrant);
        assertTrue("Participant should be added successfully", added);
        assertTrue("Participant should be in the waiting list", event.getWaitingList().contains(entrant));

        // Test removing the participant
        boolean removed = event.removeParticipant(entrant);
        assertTrue("Participant should be removed successfully", removed);
        assertFalse("Participant should not be in the waiting list", event.getWaitingList().contains(entrant));
    }

    @Test
    public void testRecurringEventWithTotalOccurrences() {
        Event event = new Event("Morning Yoga", "Relaxing session", 0.0f, facility, startDateTime, endDateTime, 2, 30L, 10L);
        event.setRecurring(true);
        event.setRecurrenceType(Event.RecurrenceType.AFTER_OCCURRENCES);
        event.setTotal_Occurrences(10);

        for (String day : new String[]{"M", "T", "W", "R", "F"}) {
            event.addRecurrenceDay(day);
        }

        // Generate occurrence dates and assert the count
        List<Date> occurrences = event.getOccurrenceDates();
        assertEquals("Event should have 10 occurrences", 10, occurrences.size());
    }

    @Test
    public void testStatusUpdateBasedOnDates() {
        // Set specific times for start, end, and current dates
        Calendar calendar = Calendar.getInstance();

        // Set startDateTime to Nov 6, 2024, 9:00 AM
        calendar.set(2024, Calendar.NOVEMBER, 6, 9, 0, 0);
        Date startDateTime = calendar.getTime();

        // Set endDateTime to Nov 6, 2024, 11:00 AM
        calendar.set(2024, Calendar.NOVEMBER, 6, 11, 0, 0);
        Date endDateTime = calendar.getTime();

        // Set currentDate to Nov 6, 2024, 10:00 AM (between start and end times)
        calendar.set(2024, Calendar.NOVEMBER, 6, 10, 0, 0);
        Date currentDate = calendar.getTime();

        // Create an event with start and end times
        Event event = new Event("Test Event", "This is a test event description.", 10.0f, facility, startDateTime, endDateTime, 0, 100L, 100L);

        // Use the overloaded updateStatus() with the specified currentDate
        event.updateStatus(currentDate);

        // Assert that the status is "ongoing" when currentDate is between start and end times
        assertEquals("Status should be 'ongoing'", "ongoing", event.getStatus());
    }
    @Test
    public void testIsWaitlistFull() {
        // Assume the maximum capacity of the waitlist is set to 2 for testing
        Event event = new Event("Morning Yoga", "Relaxing session", 0.0f, facility, startDateTime, endDateTime, 2, 30L, 10L);
        event.addToWaitlist(new Entrant("Smith", "Alice", "alice@example.com", "1112223333", "device456", "participant", false, false));
        event.addToWaitlist(new Entrant("Johnson", "Bob", "bob@example.com", "4445556666", "device789", "participant", false, false));
        assertTrue("Waitlist should be full", event.isWaitlistFull(2));
    }

    @Test
    public void testIsRegistrationOpen() {
        Event event = new Event("Morning Yoga", "Relaxing session", 0.0f, facility, startDateTime, endDateTime, 2, 30L, 10L);
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

    @Test
    public void testRecurringEventWithEndDate() {
        Event event = new Event("Morning Yoga", "Relaxing session", 0.0f, facility, startDateTime, endDateTime, 2, 30L, 10L);
        // Set event recurrence to weekdays (Monday - Friday) ending on a specific date
        event.setRecurring(true);
        event.setRecurrenceType(Event.RecurrenceType.UNTIL_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 20); // Recurrence end date: Nov 20, 2024
        Date recurrenceEndDate = calendar.getTime();
        event.setRecurrenceEndDate(recurrenceEndDate);

        for (String day : new String[]{"M", "T", "W", "R", "F"}) {
            event.addRecurrenceDay(day);
        }

        // Generate occurrence dates and verify that end date is respected
        List<Date> occurrences = event.getOccurrenceDates();
        assertTrue("Occurrences should not extend beyond end date",
                occurrences.get(occurrences.size() - 1).before(recurrenceEndDate) ||
                        occurrences.get(occurrences.size() - 1).equals(recurrenceEndDate));
    }

}
