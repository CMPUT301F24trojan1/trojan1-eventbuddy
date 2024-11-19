package com.example.trojanplanner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.trojanplanner.events.EmptyEventsFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EmptyEventsFragmentTest {

    @Test
    public void testNoEventsMessageDisplayed() {
        // Launch the EmptyEventsFragment
        FragmentScenario.launchInContainer(EmptyEventsFragment.class);

        // Verify that the "No events found" message is displayed
        onView(withId(R.id.messageTextView))
                .check(matches(withText("No events found. Come back here to see events you've created or joined!")));
    }

    //@Test
    //public void testBecomeOrganizerButtonNavigates() {
        // Launch the EmptyEventsFragment
        //FragmentScenario.launchInContainer(EmptyEventsFragment.class);

        // Perform a click on the "Become Organizer" button
        //onView(withId(R.id.becomeOrganizerButton)).perform(click());

        // Check that the navigation to OrganizerRegistrationFragment occurred
        //onView(withId(R.id.organizerRegistrationFragment)).check(matches(isDisplayed()));
}

