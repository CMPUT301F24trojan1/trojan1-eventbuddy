package com.example.trojanplanner;

import org.junit.Before;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

import com.example.trojanplanner.model.Event;
import org.junit.Before;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.ConcreteEvent;

public class EventTest {

    private SimpleDateFormat dateFormat;

    @Before
    public void setup() {
        // Initialize the date format for printing
        dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
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
        Event dailyEventWithEndDate = new ConcreteEvent("Daily Workout", "Gym session", "Gym", startDateTime, endDateTime);
        Event dailyEventWithOccurrences = new ConcreteEvent("Daily Workout", "Gym session", "Gym", startDateTime, endDateTime);

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
}
