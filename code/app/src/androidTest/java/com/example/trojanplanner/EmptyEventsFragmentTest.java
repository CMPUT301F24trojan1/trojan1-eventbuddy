package com.example.trojanplanner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Initialize Espresso Intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso Intents
        Intents.release();
    }

    @Test
    public void testNoEventsMessageDisplayed() {
        // Navigate to the Events tab
        onView(allOf(allOf(withId(R.id.navigation_home)), isDisplayed())).perform(click());

        // Verify that the empty events fragment is displayed
        onView(withId(R.id.eventIcon)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationToEmptyEvents() {
        // Navigate to Profile tab and fill the profile
        ActivityScenario.launch(ProfileActivity.class);
        //onView(allOf(withId(R.id.profileActivity), isDisplayed())).perform(click());

        // Fill and save profile data
        onView(withId(R.id.firstname_input)).perform(click(), clearText(), typeText("test user first name"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.firstname_input)).check(matches(withText("test user first name")));

        onView(withId(R.id.lastname_input)).perform(click(), clearText(), typeText("test user last name"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.lastname_input)).check(matches(withText("test user last name")));

        onView(withId(R.id.email_input)).perform(click(), clearText(), typeText("testUser@gmail.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.email_input)).check(matches(withText("testUser@gmail.com")));

        onView(withId(R.id.phone_input)).perform(click(), clearText(), typeText("123 123 1234"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.phone_input)).check(matches(withText("123 123 1234")));

        onView(withId(R.id.save_button)).perform(click());

        // Navigate to the Events tab
        onView(allOf(allOf(withId(R.id.navigation_home)), isDisplayed())).perform(click());

        // Verify that the empty events fragment is displayed
        onView(withId(R.id.alertIcon)).check(matches(isDisplayed()));
    }
}
