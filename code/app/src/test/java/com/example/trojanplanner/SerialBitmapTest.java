package com.example.trojanplanner;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.trojanplanner.model.SerialBitmap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}) // Specify the Android API level
public class SerialBitmapTest {

    private Bitmap originalBitmap;

    @Before
    public void setUp() {
        // Create a small test bitmap
        originalBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        originalBitmap.eraseColor(Color.RED); // Fill with red color
    }

    @Test
    public void testSetAndGetBitmap() {
        SerialBitmap serialBitmap = new SerialBitmap(originalBitmap);
        Bitmap retrievedBitmap = serialBitmap.getBitmap();

        assertNotNull("Retrieved bitmap should not be null", retrievedBitmap);
        assertEquals("Width should match", originalBitmap.getWidth(), retrievedBitmap.getWidth());
        assertEquals("Height should match", originalBitmap.getHeight(), retrievedBitmap.getHeight());
    }

    @Test
    public void testSetAndGetNullBitmap() {
        SerialBitmap serialBitmap = new SerialBitmap(null);

        assertNull("Retrieved bitmap should be null when input is null", serialBitmap.getBitmap());
    }

    @Test
    public void testSerializationAndDeserialization() throws Exception {
        SerialBitmap serialBitmap = new SerialBitmap(originalBitmap);

        // Serialize the SerialBitmap object
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(serialBitmap);

        // Deserialize the SerialBitmap object
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        SerialBitmap deserializedBitmap = (SerialBitmap) objectInputStream.readObject();

        // Validate the deserialized bitmap
        Bitmap retrievedBitmap = deserializedBitmap.getBitmap();

        assertNotNull("Deserialized bitmap should not be null", retrievedBitmap);
        assertEquals("Width should match after deserialization", originalBitmap.getWidth(), retrievedBitmap.getWidth());
        assertEquals("Height should match after deserialization", originalBitmap.getHeight(), retrievedBitmap.getHeight());
    }
}
