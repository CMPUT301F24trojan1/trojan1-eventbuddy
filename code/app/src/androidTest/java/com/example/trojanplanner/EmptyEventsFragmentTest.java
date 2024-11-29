package com.example.trojanplanner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.trojanplanner.events.EmptyEventsFragment;
import com.example.trojanplanner.view.MainActivity;
import com.example.trojanplanner.view.ProfileActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EmptyEventsFragmentTest {

    private ActivityScenario<MainActivity> activityScenario;
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Launch MainActivity before each test
        //activityScenario = ActivityScenario.launch(MainActivity.class);
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
    public void testNoEventsMessageDisplayed() {
        //launch main activity
        ActivityScenario.launch(MainActivity.class);

        // Simulate a click on the Profile navigation button
        Espresso.onView(withId(R.id.nav_host_fragment_activity_main)).perform(ViewActions.click());

        //Verify that the empty events activity is being shown
        onView(withId(R.id.empty_events_parent)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationToEmptyEvents() {
        //launch profile v
        ActivityScenario.launch(ProfileActivity.class);

        // click on profile from nav menu
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        //check save button on first name text field
        Espresso.onView(withId(R.id.firstname_input)).perform(ViewActions.click(), clearText(), typeText("test user first name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.firstname_input)).check(matches(withText("test user first name")));

        //check save button on last name text field
        Espresso.onView(withId(R.id.lastname_input)).perform(ViewActions.click(), clearText(), typeText("test user last name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.lastname_input)).check(matches(withText("test user last name")));

        //check save button on email text field
        Espresso.onView(withId(R.id.email_input)).perform(ViewActions.click(), clearText(), typeText("testUser@gmail.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.email_input)).check(matches(withText("testUser@gmail.com")));

        //check save button on phone text field
        Espresso.onView(withId(R.id.phone_input)).perform(ViewActions.click(), clearText(), typeText("123 123 1234"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.phone_input)).check(matches(withText("123 123 1234")));

        Espresso.onView(withId(R.id.save_button)).perform(ViewActions.click());

        // Simulate a click on the events navigation button
        Espresso.onView(withId(R.id.nav_view)).perform(ViewActions.click());

    }
}