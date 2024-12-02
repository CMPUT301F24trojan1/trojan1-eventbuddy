package com.example.trojanplanner;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.trojanplanner.view.admin.AdminActivity;
import com.example.trojanplanner.view.admin.AdminFacilitiesActivity;
import com.example.trojanplanner.view.admin.AdminImagesActivity;
import com.example.trojanplanner.view.admin.AdminQRActivity;
import com.example.trojanplanner.view.admin.AdminUsersActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminNavigationTest {

    private ActivityScenario<AdminActivity> activityScenario;
    private FirebaseFirestore db;

    @Rule
    public ActivityScenarioRule<AdminActivity> activityRule = new ActivityScenarioRule<>(AdminActivity.class);

    @Before
    public void setUp() {
        // Launch AdminActivity before each test
        activityScenario = ActivityScenario.launch(AdminActivity.class);
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
    public void testNavigationFromEvents() {
        // Launch AdminActivity
        ActivityScenario.launch(AdminActivity.class);
        sleepFor(2000);

        //Espresso.onView(withText("GOT IT!")).perform(ViewActions.click());

        // Check admin events are showing
        Espresso.onView(withId(R.id.navigation_home)).perform(click());
        sleepFor(1000);

        // Check admin facilities are showing
        Espresso.onView(withId(R.id.navigation_facilities)).perform(click());
        sleepFor(1000);

        // Check admin users are showing
        Espresso.onView(withId(R.id.navigation_users)).perform(click());
        sleepFor(1000);

        // Check admin images are showing
        Espresso.onView(withId(R.id.navigation_images)).perform(click());
        sleepFor(1000);

        // Check admin QR codes are showing
        Espresso.onView(withId(R.id.navigation_qr)).perform(click());
        sleepFor(1000);
    }

    @Test
    public void testNavigationFromFacility() {
        // Launch AdminActivity
        ActivityScenario.launch(AdminFacilitiesActivity.class);
        sleepFor(2000);

        //Espresso.onView(withText("GOT IT!")).perform(ViewActions.click());

        // Check admin events are showing
        Espresso.onView(withId(R.id.navigation_home)).perform(click());
        sleepFor(1000);

        // Check admin facilities are showing
        Espresso.onView(withId(R.id.navigation_facilities)).perform(click());
        sleepFor(1000);

        // Check admin users are showing
        Espresso.onView(withId(R.id.navigation_users)).perform(click());
        sleepFor(1000);

        // Check admin images are showing
        Espresso.onView(withId(R.id.navigation_images)).perform(click());
        sleepFor(1000);

        // Check admin QR codes are showing
        Espresso.onView(withId(R.id.navigation_qr)).perform(click());
        sleepFor(1000);
    }

    @Test
    public void testNavigationFromUsers() {
        // Launch AdminActivity
        ActivityScenario.launch(AdminUsersActivity.class);
        sleepFor(2000);

        //Espresso.onView(withText("GOT IT!")).perform(ViewActions.click());

        // Check admin events are showing
        Espresso.onView(withId(R.id.navigation_home)).perform(click());
        sleepFor(1000);

        // Check admin facilities are showing
        Espresso.onView(withId(R.id.navigation_facilities)).perform(click());
        sleepFor(1000);

        // Check admin users are showing
        Espresso.onView(withId(R.id.navigation_users)).perform(click());
        sleepFor(1000);

        // Check admin images are showing
        Espresso.onView(withId(R.id.navigation_images)).perform(click());
        sleepFor(1000);

        // Check admin QR codes are showing
        Espresso.onView(withId(R.id.navigation_qr)).perform(click());
        sleepFor(1000);
    }

    @Test
    public void testNavigationFromImages() {
        // Launch AdminActivity
        ActivityScenario.launch(AdminImagesActivity.class);
        sleepFor(2000);

        //Espresso.onView(withText("GOT IT!")).perform(ViewActions.click());

        // Check admin events are showing
        Espresso.onView(withId(R.id.navigation_home)).perform(click());
        sleepFor(1000);

        // Check admin facilities are showing
        Espresso.onView(withId(R.id.navigation_facilities)).perform(click());
        sleepFor(1000);

        // Check admin users are showing
        Espresso.onView(withId(R.id.navigation_users)).perform(click());
        sleepFor(1000);

        // Check admin images are showing
        Espresso.onView(withId(R.id.navigation_images)).perform(click());
        sleepFor(1000);

        // Check admin QR codes are showing
        Espresso.onView(withId(R.id.navigation_qr)).perform(click());
        sleepFor(1000);
    }

    @Test
    public void testNavigationFromQR() {
        // Launch AdminActivity
        ActivityScenario.launch(AdminQRActivity.class);
        sleepFor(2000);

        //Espresso.onView(withText("GOT IT!")).perform(ViewActions.click());

        // Check admin events are showing
        Espresso.onView(withId(R.id.navigation_home)).perform(click());
        sleepFor(1000);

        // Check admin facilities are showing
        Espresso.onView(withId(R.id.navigation_facilities)).perform(click());
        sleepFor(1000);

        // Check admin users are showing
        Espresso.onView(withId(R.id.navigation_users)).perform(click());
        sleepFor(1000);

        // Check admin images are showing
        Espresso.onView(withId(R.id.navigation_images)).perform(click());
        sleepFor(1000);

        // Check admin QR codes are showing
        Espresso.onView(withId(R.id.navigation_qr)).perform(click());
        sleepFor(1000);
    }

    @Test
    public void ExitAdminViewTest() {
        // Launch AdminActivity
        ActivityScenario.launch(AdminActivity.class);
        sleepFor(2000);

        Espresso.onView(withText("GOT IT!")).perform(ViewActions.click());

        Espresso.onView(withId(R.id.switchAdmin)).perform(click());
        sleepFor(2000);

        Espresso.onView(withId(R.id.profileActivity)).check(matches(isDisplayed()));


    }

    @After
    public void tearDown() {
        Intents.release(); // Clean up after the test
    }
}
