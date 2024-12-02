package com.example.trojanplanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;

public class FacilityTest {

    private Facility facility;
    private Organizer mockOrganizer;
    private Bitmap mockBitmap;

    @Before
    public void setup() {
        // Mock Organizer
        mockOrganizer = mock(Organizer.class);
        when(mockOrganizer.getFirstName()).thenReturn("John");
        when(mockOrganizer.getLastName()).thenReturn("Doe");

        // Initialize Facility with a mocked Organizer
        facility = new Facility("Library", "123", "34.0522,-118.2437", mockOrganizer, "path/to/file");

        // Mock a Bitmap
        mockBitmap = mock(Bitmap.class);
    }

    @Test
    public void testSetAndGetFacilityName() {
        facility.setName("New Library");
        assertEquals("New Library", facility.getName());
    }

    @Test
    public void testSetAndGetFacilityId() {
        facility.setFacilityId("456");
        assertEquals("456", facility.getFacilityId());
    }

    @Test
    public void testSetAndGetLocation() {
        facility.setLocation("40.7128,-74.0060");
        assertEquals("40.7128,-74.0060", facility.getLocation());
    }

    @Test
    public void testSetAndGetOwner() {
        Organizer newOwner = mock(Organizer.class);
        when(newOwner.getFirstName()).thenReturn("Jane");
        when(newOwner.getLastName()).thenReturn("Smith");

        facility.setOwner(newOwner);

        assertEquals("Jane", facility.getOwner().getFirstName());
        assertEquals("Smith", facility.getOwner().getLastName());
    }

    @Test
    public void testSetAndGetPfpFacilityFilePath() {
        facility.setPfpFacilityFilePath("new/path/to/file");
        assertEquals("new/path/to/file", facility.getPfpFacilityFilePath());
    }

    @Test
    public void testToString() {
        String expected = "Facility: Library (123)";
        assertEquals(expected, facility.toString());
    }
}
