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

import com.example.trojanplanner.model.ConcreteEvent;

import com.example.trojanplanner.model.Facility;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.content.Context;
import android.content.res.Resources;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class EventTest {

    private SimpleDateFormat dateFormat;

//    @Before
//    public void setup() {
//        // Initialize the date format for printing
//        dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
//    }


    private Date startDateTime;
    private Date endDateTime;
    private Facility facility;
    private Context mockContext;
    private Resources mockResources;
    private Bitmap defaultBitmap;

    @Before
    public void setup() {
        dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

        //set start and end date
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 6, 9, 0); // Start time: Nov 6, 2024, 9:00 AM
        startDateTime = calendar.getTime();

        calendar.set(2024, Calendar.NOVEMBER, 6, 11, 0); // End time: Nov 6, 2024, 11:00 AM
        endDateTime = calendar.getTime();

        // Set up facility coordinates
        facility = new Facility("Gym", "1", "34.0522,-118.2437", null, null, null);

    }

    @Test
    public void testDailyRecurringEventWithEndDateAndOccurrences() {
        // Set up start and end times for the event on Nov 6, 2024
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 6, 9, 0); // Start time: Nov 6, 2024, 9:00 AM
        Date startDateTime = calendar.getTime();

        calendar.set(2024, Calendar.NOVEMBER, 6, 11, 0); // End time: Nov 6, 2024, 11:00 AM
        Date endDateTime = calendar.getTime();

        // Initialize two instances of the same recurring event, with different recurrence settings
        Event dailyEventWithEndDate = new ConcreteEvent("Daily Workout", "Gym session", 0, "Gym", startDateTime, endDateTime);
        Event dailyEventWithOccurrences = new ConcreteEvent("Daily Workout", "Gym session", 0, "Gym", startDateTime, endDateTime);

        // Set both events to recur on weekdays (Monday - Friday)
        for (String day : new String[]{"M", "T", "W", "R", "F"}) {
            dailyEventWithEndDate.addRecurrenceDay(day);
            dailyEventWithOccurrences.addRecurrenceDay(day);
        }

        // Scenario 1: Set dailyEventWithEndDate to end on a specific date
        dailyEventWithEndDate.setRecurring(true);
        dailyEventWithEndDate.setRecurrenceType(Event.RecurrenceType.UNTIL_DATE);
        calendar.set(2024, Calendar.NOVEMBER, 20); // Recurrence end date: Nov 20, 2024
        Date recurrenceEndDate = calendar.getTime();
        dailyEventWithEndDate.setRecurrenceEndDate(recurrenceEndDate);

        // Generate occurrence dates based on end date
        List<Date> datesFromEndDate = dailyEventWithEndDate.getOccurrenceDates();

        // Scenario 2: Set dailyEventWithOccurrences to recur a specific number of times
        dailyEventWithOccurrences.setRecurring(true);
        dailyEventWithOccurrences.setRecurrenceType(Event.RecurrenceType.AFTER_OCCURRENCES);
        dailyEventWithOccurrences.setTotal_Occurrences(datesFromEndDate.size()); // Match occurrences to dates from dailyEventWithEndDate

        // Get occurrence dates for both scenarios
        List<Date> datesFromOccurrences = dailyEventWithOccurrences.getOccurrenceDates();

        // Convert dailyEventWithOccurrences to UNTIL_DATE recurrence type to standardize
        dailyEventWithOccurrences.convertToEndDateType();

        // Print details for both events
        System.out.println("Daily Recurring Event (End Date):");
        printEventDetails(dailyEventWithEndDate, datesFromEndDate);

        System.out.println("Daily Recurring Event (Occurrences):");
        printEventDetails(dailyEventWithOccurrences, datesFromOccurrences);

        // Assertions to verify that both events are equivalent
        assertEquals("Occurrence counts do not match", datesFromEndDate.size(), datesFromOccurrences.size());
        assertEquals("End dates do not match",
                dateFormat.format(dailyEventWithEndDate.getRecurrenceEndDate()),
                dateFormat.format(dailyEventWithOccurrences.getRecurrenceEndDate()));
        assertEquals("Occurrence dates do not match", datesFromEndDate, datesFromOccurrences);
        assertEquals("Recurrence types do not match", dailyEventWithEndDate.getRecurrenceType(), dailyEventWithOccurrences.getRecurrenceType());
    }

    private void printEventDetails(Event event, List<Date> occurrenceDates) {
        System.out.println("Event: " + event.getName());
        System.out.println("Facility: " + event.getFacility());
        System.out.println("Starts: " + dateFormat.format(event.getStartDateTime()));
        System.out.println("Ends: " + dateFormat.format(event.getEndDateTime()));
        System.out.println("Recurring: " + (event.isRecurring() ? "Yes" : "No"));
        System.out.println("Recurs on: " + event.getRecurrenceDays());
        System.out.println("Recurrence Type: " + event.getRecurrenceType());
        System.out.println("Recurrence End Date: " + dateFormat.format(event.getRecurrenceEndDate()));
        System.out.println("Total Occurrences: " + occurrenceDates.size());

        System.out.println("Occurrence Dates:");
        for (Date date : occurrenceDates) {
            System.out.println(dateFormat.format(date));
        }
        System.out.println();
    }

    @Test
    public void testAddAndRemoveParticipant() {
        Event event = new ConcreteEvent("Morning Yoga", "Relaxing session", 0, "Gym", startDateTime, endDateTime);
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
    public void testIsWaitlistFull() {
        // Assume the maximum capacity of the waitlist is set to 2 for testing
        Event event = new ConcreteEvent("Morning Yoga", "Relaxing session", 0, "Gym", startDateTime, endDateTime);
        event.addToWaitlist(new Entrant("Smith", "Alice", "alice@example.com", "1112223333", "device456", "participant", false, false));
        event.addToWaitlist(new Entrant("Johnson", "Bob", "bob@example.com", "4445556666", "device789", "participant", false, false));
        assertTrue("Waitlist should be full", event.isWaitlistFull(2));
    }




//    @Test
//    public void testValidateGeolocation() {
//        Event event = new ConcreteEvent("Morning Yoga", "Relaxing session", "Gym", startDateTime, endDateTime);
//        // Setup user location near the facility's coordinates for validation
//        UserLocation userLocationNearby = new UserLocation("34.0523", "-118.2438");
//
//        // Test for nearby user
//        boolean isNearby = event.validateGeolocation(userLocationNearby, facility);
//        assertTrue("Geolocation validation should pass for nearby location", isNearby);
//
//        // Setup user location far from the facility's coordinates
//        UserLocation userLocationFar = new UserLocation("40.7128", "-74.0060"); // New York City
//        boolean isFar = event.validateGeolocation(userLocationFar, facility);
//        assertFalse("Geolocation validation should fail for far location", isFar);
//    }

    @Test
    public void testRecurringEventWithTotalOccurrences() {
        Event event = new ConcreteEvent("Morning Yoga", "Relaxing session", 0, "Gym", startDateTime, endDateTime);
        // Set event recurrence to weekdays (Monday - Friday) with 10 occurrences
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
    public void testRecurringEventWithEndDate() {
        Event event = new ConcreteEvent("Morning Yoga", "Relaxing session", 0, "Gym", startDateTime, endDateTime);
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

    @Test
    public void testIsRegistrationOpen() {
        Event event = new ConcreteEvent("Morning Yoga", "Relaxing session", 0, "Gym", startDateTime, endDateTime);
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
        Event event = new ConcreteEvent("Morning Yoga", "Relaxing session", 0, "Gym", startDateTime, endDateTime);

        // Use the overloaded updateStatus() with the specified currentDate
        event.updateStatus(currentDate);

        // Assert that the status is "ongoing" when currentDate is between start and end times
        assertEquals("Status should be 'ongoing'", "ongoing", event.getStatus());

        // Set endDateTime to a past time (before currentDate) to simulate a finished event
        calendar.set(2024, Calendar.NOVEMBER, 6, 9, 30, 0); // 9:30 AM, before 10:00 AM currentDate
        event.setEndDateTime(calendar.getTime());

        // Update the status again with currentDate and check if it is "finished"
        event.updateStatus(currentDate);
        assertEquals("Status should be 'finished'", "finished", event.getStatus());
    }


}
