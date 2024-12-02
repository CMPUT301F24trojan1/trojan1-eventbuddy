package com.example.trojanplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom View that overlays focus points on the camera preview in a barcode scanning application.
 * It draws circles at the specified focus points on the screen, scaling them based on the camera preview size.
 */
public class FocusOverlayView extends View {
    private final Paint paint = new Paint();
    private final List<ResultPoint> resultPoints = new ArrayList<>();
    private int previewWidth = 1; // Default to 1 to avoid division by zero
    private int previewHeight = 1;

    /**
     * Constructor for the FocusOverlayView.
     * Sets up the paint color, style, and anti-aliasing for the view.
     *
     * @param context The application context.
     * @param attrs The attributes for the view from XML layout (if any).
     */
    public FocusOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(0xFFFF0000); // Red color
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    // Update the camera preview size
    public void setPreviewSize(int width, int height) {
        this.previewWidth = width;
        this.previewHeight = height;
    }

    /**
     * Adds a list of focus points to be drawn on the view.
     * The view is invalidated so it will be redrawn with the new focus points.
     *
     * @param points The list of ResultPoint objects to be displayed as focus points.
     */
    public void addFocusPoints(List<ResultPoint> points) {
        resultPoints.clear();
        resultPoints.addAll(points);
        invalidate(); // Redraw the view
    }

    /**
     * Draws the focus points on the canvas. This method scales the coordinates of the focus points
     * based on the current dimensions of the view and the camera preview size.
     *
     * @param canvas The canvas on which the view is drawn.
     */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (resultPoints.isEmpty()) {
            Log.d("FocusOverlayView", "No points to draw");
            return;
        }

        float scaleX = (float) getWidth() / previewWidth;
        float scaleY = (float) getHeight() / previewHeight;

        for (ResultPoint point : resultPoints) {
            float x = point.getX() * scaleX;
            float y = point.getY() * scaleY;
            Log.d("FocusOverlayView", "Drawing point at: (" + x + ", " + y + ")");
            canvas.drawCircle(x, y, 10, paint);
        }
    }

}
