package com.example.trojanplanner;

import androidx.test.core.app.ActivityScenario;
import static org.hamcrest.Matchers.allOf;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
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

    private void sleepFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNavigationToEventsFragment() {

        // Launch MainActivity before test
        ActivityScenario.launch(MainActivity.class);

        // Check if the EventsFragment is displayed
        Espresso.onView(withId(R.id.alertIcon)).check(matches(ViewMatchers.isDisplayed()));
    }


    @Test
    public void testNavigationFromProfileActivity() {
        // Start ProfileActivity
        ActivityScenario.launch(ProfileActivity.class);

        // click on profile from nav menu
        //Espresso.onView(allOf(withId(R.id.profileActivity))).perform(ViewActions.click());

        //fill out profile activity
        Espresso.onView(withId(R.id.firstname_input)).perform(ViewActions.click(), clearText(), typeText("test user first name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.firstname_input)).check(matches(withText("test user first name")));

        Espresso.onView(withId(R.id.lastname_input)).perform(ViewActions.click(), clearText(), typeText("test user last name"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.lastname_input)).check(matches(withText("test user last name")));

        Espresso.onView(withId(R.id.email_input)).perform(ViewActions.click(), clearText(), typeText("testUser@gmail.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.email_input)).check(matches(withText("testUser@gmail.com")));

        Espresso.onView(withId(R.id.phone_input)).perform(ViewActions.click(), clearText(), typeText("123 123 1234"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.phone_input)).check(matches(withText("123 123 1234")));

        //save test profile
        Espresso.onView(withId(R.id.save_button)).perform(ViewActions.click());

        // Click on the home navigation item in ProfileActivity's BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(click());

        // Check if it navigates back to MainActivity by checking if NavHostFragment is displayed
        Espresso.onView(withId(R.id.empty_events_parent)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationFromQRActivity() {
        // Start QRActivity
        ActivityScenario.launch(QRActivity.class);

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Wait for the permission dialog to appear
        sleepFor(1000); // Adjust if needed

        // Click on the "Don't allow" button
        try {
            device.findObject(new UiSelector().text("Don’t allow")).click();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Click on the profile navigation item in QRActivity's BottomNavigationView
        Espresso.onView(allOf(withId(R.id.profileActivity))).perform(click());

        // Check if it navigates to ProfileActivity
        Espresso.onView(withId(R.id.profile_image)).check(matches(ViewMatchers.isDisplayed()));
    }

    @After
    public void tearDown() {
        Intents.release(); // Clean up after the test
    }
}