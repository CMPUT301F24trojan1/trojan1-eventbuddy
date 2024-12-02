package com.example.trojanplanner;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojanplanner.view.MainActivity;
import com.example.trojanplanner.view.ProfileActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

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
    public void testProfileFragmentNavigation() {
        // Simulate a click on the Profile navigation button
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        // Verify that ProfileActivity is launched
        Intents.intended(hasComponent(ProfileActivity.class.getName()));

        //verify that ProfileActivity is in view
        Espresso.onView(withId(R.id.profileFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testProfileFragmentInput() {
        // Navigate to the Profile Fragment or Activity
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        // Enter and verify text in the first name input field
        Espresso.onView(withId(R.id.firstname_input)).perform(ViewActions.click(), clearText(), typeText("test user first name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.firstname_input)).check(matches(withText("test user first name")));

        // Enter and verify text in the last name input field
        Espresso.onView(withId(R.id.lastname_input)).perform(ViewActions.click(), clearText(), typeText("test user last name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.lastname_input)).check(matches(withText("test user last name")));

        // Enter and verify text in the email input field
        Espresso.onView(withId(R.id.email_input)).perform(ViewActions.click(), clearText(), typeText("testuser@gmail.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.email_input)).check(matches(withText("testuser@gmail.com")));

        // Enter and verify text in the phone input field
        Espresso.onView(withId(R.id.phone_input)).perform(ViewActions.click(), clearText(), typeText("123 123 1234"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.phone_input)).check(matches(withText("123 123 1234")));
    }


    @Test
    public void testProfileSaveButton() {
        // click on profile from nav menu
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        //check save button on first name text field
        Espresso.onView(withId(R.id.firstname_input)).perform(ViewActions.click(), clearText(), typeText("test user first name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.save_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.firstname_input)).check(matches(withText("test user first name")));

        //check save button on last name text field
        Espresso.onView(withId(R.id.lastname_input)).perform(ViewActions.click(), clearText(), typeText("test user last name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.save_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.lastname_input)).check(matches(withText("test user last name")));

        //check save button on email text field
        Espresso.onView(withId(R.id.email_input)).perform(ViewActions.click(), clearText(), typeText("testUser@gmail.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.save_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.email_input)).check(matches(withText("testUser@gmail.com")));

        //check save button on phone text field
        Espresso.onView(withId(R.id.phone_input)).perform(ViewActions.click(), clearText(), typeText("123 123 1234"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.save_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.phone_input)).check(matches(withText("123 123 1234")));
    }

    @Test
    public void testProfileCancelButton() {
        // click on profile from nav menu
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        //check cancel button on first name text field
        Espresso.onView(withId(R.id.firstname_input)).perform(ViewActions.click(), clearText(), typeText("test_user_first_name_2 (cancel test)"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.cancel_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.firstname_input)).check(matches(withText("test user first name")));

        //check cancel button on last name text field
        Espresso.onView(withId(R.id.lastname_input)).perform(ViewActions.click(), clearText(), typeText("test_user_last_name_2 (cancel test)"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.cancel_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.lastname_input)).check(matches(withText("test user last name")));

        //check cancel button on email text field
        Espresso.onView(withId(R.id.email_input)).perform(ViewActions.click(), clearText(), typeText("testUser2@gmail.com (cancel test)"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.cancel_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.email_input)).check(matches(withText("testUser@gmail.com")));

        //check cancel button on phone text field
        Espresso.onView(withId(R.id.phone_input)).perform(ViewActions.click(), clearText(), typeText("321 321 4321"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.cancel_button)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.phone_input)).check(matches(withText("123 123 1234")));
    }
}
