package com.example.trojanplanner;
import androidx.fragment.app.testing.FragmentScenario;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojanplanner.R;
import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.ConcreteEvent;
import com.example.trojanplanner.model.Entrant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

// this is for testing US 01.01.01 - As an entrant, I want to join the waiting list for a specific event.
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Entrant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;



import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Date;
import java.util.HashSet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Date;
import java.util.HashSet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashSet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashSet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EventDetailsFragmentTest {

    private Event testEvent;
    private Entrant testEntrant;

    @Mock
    private Database mockDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Define the test facility and event
        Facility testFacility = new Facility("Gym", "F001", "Main Street", null, "facility_image_path", null);
        testEvent = new Event(
                "Morning Yoga", "Relaxing yoga session", "", 15.0f, testFacility,
                new Date(), new Date(System.currentTimeMillis() + 3600000),
                2, 30L, 10L
        );

        // Set recurrence days for the event
//        HashSet<String> recurrenceDays = new ArrayList<>();
//        recurrenceDays.add("Monday");
//        recurrenceDays.add("Wednesday");
        testEvent.addRecurrenceDay("Monday");
        testEvent.addRecurrenceDay("Wednesday");

        // Define a test entrant
        testEntrant = new Entrant("Doe", "Jane", "jane.doe@example.com", "123-456-7890", "Device001", "Guest", false, false);

        // Mock the database insert operation
        doNothing().when(mockDatabase).insertEvent(Mockito.any(), Mockito.any(), Mockito.eq(testEvent));
    }

    @Test
    public void testEventDetailsDisplay() {
        // Launch the fragment with the event as an argument
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", testEvent);

        FragmentScenario<EventDetailsFragment> fragmentScenario =
                FragmentScenario.launchInContainer(EventDetailsFragment.class, bundle);

        // Verify that the UI displays the correct event details
        onView(withId(R.id.eventNameTextView)).check(matches(withText("Morning Yoga")));
        onView(withId(R.id.eventLocationTextView)).check(matches(withText("F001")));
        onView(withId(R.id.eventDescriptionTextView)).check(matches(withText("Relaxing yoga session")));

        // Format date range for display
        String expectedDateRange = "Start date to end date"; // Use formatted date range here
        onView(withId(R.id.eventDateTextView)).check(matches(withText(expectedDateRange)));

        // Check recurrence days display
        onView(withId(R.id.recurringDatesTextView)).check(matches(withText("Monday, Wednesday")));
    }

    @Test
    public void testAddEntrantToWaitlist() {
        // Launch fragment with the event as an argument
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", testEvent);

        FragmentScenario<EventDetailsFragment> fragmentScenario =
                FragmentScenario.launchInContainer(EventDetailsFragment.class, bundle);

        fragmentScenario.onFragment(fragment -> {
            // Set the database and entrant in the fragment (requires adding setter methods in EventDetailsFragment)
            fragment.setDatabase(mockDatabase);
            fragment.setEntrant(testEntrant);
        });

        // Click the "Enter Now" button
        onView(withId(R.id.button_enter_now)).perform(click());

        // Verify database interaction to add entrant
        verify(mockDatabase).insertEvent(Mockito.any(), Mockito.any(), Mockito.eq(testEvent));
    }
}


//
//
//@RunWith(AndroidJUnit4.class)
//@MediumTest
//public class EventDetailsFragmentTest {
//
//    private Event testEvent;
//
//    @Before
//    public void setUp() {
//        // Define test facility
//        Facility testFacility = new Facility("Gym", "F001", "Main Street", null, "facility_image_path", null);
//
//        // Define fixed start and end dates for the event (e.g., Nov 8, 2024, 03:00 to 04:00)
//        Date startDateTime = new Date(1700000000000L); // Replace with desired timestamp
//        Date endDateTime = new Date(1700003600000L);   // One hour later
//
//        // Define test event with full details
//        testEvent = new Event(
//                "Morning Yoga",                               // Event name
//                "Relaxing yoga session to start the day",    // Description
//                15.0f,                                        // Price
//                testFacility,                                 // Facility instance
//                startDateTime,                                // Fixed start date and time
//                endDateTime,                                  // Fixed end date and time
//                2,                                            // Days left to register
//                30L,                                          // Total spots
//                10L                                           // Available spots
//        );
//
//        // Set recurrence days for the event
//        HashSet<String> recurrenceDays = new HashSet<>();
//        recurrenceDays.add("Monday");
//        recurrenceDays.add("Wednesday");
//        testEvent.setRecurrenceDays(recurrenceDays);
//    }
//
//    @Test
//    public void testEventDetailsDisplay() {
//        // Launch the EventDetailsFragment with the test Event as an argument
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("event", testEvent);
//
//        FragmentScenario<EventDetailsFragment> fragmentScenario =
//                FragmentScenario.launchInContainer(EventDetailsFragment.class, bundle);
//
//        // Verify that event details are displayed correctly
//        onView(withId(R.id.eventNameTextView)).check(matches(withText("Morning Yoga")));
//        onView(withId(R.id.eventLocationTextView)).check(matches(withText("F001")));
//        onView(withId(R.id.eventDescriptionTextView)).check(matches(withText("Relaxing yoga session to start the day")));
//
//        // Format start and end dates
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String expectedDateRange = dateFormat.format(new Date(1700000000000L)) + " - " + dateFormat.format(new Date(1700003600000L));
//
//        // Check start and end dates display
//        onView(withId(R.id.eventDateTextView)).check(matches(withText(expectedDateRange)));
//
//        // Check recurrence days display
//        onView(withId(R.id.recurringDatesTextView)).check(matches(withText("Monday, Wednesday")));
//    }
//}
