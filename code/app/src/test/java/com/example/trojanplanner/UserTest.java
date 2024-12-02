package com.example.trojanplanner;

import android.graphics.Bitmap;

import com.example.trojanplanner.model.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserTest {

    private User mockUser;
    private Bitmap mockBitmap;

    @Before
    public void setUp() {
        mockBitmap = mock(Bitmap.class);
        when(mockBitmap.getWidth()).thenReturn(100);
        when(mockBitmap.getHeight()).thenReturn(100);

        mockUser = new User("John", "Doe", "john.doe@example.com", "1234567890", "DEVICE123", "Entrant", false, false) {
            @Override
            public Bitmap getPfpBitmap() {
                return mockBitmap;
            }
        };
    }

    @Test
    public void testGetFullName() {
        String fullName = mockUser.getUserName();
        assertEquals("Doe John", fullName);
    }

    @Test
    public void testSetAndGetLastName() {
        mockUser.setLastName("Smith");
        assertEquals("Smith", mockUser.getLastName());
    }

    @Test
    public void testSetAndGetFirstName() {
        mockUser.setFirstName("Jane");
        assertEquals("Jane", mockUser.getFirstName());
    }

    @Test
    public void testSetAndGetEmail() {
        mockUser.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", mockUser.getEmail());
    }

    @Test
    public void testSetAndGetPhoneNumber() {
        mockUser.setPhoneNumber("9876543210");
        assertEquals("9876543210", mockUser.getPhoneNumber());
    }

    @Test
    public void testSetAndGetDeviceId() {
        mockUser.setDeviceId("DEVICE456");
        assertEquals("DEVICE456", mockUser.getDeviceId());
    }

    @Test
    public void testSetAndGetRole() {
        mockUser.setRole("Organizer");
        assertEquals("Organizer", mockUser.getRole());
    }

    @Test
    public void testIsOrganizer() {
        assertFalse(mockUser.isOrganizer());
        mockUser.setIsOrganizer(true);
        assertTrue(mockUser.isOrganizer());
    }

    @Test
    public void testIsAdmin() {
        assertFalse(mockUser.isAdmin());
        mockUser.setIsAdmin(true);
        assertTrue(mockUser.isAdmin());
    }

    @Test
    public void testGetPfpBitmap() {
        Bitmap bitmap = mockUser.getPfpBitmap();
        assertNotNull(bitmap);
        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void testSetPfpBitmap() {
        mockUser.setPfpBitmap(mockBitmap);
        assertEquals(mockBitmap, mockUser.getPfpBitmap());
    }
}
