package com.example.trojanplanner;

/*
public class InstructionSlideFragmentTest {

    @Test
    public void testSlideFunctionality() {
        // Launch SlideShowActivity
        ActivityScenario.launch(SlideShowActivity.class);

        // Test sliding through all pages in the ViewPager
        for (int i = 0; i < 5; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.view_pager)).perform(ViewPagerActions.scrollToPage(i)); // Slide to the next page
        }
    }

    @Test
    public void testCloseButtonClosesActivity() {
        // Launch SlideShowActivity
        try (ActivityScenario<SlideShowActivity> scenario = ActivityScenario.launch(SlideShowActivity.class)) {
            // Verify that the close button is displayed
            Espresso.onView(ViewMatchers.withId(R.id.close_button)).check(matches(ViewMatchers.isDisplayed()));

            // Perform a click on the close button
            Espresso.onView(ViewMatchers.withId(R.id.close_button)).perform(ViewActions.click());

            // Verify that the activity has finished
            scenario.onActivity(activity -> {
                // Check if the activity is finishing or finished
                assert activity.isFinishing();
            });
        }
    }
}

 */