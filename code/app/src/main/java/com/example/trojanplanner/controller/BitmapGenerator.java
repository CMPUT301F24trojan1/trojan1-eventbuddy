package com.example.trojanplanner.controller;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * BitmapGenerator is a utility class that generates a modified version of a default Bitmap
 * by applying a color filter based on the device ID.
 * <p>
 * The generated color is deterministic, based on the device's unique device ID, ensuring that
 * each device gets a distinct color overlay on the default image.
 * </p>
 */
public class BitmapGenerator {
    private String deviceID;
    private Bitmap defaultPicture;

    /**
     * Constructs a BitmapGenerator with the specified device ID and default picture.
     *
     * @param deviceID The unique device ID, used to generate the color filter.
     * @param defaultPicture The original bitmap that will be modified with a color overlay.
     */
    public BitmapGenerator(String deviceID, Bitmap defaultPicture) {
        this.deviceID = deviceID;
        this.defaultPicture = defaultPicture;
    }

    /**
     * Generates a new bitmap by applying a color filter based on the device ID.
     * The color generated from the device ID is overlaid on top of the default picture.
     *
     * @return A new Bitmap with the color filter applied.
     */
    public Bitmap generate() {
        // Generate a deterministic color based on the deviceID
        int color = generateColorFromDeviceID(deviceID);

        // Create a mutable copy of the original bitmap
        Bitmap mutableBitmap = defaultPicture.copy(Bitmap.Config.ARGB_8888, true);

        // Apply the color filter to the bitmap
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(50); // Set alpha to allow blending with the original image
        canvas.drawRect(0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight(), paint);

        return mutableBitmap;
    }

    /**
     * Generates an RGB color based on the device ID by hashing it and using the hash
     * to extract the red, green, and blue components of the color.
     *
     * @param deviceID The device ID used to generate the color.
     * @return The RGB color generated from the device ID.
     */
    private int generateColorFromDeviceID(String deviceID) {
        // Hash the deviceID and generate an RGB color
        int hash = deviceID.hashCode();
        int red = (hash >> 16) & 0xFF;
        int green = (hash >> 8) & 0xFF;
        int blue = hash & 0xFF;

        return Color.rgb(red, green, blue);
    }
}
