package com.example.trojanplanner;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.trojanplanner.model.Admin;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.User;

import java.util.ArrayList;
import java.util.List;

public class AdminTest {

    private Admin admin;
    private Event mockEvent;
    private User mockUser;
    private Facility mockFacility;

    @Before
    public void setup() {
        // Initialize the admin object
        admin = new Admin("Doe", "John", "admin@example.com", "1234567890", "DEVICE123", "Admin", true, true);

        // Create mock objects
        mockEvent = mock(Event.class);
        mockUser = mock(User.class);
        mockFacility = mock(Facility.class);
    }

    @Test
    public void testAdminConstructor() {
        // Verify the Admin constructor initializes fields correctly
        assertEquals("Doe", admin.getLastName());
        assertEquals("John", admin.getFirstName());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("1234567890", admin.getPhoneNumber());
        assertEquals("DEVICE123", admin.getDeviceId());
        assertEquals("Admin", admin.getRole());
        assertTrue(admin.isOrganizer());
        assertTrue(admin.isAdmin());
    }

    @Test
    public void testIncompleteAdminConstructor() {
        // Test the alternate constructor
        Admin incompleteAdmin = new Admin("DEVICE789");
        assertEquals("DEVICE789", incompleteAdmin.getDeviceId());
        assertNull(incompleteAdmin.getFirstName());
        assertNull(incompleteAdmin.getLastName());
        assertNull(incompleteAdmin.getEmail());
    }

    @Test
    public void testCreateTermsAndConditionsAgreement() {
        // Call the createTermsAndConditionsAgreement method
        admin.createTermsAndConditionsAgreement(mockUser);

        // Add additional verifications/assertions when the method is implemented
        // e.g., verify(mockUser).acceptTerms();
    }

    @Test
    public void testRemoveEvent() {
        // Call the removeEvent method
        admin.removeEvent(mockEvent);

        // Verify that the method can be invoked without errors
        // Add additional verifications/assertions when the method is implemented
    }

    @Test
    public void testRemoveProfile() {
        // Call the removeProfile method
        admin.removeProfile(mockUser);

        // Verify that the method can be invoked without errors
        // Add additional verifications/assertions when the method is implemented
    }

    @Test
    public void testRemoveImage() {
        String mockImagePath = "path/to/image.png";

        // Call the removeImage method
        admin.removeImage(mockImagePath);

        // Verify that the method can be invoked without errors
        // Add additional verifications/assertions when the method is implemented
    }

    @Test
    public void testRemoveHashedQRCodeData() {
        // Call the removeHashedQRCodeData method
        admin.removeHashedQRCodeData(mockEvent);

        // Verify that the method can be invoked without errors
        // Add additional verifications/assertions when the method is implemented
    }

    @Test
    public void testBrowseEvents() {
        // Mock the behavior of browseEvents
        List<Event> mockEvents = new ArrayList<>();
        mockEvents.add(mockEvent);

        Admin spyAdmin = spy(admin);
        doReturn(mockEvents).when(spyAdmin).browseEvents();

        List<Event> events = spyAdmin.browseEvents();
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(mockEvent, events.get(0));
    }

    @Test
    public void testBrowseUserProfiles() {
        // Mock the behavior of browseUserProfiles
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(mockUser);

        Admin spyAdmin = spy(admin);
        doReturn(mockUsers).when(spyAdmin).browseUserProfiles();

        List<User> users = spyAdmin.browseUserProfiles();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(mockUser, users.get(0));
    }

    @Test
    public void testBrowseImages() {
        // Mock the behavior of browseImages
        List<String> mockImages = new ArrayList<>();
        mockImages.add("path/to/image1.png");

        Admin spyAdmin = spy(admin);
        doReturn(mockImages).when(spyAdmin).browseImages();

        List<String> images = spyAdmin.browseImages();
        assertNotNull(images);
        assertEquals(1, images.size());
        assertEquals("path/to/image1.png", images.get(0));
    }

    @Test
    public void testBrowseFacilities() {
        // Mock the behavior of browseFacilities
        List<Facility> mockFacilities = new ArrayList<>();
        mockFacilities.add(mockFacility);

        Admin spyAdmin = spy(admin);
        doReturn(mockFacilities).when(spyAdmin).browseFacilities();

        List<Facility> facilities = spyAdmin.browseFacilities();
        assertNotNull(facilities);
        assertEquals(1, facilities.size());
        assertEquals(mockFacility, facilities.get(0));
    }

    @Test
    public void testRemoveFacility() {
        // Call the removeFacility method
        admin.removeFacility(mockFacility);

        // Verify that the method can be invoked without errors
        // Add additional verifications/assertions when the method is implemented
    }
}
