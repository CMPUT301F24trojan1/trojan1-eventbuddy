package com.example.trojanplanner;


import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class EventInstrumentedTest {

    private Context context;
    private Date startDateTime;
    private Date endDateTime;
    private Facility facility;
    private Organizer organizer;
    private Event event;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext(); // Set up the application context
        startDateTime = new Date();
        endDateTime = new Date(startDateTime.getTime() + 3600000); // 1 hour later

        // Step 1: Create the Facility without an owner initially.
        ArrayList<Event> events = new ArrayList<>();
        facility = new Facility(
                "Gym",                 // name
                "Facility123",         // facilityId
                "Downtown",            // location
                null,                  // owner - initially null
                "facility_image_path", // pfpFacilityFilePath
                null                   // pfpFacilityBitmap - null if no Bitmap needed
        );

        // Step 2: Create the Organizer with the Facility.
        organizer = new Organizer(
                "Doe", "John", "johndoe@example.com", "123-456-7890",
                "Device001", "Manager", true, false, events, facility
        );

        // Step 3: Set the Organizer as the owner of the Facility.
        facility.setOwner(organizer); // Assuming there’s a `setOwner` method in Facility.

        // Now create the Event with the Facility object
        event = new Event(
                "Morning Yoga", "Relaxing session", "", 0.0f,
                facility, startDateTime, endDateTime, 0, 20L, 20L
        );
    }

    @Test
    public void testDefaultImageForEvent() {
        // Test that the default image is returned if no custom picture is set
        Bitmap picture = event.getPicture();
        assertNotNull("Event should have a default picture", picture);
    }
}
