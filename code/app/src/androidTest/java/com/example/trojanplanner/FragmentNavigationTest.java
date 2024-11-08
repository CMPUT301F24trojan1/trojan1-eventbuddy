package com.example.trojanplanner;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.trojanplanner.view.MainActivity;
import com.example.trojanplanner.view.ProfileActivity;
import com.example.trojanplanner.view.QRActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FragmentNavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testNavigationToEventsFragment() {
        // Click on the events tab in the BottomNavigationView
        Espresso.onView(withId(R.id.eventsFragment)).perform(click());

        // Check if the EventsFragment is displayed
        Espresso.onView(withText("Event 1")).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationToQRActivity() {
        // Click on the QR tab in the BottomNavigationView
        Espresso.onView(withId(R.id.qrActivity)).perform(click());

        // Check if the QRActivity starts (check a specific view or feature of QRActivity)
        Espresso.onView(withId(R.id.barcode_scanner)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationToProfileActivity() {
        // Click on the profile tab in the BottomNavigationView
        Espresso.onView(withId(R.id.profileActivity)).perform(click());

        // Check if the ProfileActivity starts (check a specific view or feature of ProfileActivity)
        Espresso.onView(withId(R.id.profile_image)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationFromProfileActivity() {
        // Start ProfileActivity
        Intent profileIntent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), ProfileActivity.class);
        mainActivityRule.getActivity().startActivity(profileIntent);

        // Click on the home navigation item in ProfileActivity's BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(click());

        // Check if it navigates back to MainActivity by checking if NavHostFragment is displayed
        Espresso.onView(withId(R.id.nav_host_fragment_activity_main)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationFromQRActivity() {
        // Start QRActivity
        Intent qrIntent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), QRActivity.class);
        mainActivityRule.getActivity().startActivity(qrIntent);

        // Click on the profile navigation item in QRActivity's BottomNavigationView
        Espresso.onView(withId(R.id.profileActivity)).perform(click());

        // Check if it navigates to ProfileActivity
        Espresso.onView(withId(R.id.profile_image)).check(matches(ViewMatchers.isDisplayed()));
    }

    @After
    public void tearDown() {
        Intents.release(); // Clean up after the test
    }
}