package com.example.trojanplanner;

/* uncomment when needed
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BecomeOrganizerTest {

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

    private void sleepFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestFacilityCancelButton() {
        // Launch MainActivity before test
        //ActivityScenario.launch(ProfileActivity.class);

        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

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

        // Click on the QR tab in the BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(ViewActions.click());

        sleepFor(1000); // Sleep for 2 seconds


        // Click on the become organizer button
        Espresso.onView(withId(R.id.becomeOrganizerButton)).perform(ViewActions.click());

        // Check if facility set up page is displayed
        Espresso.onView(withId(R.id.facility_photo)).check(matches(isDisplayed()));

        // Click on facility name text box and fill in info about facility
        Espresso.onView(withId(R.id.facility_name)).perform(ViewActions.click(), clearText(), typeText("test facility"), ViewActions.closeSoftKeyboard());
        // Click on facility location text box and fill in info about facility
        Espresso.onView(withId(R.id.location)).perform(ViewActions.click(), clearText(), typeText("test location"), ViewActions.closeSoftKeyboard());
        // Click on cancel button
        Espresso.onView(withId(R.id.cancel_button)).perform(ViewActions.click());

    }

    @Test
    public void TestFacilityAndEventCreation() {
        // Launch ProfileActivity before test
        //ActivityScenario.launch(MainActivity.class);

        //launch
        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        // Click on the events tab in the BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(ViewActions.click());

        sleepFor(1000); // Sleep for 2 seconds


        // Click on the become organizer button
        Espresso.onView(withId(R.id.becomeOrganizerButton)).perform(ViewActions.click());

        // Check if facility set up page is displayed
        Espresso.onView(withId(R.id.facility_photo)).check(matches(isDisplayed()));

        // Click on facility name text box and fill in info about facility
        Espresso.onView(withId(R.id.facility_name)).perform(ViewActions.click(), clearText(), typeText("test facility"), ViewActions.closeSoftKeyboard());
        // Click on facility location text box and fill in info about facility
        Espresso.onView(withId(R.id.location)).perform(ViewActions.click(), clearText(), typeText("test location"), ViewActions.closeSoftKeyboard());
        // Click on cancel button
        Espresso.onView(withId(R.id.save_button)).perform(ViewActions.click());
        // Click on profile tab on navigation bar

        sleepFor(2000); // Sleep for 2 seconds


        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());

        Espresso.onView(withId(R.id.navigation_home)).perform(ViewActions.click());

        sleepFor(2000); // Sleep for 2 seconds


        Espresso.onView(withId(R.id.createEventButton)).perform(ViewActions.click());

        Espresso.onView(withText("YES I UNDERSTAND")).perform(ViewActions.click());

        // Check if facility create events page is displayed
        Espresso.onView(withId(R.id.eventImageView)).check(matches(isDisplayed()));

        sleepFor(2000); // Sleep for 2 seconds


        //Testing create event button
        Espresso.onView(withId(R.id.eventNameEditText)).perform(ViewActions.click(), clearText(), typeText("test event"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.eventNameEditText)).check(matches(withText("test event")));


        Espresso.onView(withId(R.id.eventSpotsAvailableEditText)).perform(ViewActions.click(), clearText(), typeText("21"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.eventSpotsAvailableEditText)).check(matches(withText("21")));

        Espresso.onView(withId(R.id.eventDescriptionEditText)).perform(ViewActions.click(), clearText(), typeText("test"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.eventDescriptionEditText)).check(matches(withText("test")));

        Espresso.onView(withId(R.id.eventPriceEditText)).perform(ViewActions.click(), clearText(), typeText("30"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.eventPriceEditText)).check(matches(withText("30")));

        Espresso.onView(withId(R.id.eventDateEditText)).perform(ViewActions.click());
        Espresso.onView(withText("OK")).perform(ViewActions.click());

        Espresso.onView(withId(R.id.eventendDateEditText)).perform(ViewActions.click());
        Espresso.onView(withText("OK")).perform(ViewActions.click());

        Espresso.onView(withId(R.id.signupOpenDateEditText)).perform(ViewActions.click());
        Espresso.onView(withText("OK")).perform(ViewActions.click());

        Espresso.onView(withId(R.id.signupCloseDateEditText)).perform(ViewActions.click());
        Espresso.onView(withText("OK")).perform(ViewActions.click());

        Espresso.onView(withId(R.id.waitlistCapacityEditText)).perform(ViewActions.click(), clearText(), typeText("20"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.waitlistCapacityEditText)).check(matches(withText("20")));

        Espresso.onView(withId(R.id.createEventButton)).perform(ViewActions.click());

        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withId(R.id.profileActivity)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.navigation_home)).perform(ViewActions.click());

        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withText("OK")).perform(ViewActions.click());

        sleepFor(2000); // Sleep for 2 seconds

        //Espresso.onView(withId(R.id.eventDescriptionEditText)).check(matches(withText("test")));
    }

    @Test
    public void TestEventOptionsButton() {

        sleepFor(4000); // Sleep for 2 seconds


        Espresso.onView(withId(R.id.event_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.event_name)).perform(ViewActions.click());

        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withId(R.id.ManageEvents)).perform(ViewActions.click());
        Espresso.onView(withText("Show Check-In Code")).perform(ViewActions.click());

        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withText("CLOSE")).perform(ViewActions.click());


        //Generate Event Code
        Espresso.onView(withId(R.id.ManageEvents)).perform(ViewActions.click());
        Espresso.onView(withText("Generate Event Code")).perform(ViewActions.click());

        //View Waiting List
        Espresso.onView(withId(R.id.ManageEvents)).perform(ViewActions.click());
        Espresso.onView(withText("View Waiting List")).perform(ViewActions.click());

        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withText("I UNDERSTAND")).perform(ViewActions.click());

        Espresso.onView(withId(R.id.waitlistListView)).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.goBackButton)).perform(ViewActions.click());

        //View Enrolled Entrants
        Espresso.onView(withText("View Enrolled Entrants")).perform(ViewActions.click());
        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withText("I UNDERSTAND")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.finalizeButton)).perform(ViewActions.click());
        Espresso.onView(withText("NO")).perform(ViewActions.click());
        sleepFor(2000); // Sleep for 2 seconds
        Espresso.onView(withId(R.id.goBackButton)).perform(ViewActions.click());

        //View Cancelled Entrants
        Espresso.onView(withText("View Cancelled Entrants")).perform(ViewActions.click());
        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withText("I UNDERSTAND")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.goBackButton)).perform(ViewActions.click());

        //View Invited Entrants
        Espresso.onView(withText("View Invited Entrants")).perform(ViewActions.click());
        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withText("I UNDERSTAND")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.goBackButton)).perform(ViewActions.click());

        //Initiate lottery
        Espresso.onView(withText("Initiate Lottery")).perform(ViewActions.click());
        sleepFor(2000); // Sleep for 2 seconds

        Espresso.onView(withText("OKAY I UNDERSTAND")).perform(ViewActions.click());

        //view map of Entrants
        Espresso.onView(withId(R.id.ManageEvents)).perform(ViewActions.click());
        Espresso.onView(withText("View Map of Entrants")).perform(ViewActions.click());

    }
}

 */