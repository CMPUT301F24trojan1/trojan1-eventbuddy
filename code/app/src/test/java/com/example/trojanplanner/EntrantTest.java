package com.example.trojanplanner;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.app.Activity;
import android.content.res.Resources;

import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Organizer;

import java.util.ArrayList;

public class EntrantTest {

    private Entrant entrant;
    private Event mockEvent;

    @Before
    public void setup() {
        // Mock Activity and Resources
        Activity mockActivity = mock(Activity.class);
        Resources mockResources = mock(Resources.class);
        when(mockActivity.getResources()).thenReturn(mockResources);

        // Assign mockActivity to App.activity
        com.example.trojanplanner.App.activity = mockActivity;

        // Initialize Entrant
        entrant = new Entrant("Doe", "John", "johndoe@example.com", "1234567890", "DEVICE123", "entrant", false, false);

        // Mock Event
        mockEvent = mock(Event.class);
        when(mockEvent.getEventId()).thenReturn("EVENT123");
    }

    @Test
    public void testConstructor() {
        assertEquals("Doe", entrant.getLastName());
        assertEquals("John", entrant.getFirstName());
        assertEquals("johndoe@example.com", entrant.getEmail());
        assertEquals("1234567890", entrant.getPhoneNumber());
        assertEquals("DEVICE123", entrant.getDeviceId());
        assertFalse(entrant.isOrganizer());
        assertFalse(entrant.isAdmin());
        assertNotNull(entrant.getCurrentWaitlistedEvents());
        assertNotNull(entrant.getCurrentEnrolledEvents());
        assertNotNull(entrant.getCurrentPendingEvents());
        assertNotNull(entrant.getCurrentDeclinedEvents());
    }

    @Test
    public void testAddWaitlistedEvent() {
        entrant.addWaitlistedEvent(mockEvent);
        assertTrue(entrant.getCurrentWaitlistedEvents().contains(mockEvent));

        // Test adding a duplicate
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.addWaitlistedEvent(mockEvent));
        assertEquals("Entrant is already registered to event", exception.getMessage());
    }

    @Test
    public void testRemoveWaitlistedEvent() {
        entrant.addWaitlistedEvent(mockEvent);
        entrant.removeWaitlistedEvent(mockEvent);
        assertFalse(entrant.getCurrentWaitlistedEvents().contains(mockEvent));

        // Test removing a non-existent event
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.removeWaitlistedEvent(mockEvent));
        assertEquals("Event doesn't exist in the list.", exception.getMessage());
    }

    @Test
    public void testAddEnrolledEvent() {
        entrant.addEnrolledEvent(mockEvent);
        assertTrue(entrant.getCurrentEnrolledEvents().contains(mockEvent));

        // Test adding a duplicate
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.addEnrolledEvent(mockEvent));
        assertEquals("Entrant already confirmed to join event.", exception.getMessage());
    }

    @Test
    public void testRemoveEnrolledEvent() {
        entrant.addEnrolledEvent(mockEvent);
        entrant.removeEnrolledEvent(mockEvent);
        assertFalse(entrant.getCurrentEnrolledEvents().contains(mockEvent));

        // Test removing a non-existent event
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.removeEnrolledEvent(mockEvent));
        assertEquals("Event doesn't exist in Entrant's joined events.", exception.getMessage());
    }

    @Test
    public void testAddPendingEvent() {
        entrant.addPendingEvent(mockEvent);
        assertTrue(entrant.getCurrentPendingEvents().contains(mockEvent));

        // Test adding a duplicate
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.addPendingEvent(mockEvent));
        assertEquals("Event is already in Entrant's pending list.", exception.getMessage());
    }

    @Test
    public void testRemovePendingEvent() {
        entrant.addPendingEvent(mockEvent);
        entrant.removePendingEvent(mockEvent);
        assertFalse(entrant.getCurrentPendingEvents().contains(mockEvent));

        // Test removing a non-existent event
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.removePendingEvent(mockEvent));
        assertEquals("Event does not exist in Entrant's pending events list.", exception.getMessage());
    }

    @Test
    public void testAddDeclinedEvent() {
        entrant.addDeclinedEvent(mockEvent);
        assertTrue(entrant.getCurrentDeclinedEvents().contains(mockEvent));

        // Test adding a duplicate
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.addDeclinedEvent(mockEvent));
        assertEquals("Event is already in Entrant's declined list.", exception.getMessage());
    }

    @Test
    public void testRemoveDeclinedEvent() {
        entrant.addDeclinedEvent(mockEvent);
        entrant.removeDeclinedEvent(mockEvent);
        assertFalse(entrant.getCurrentDeclinedEvents().contains(mockEvent));

        // Test removing a non-existent event
        Exception exception = assertThrows(IllegalArgumentException.class, () -> entrant.removeDeclinedEvent(mockEvent));
        assertEquals("Event does not exist in Entrant's declined events list.", exception.getMessage());
    }

    @Test
    public void testReplaceEventMatchingId() {
        entrant.addWaitlistedEvent(mockEvent);

        // Create a replacement event with the same ID
        Event replacementEvent = mock(Event.class);
        when(replacementEvent.getEventId()).thenReturn("EVENT123");

        boolean replaced = entrant.replaceEventMatchingId(replacementEvent);
        assertTrue(replaced);
        assertTrue(entrant.getCurrentWaitlistedEvents().contains(replacementEvent));
        assertFalse(entrant.getCurrentWaitlistedEvents().contains(mockEvent));

        // Test replacing a non-existent event
        Event nonExistentEvent = mock(Event.class);
        when(nonExistentEvent.getEventId()).thenReturn("EVENT456");
        assertFalse(entrant.replaceEventMatchingId(nonExistentEvent));
    }

    @Test
    public void testFindIndexWithId() {
        entrant.addWaitlistedEvent(mockEvent);

        int index = entrant.findIndexWithId(entrant.getCurrentWaitlistedEvents(), "EVENT123");
        assertEquals(0, index);

        // Test finding a non-existent event
        index = entrant.findIndexWithId(entrant.getCurrentWaitlistedEvents(), "EVENT456");
        assertEquals(-1, index);
    }

    @Test
    public void testReturnEventsArrayByIndex() {
        assertEquals(entrant.getCurrentEnrolledEvents(), entrant.returnEventsArrayByIndex(0));
        assertEquals(entrant.getCurrentWaitlistedEvents(), entrant.returnEventsArrayByIndex(1));
        assertEquals(entrant.getCurrentPendingEvents(), entrant.returnEventsArrayByIndex(2));
        assertEquals(entrant.getCurrentDeclinedEvents(), entrant.returnEventsArrayByIndex(3));

        // Test invalid index
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> entrant.returnEventsArrayByIndex(4));
        assertEquals("Index must be between 0-3", exception.getMessage());
    }
}
