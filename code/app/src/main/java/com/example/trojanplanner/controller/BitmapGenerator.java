package com.example.trojanplanner.controller;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BitmapGenerator {
    private String deviceID;
    private Bitmap defaultPicture;

    public BitmapGenerator(String deviceID, Bitmap defaultPicture) {
        this.deviceID = deviceID;
        this.defaultPicture = defaultPicture;
    }

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

    private int generateColorFromDeviceID(String deviceID) {
        // Hash the deviceID and generate an RGB color
        int hash = deviceID.hashCode();
        int red = (hash >> 16) & 0xFF;
        int green = (hash >> 8) & 0xFF;
        int blue = hash & 0xFF;

        return Color.rgb(red, green, blue);
    }
}
