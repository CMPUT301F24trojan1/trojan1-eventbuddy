package com.example.trojanplanner;

/*
@RunWith(AndroidJUnit4.class)
@LargeTest
public class FragmentNavigationTest {

    private ActivityScenario<MainActivity> activityScenario;
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Launch MainActivity before each test
        //activityScenario = ActivityScenario.launch(MainActivity.class);
        Intents.init(); // Initialize Espresso Intents
    }

    @Test
    public void testNavigationToEventsFragment() {

        // Launch MainActivity before test
        activityScenario = ActivityScenario.launch(MainActivity.class);
        // Click on the events tab in the BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(click());

        // Check if the EventsFragment is displayed
        Espresso.onView(withId(R.id.empty_events_parent)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationToQRActivity() {
        // Launch MainActivity before test
        activityScenario = ActivityScenario.launch(MainActivity.class);

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
        ActivityScenario.launch(ProfileActivity.class);

        // click on profile from nav menu
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

        // Click on the home navigation item in ProfileActivity's BottomNavigationView
        Espresso.onView(withId(R.id.navigation_home)).perform(click());

        // Check if it navigates back to MainActivity by checking if NavHostFragment is displayed
        Espresso.onView(withId(R.id.empty_events_parent)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationFromQRActivity() {
        // Start QRActivity
        ActivityScenario.launch(QRActivity.class);

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


 */