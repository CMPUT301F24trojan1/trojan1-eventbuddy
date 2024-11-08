package com.example.trojanplanner;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojanplanner.model.ConcreteEvent;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.view.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.containsString;

import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;



@RunWith(AndroidJUnit4.class)
public class WaitlistFragmentTest {

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
    //TO DO: make mcok filrebase
    @Test
    public void testWaitlistDisplay() {
        // Launch MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            // Prepare fragment arguments
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", testEvent);

            // Navigate to waitlistFragment using NavController
            NavController navController = Navigation.findNavController(R.id.nav_host_fragment_activity_main, );
            navController.navigate(R.id.waitlistFragment, bundle);
        });

        // Verify each waitlist entrant's name and device ID is displayed
        onView(withText(containsString("Alice"))).check(matches(isDisplayed()));
        onView(withText(containsString("Device123"))).check(matches(isDisplayed()));

        onView(withText(containsString("Bob"))).check(matches(isDisplayed()));
        onView(withText(containsString("Device456"))).check(matches(isDisplayed()));

        onView(withText(containsString("Charlie"))).check(matches(isDisplayed()));
        onView(withText(containsString("Device789"))).check(matches(isDisplayed()));

        onView(withText(containsString("Dana"))).check(matches(isDisplayed()));
        onView(withText(containsString("Device101"))).check(matches(isDisplayed()));

        onView(withText(containsString("Eve"))).check(matches(isDisplayed()));
        onView(withText(containsString("Device102"))).check(matches(isDisplayed()));

        onView(withText(containsString("Frank"))).check(matches(isDisplayed()));
        onView(withText(containsString("Device103"))).check(matches(isDisplayed()));
    }
}
