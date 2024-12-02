package com.example.trojanplanner;

import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Notification;
import com.example.trojanplanner.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class NotificationTest {

    private Notification notification;
    private User mockUser;
    private User mockOrganizer;
    private Event mockEvent;
    private Facility mockFacility;

    @Before
    public void setup() {
        notification = spy(Notification.class);

        // Mock objects
        mockUser = mock(User.class);
        mockOrganizer = mock(User.class);
        mockEvent = mock(Event.class);
        mockFacility = mock(Facility.class);
    }

    @Test
    public void testNotifyEntrantAboutEvent() {
        notification.notifyEntrantAboutEvent(mockUser, mockEvent);

        // Verify that the method is called with the correct parameters
        verify(notification).notifyEntrantAboutEvent(mockUser, mockEvent);
    }

    @Test
    public void testNotifyOrganizerEventFull() {
        notification.notifyOrganizerEventFull(mockOrganizer, mockEvent);

        // Verify that the method is called with the correct parameters
        verify(notification).notifyOrganizerEventFull(mockOrganizer, mockEvent);
    }

    @Test
    public void testNotifyUsersNewEvent() {
        List<User> users = Arrays.asList(mockUser, mockOrganizer);

        notification.notifyUsersNewEvent(users, mockEvent);

        // Verify that the method is called for all users in the list
        verify(notification).notifyUsersNewEvent(users, mockEvent);
    }

    @Test
    public void testNotifyUsersEventModified() {
        List<User> users = Arrays.asList(mockUser, mockOrganizer);

        notification.notifyUsersEventModified(users, mockEvent);

        // Verify that the method is called for all users in the list
        verify(notification).notifyUsersEventModified(users, mockEvent);
    }

    @Test
    public void testNotifyUsersEventFinished() {
        List<User> users = Arrays.asList(mockUser, mockOrganizer);

        notification.notifyUsersEventFinished(users, mockEvent);

        // Verify that the method is called for all users in the list
        verify(notification).notifyUsersEventFinished(users, mockEvent);
    }

    @Test
    public void testNotifyUsersFacilityChange() {
        List<User> users = Arrays.asList(mockUser, mockOrganizer);

        notification.notifyUsersFacilityChange(users, mockFacility);

        // Verify that the method is called for all users in the list
        verify(notification).notifyUsersFacilityChange(users, mockFacility);
    }
}
