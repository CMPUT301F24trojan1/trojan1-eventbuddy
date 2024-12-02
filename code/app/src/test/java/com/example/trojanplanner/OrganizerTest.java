package com.example.trojanplanner;

import android.graphics.Bitmap;

import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrganizerTest {

    private Organizer organizer;
    private Event mockEvent1;
    private Event mockEvent2;
    private Facility mockFacility;

    @Before
    public void setUp() {
        // Initialize Organizer
        organizer = new Organizer("Smith", "John", "john.smith@example.com", "1234567890",
                "device123", "Organizer", true, false, new ArrayList<>(), null);

        // Mock Events
        mockEvent1 = mock(Event.class);
        mockEvent2 = mock(Event.class);

        // Set up mock events
        when(mockEvent1.getEventId()).thenReturn("event1");
        when(mockEvent2.getEventId()).thenReturn("event2");

        // Mock Facility
        mockFacility = mock(Facility.class);
    }

    @Test
    public void testAddEvent() {
        organizer.addEvent(mockEvent1);
        assertTrue(organizer.getCreatedEvents().contains(mockEvent1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateEvent() {
        organizer.addEvent(mockEvent1);
        organizer.addEvent(mockEvent1); // Should throw exception
    }

    @Test
    public void testRemoveEventByObject() {
        organizer.addEvent(mockEvent1);
        organizer.removeEvent(mockEvent1);
        assertFalse(organizer.getCreatedEvents().contains(mockEvent1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEventByObjectNotInList() {
        organizer.removeEvent(mockEvent1); // Should throw exception
    }

    @Test
    public void testRemoveEventByIndex() {
        organizer.addEvent(mockEvent1);
        organizer.addEvent(mockEvent2);
        organizer.removeEvent(0);
        assertEquals(1, organizer.getCreatedEvents().size());
        assertEquals(mockEvent2, organizer.getCreatedEvents().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEventByInvalidIndex() {
        organizer.removeEvent(0); // Should throw exception
    }

    @Test
    public void testFindIndexWithEventId() {
        organizer.addEvent(mockEvent1);
        organizer.addEvent(mockEvent2);
        assertEquals(0, organizer.findIndexWithEventId("event1"));
        assertEquals(1, organizer.findIndexWithEventId("event2"));
        assertEquals(-1, organizer.findIndexWithEventId("event3"));
    }

    @Test
    public void testSetEventAtIndex() {
        organizer.addEvent(mockEvent1);
        organizer.addEvent(mockEvent2);

        Event mockEvent3 = mock(Event.class);
        when(mockEvent3.getEventId()).thenReturn("event3");

        organizer.setEventAtIndex(mockEvent3, 1);
        assertEquals(mockEvent3, organizer.getCreatedEvents().get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEventAtInvalidIndex() {
        Event mockEvent3 = mock(Event.class);
        organizer.setEventAtIndex(mockEvent3, 0); // Should throw exception
    }

    @Test
    public void testCreateFacility() {
        Bitmap mockBitmap = mock(Bitmap.class);

        Facility facility = organizer.createFacility("Test Facility", "facility123", "Location", organizer,
                "path/to/facility", mockBitmap);
        assertNotNull(facility);
        assertEquals("Test Facility", facility.getName());
        assertEquals("facility123", facility.getFacilityId());
        assertEquals(organizer, facility.getOwner());
    }

    @Test
    public void testGetFacility() {
        Bitmap mockBitmap = mock(Bitmap.class);
        Facility facility = organizer.createFacility("Test Facility", "facility123", "Location", organizer,
                "path/to/facility", mockBitmap);

        assertEquals(facility, organizer.getFacility());
    }

    @Test
    public void testRemoveFacility() {
        Bitmap mockBitmap = mock(Bitmap.class);
        organizer.createFacility("Test Facility", "facility123", "Location", organizer, "path/to/facility", mockBitmap);

        organizer.removeFacility();
        assertNull(organizer.getFacility());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveFacilityWhenNoneExists() {
        organizer.removeFacility(); // Should throw exception
    }
}
