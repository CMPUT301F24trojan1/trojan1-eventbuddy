package com.example.trojanplanner;


import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojanplanner.model.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class EventInstrumentedTest {

    private Context context;
    private Date startDateTime;
    private Date endDateTime;

    @Before
    public void setUp() {
        // Use ApplicationProvider to get the application context in an instrumented test
        context = ApplicationProvider.getApplicationContext();

        // Set up start and end times for the event
        Calendar calendar = Calendar.getInstance();
        startDateTime = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        endDateTime = calendar.getTime();
    }

    @Test
    public void testDefaultImageForEvent() {
        // Create an instance of ConcreteEvent without setting a custom picture
        Event event = new Event("Morning Yoga");

        // Call getPicture() with the application context
        Bitmap picture = event.getPicture();

        // Ensure the default image is returned when no picture is set
        assertNotNull("Event should have a default picture", picture);
    }
}
