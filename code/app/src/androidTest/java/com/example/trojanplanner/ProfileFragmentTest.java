package com.example.trojanplanner;

import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.trojanplanner.ProfileUtils.ProfileFragment;
import com.example.trojanplanner.view.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Initialize the activity and fragment before running the tests
        Intents.init();
    }

    @Test
    public void testProfileFragmentUI() {
        // Using Espresso to navigate to ProfileFragment
        Espresso.onView(withId(R.id.nav_view)) // Bottom navigation view
                .perform(ViewActions.click()); // Click the profile icon

        // Check if ProfileFragment is displayed in the container
        Espresso.onView(withId(R.id.profile_fragment_container))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testProfileFragmentFormInteractions() {
        // Navigate to ProfileFragment
        Espresso.onView(withId(R.id.nav_view))
                .perform(ViewActions.click());

        // Interact with form inputs
        Espresso.onView(withId(R.id.firstname_input))
                .perform(clearText(), ViewActions.typeText("Test User"), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.lastname_input))
                .perform(clearText(), ViewActions.typeText("Test User1"), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.email_input))
                .perform(clearText(), ViewActions.typeText("test12@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.phone_input))
                .perform(clearText(), ViewActions.typeText("123 123 1234"), ViewActions.closeSoftKeyboard());

        // Verify fields contain the entered text
        Espresso.onView(withId(R.id.firstname_input))
                .check(matches(withText("Test User")));
        Espresso.onView(withId(R.id.lastname_input))
                .check(matches(withText("Test User1")));
        Espresso.onView(withId(R.id.email_input))
                .check(matches(withText("test12@gmail.com")));
        Espresso.onView(withId(R.id.phone_input))
                .check(matches(withText("123 123 1234")));
    }

    @Test
    public void testSaveButton() {
        // Navigate to ProfileFragment
        Espresso.onView(withId(R.id.nav_view))
                .perform(ViewActions.click());

        // Interact with form inputs
        Espresso.onView(withId(R.id.firstname_input))
                .perform(clearText(), ViewActions.typeText("Test User"));

        Espresso.onView(withId(R.id.save_button))
                .perform(ViewActions.click());

        // Verify that the "Document upload successful!" toast is shown
        Espresso.onView(withText("Document upload successful!"))
                .inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView())))) // Ensure this is a toast
                .check(matches(ViewMatchers.isDisplayed())); // Ensure it's displayed
    }

    @Test
    public void testCancelButton() {
        // Navigate to ProfileFragment
        Espresso.onView(withId(R.id.nav_view))
                .perform(ViewActions.click());

        // Enter some text into the fields
        Espresso.onView(withId(R.id.firstname_input))
                .perform(clearText(), ViewActions.typeText("Test User"), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.lastname_input))
                .perform(clearText(), ViewActions.typeText("Test User1"), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.email_input))
                .perform(clearText(), ViewActions.typeText("test12@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.phone_input))
                .perform(clearText(), ViewActions.typeText("123 123 1234"), ViewActions.closeSoftKeyboard());

        // Now click the Cancel button
        Espresso.onView(withId(R.id.cancel_button))
                .perform(ViewActions.click());

        // Verify that the fields revert to their default values
        Espresso.onView(withId(R.id.firstname_input))
                .check(matches(withText("boe"))); // Default first name
        Espresso.onView(withId(R.id.lastname_input))
                .check(matches(withText("jdien"))); // Default last name
        Espresso.onView(withId(R.id.email_input))
                .check(matches(withText("boejiden@gmail.com"))); // Default email
        Espresso.onView(withId(R.id.phone_input))
                .check(matches(withText(""))); // Default phone number is empty
    }

    @After
    public void tearDown() {
        Intents.release();
    }
}
