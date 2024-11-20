package com.example.trojanplanner;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
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

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FragmentNavigationTest {

    private ActivityScenario<MainActivity> activityScenario;
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Launch MainActivity before each test
        activityScenario = ActivityScenario.launch(MainActivity.class);
        Intents.init(); // Initialize Espresso Intents
    }

    @After
    public void tearDown() {
        // Release Espresso Intents after each test
        Intents.release();
        if (activityScenario != null) {
            activityScenario.close();
        }
    }

    @Test
    public void testNavigationFromEventsFragment() {

        //Click on the events tab in the BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(ViewActions.click());
        //Check if the EventsFragment is displayed
        Espresso.onView(withId(R.id.empty_events_fragment_parent)).check(matches(ViewMatchers.isDisplayed()));

        //Shift to qr fragment
        Espresso.onView(withId(R.id.qrActivity)).perform(ViewActions.click());
        //Check if the qr fragment is displayed
        Espresso.onView(withId(R.id.activity_qr_parent)).check(matches(ViewMatchers.isDisplayed()));
        //Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        //Shift to profile fragment
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());
        //Check if the profile fragment is displayed
        Espresso.onView(withId(R.id.profile_fragment_parent)).check(matches(ViewMatchers.isDisplayed()));

    }

    @Test
    public void testNavigationFromQRFragment() {

        Espresso.onView(withId(R.id.qrActivity)).perform(ViewActions.click());
        //Check if the qr fragment is displayed
        Espresso.onView(withId(R.id.activity_qr_parent)).check(matches(ViewMatchers.isDisplayed()));
        //Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        //Shift to profile fragment
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());
        //Check if the profile fragment is displayed
        Espresso.onView(withId(R.id.profile_fragment_parent)).check(matches(ViewMatchers.isDisplayed()));

        //Click on the events tab in the BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(ViewActions.click());
        //Check if the EventsFragment is displayed
        Espresso.onView(withId(R.id.empty_events_fragment_parent)).check(matches(ViewMatchers.isDisplayed()));



    }

    @Test
    public void testNavigationFromProfileFragment() {

        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());
        //Check if the profile fragment is displayed
        Espresso.onView(withId(R.id.profile_fragment_parent)).check(matches(ViewMatchers.isDisplayed()));

        //Shift to qr fragment
        Espresso.onView(withId(R.id.qrActivity)).perform(ViewActions.click());
        //Check if the qr fragment is displayed
        Espresso.onView(withId(R.id.activity_qr_parent)).check(matches(ViewMatchers.isDisplayed()));
        //Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        //Click on the events tab in the BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(ViewActions.click());
        //Check if the EventsFragment is displayed
        Espresso.onView(withId(R.id.empty_events_fragment_parent)).check(matches(ViewMatchers.isDisplayed()));

    }




}