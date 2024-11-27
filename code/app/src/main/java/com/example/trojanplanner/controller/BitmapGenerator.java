package com.example.trojanplanner.controller;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Path;

import java.util.Random;

public class BitmapGenerator {

    // Generate a Bitmap with shape collages based on the input String
    public static Bitmap generateShapeCollage(String input, int width, int height) {
        // Create a Bitmap with the specified dimensions
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a Canvas to draw on the Bitmap
        Canvas canvas = new Canvas(bitmap);

        // Set up the Paint object for shapes
        Paint paint = new Paint();
        paint.setAntiAlias(true); // Anti-aliasing for smooth shapes

        // Generate a numeric value from the input string (hash code) for deterministic randomness
        int seed = input.hashCode();
        Random random = new Random(seed);

        // Fill the canvas with a background color (e.g., light gray)
        canvas.drawColor(Color.LTGRAY);

        // Derive number of shapes from the length of the input string
        int shapeCount = input.length(); // Number of shapes based on the length of the string

        for (int i = 0; i < shapeCount; i++) {
            // Randomize the shape's color based on the seed
            paint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

            // Randomize the shape type (circle, rectangle, or triangle)
            int shapeType = random.nextInt(3); // 0 = circle, 1 = rectangle, 2 = triangle

            // Randomize shape size and position
            int left = random.nextInt(width);
            int top = random.nextInt(height);
            int size = random.nextInt(Math.min(width, height) / 4) + 50;

            switch (shapeType) {
                case 0: // Circle
                    int radius = size / 2;
                    canvas.drawCircle(left + radius, top + radius, radius, paint);
                    break;
                case 1: // Rectangle
                    RectF rect = new RectF(left, top, left + size, top + size);
                    canvas.drawRect(rect, paint);
                    break;
                case 2: // Triangle
                    Path trianglePath = new Path();
                    trianglePath.moveTo(left, top);
                    trianglePath.lineTo(left + size, top);
                    trianglePath.lineTo(left + size / 2, top + size);
                    trianglePath.close();
                    canvas.drawPath(trianglePath, paint);
                    break;
            }
        }

        return bitmap;
    }
}
