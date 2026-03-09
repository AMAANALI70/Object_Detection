package com.example.detection_model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.tensorflow.lite.task.vision.detector.Detection;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom overlay view that draws bounding boxes and labels
 * on top of the camera preview for detected objects.
 */
public class OverlayView extends View {

    private List<Detection> detections = new ArrayList<>();
    private int imageWidth = 1;
    private int imageHeight = 1;

    private final Paint boxPaint;
    private final Paint textPaint;
    private final Paint textBackgroundPaint;

    // Predefined colors for different detections
    private static final int[] COLORS = {
            Color.rgb(255, 64, 129),   // Pink
            Color.rgb(0, 188, 212),    // Cyan
            Color.rgb(255, 193, 7),    // Amber
            Color.rgb(76, 175, 80),    // Green
            Color.rgb(156, 39, 176),   // Purple
            Color.rgb(255, 87, 34),    // Deep Orange
            Color.rgb(3, 169, 244),    // Light Blue
            Color.rgb(205, 220, 57),   // Lime
            Color.rgb(233, 30, 99),    // Rose
            Color.rgb(0, 150, 136),    // Teal
    };

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5f);
        boxPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(48f);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);

        textBackgroundPaint = new Paint();
        textBackgroundPaint.setStyle(Paint.Style.FILL);
        textBackgroundPaint.setAntiAlias(true);
    }

    /**
     * Update the detections to draw and trigger a redraw.
     * Must be called from the UI thread.
     *
     * @param detections  List of TFLite Detection results
     * @param imageWidth  Width of the analyzed image (for coordinate scaling)
     * @param imageHeight Height of the analyzed image (for coordinate scaling)
     */
    public void setDetections(List<Detection> detections, int imageWidth, int imageHeight) {
        this.detections = detections;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        invalidate(); // trigger onDraw
    }

    public void clear() {
        this.detections = new ArrayList<>();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (detections == null || detections.isEmpty()) {
            return;
        }

        // Calculate scale factors to map model coordinates to view coordinates
        float scaleX = (float) getWidth() / imageWidth;
        float scaleY = (float) getHeight() / imageHeight;

        for (int i = 0; i < detections.size(); i++) {
            Detection detection = detections.get(i);

            if (detection.getCategories().isEmpty()) continue;

            String label = detection.getCategories().get(0).getLabel();
            float score = detection.getCategories().get(0).getScore();
            String text = String.format("%s %.0f%%", label, score * 100);

            int color = COLORS[i % COLORS.length];
            boxPaint.setColor(color);
            textBackgroundPaint.setColor(color);

            // Get the bounding box and scale it to the view size
            RectF boundingBox = detection.getBoundingBox();
            RectF scaledBox = new RectF(
                    boundingBox.left * scaleX,
                    boundingBox.top * scaleY,
                    boundingBox.right * scaleX,
                    boundingBox.bottom * scaleY
            );

            // Draw bounding box
            canvas.drawRect(scaledBox, boxPaint);

            // Draw label background
            float textWidth = textPaint.measureText(text);
            float textHeight = 48f;
            float padding = 8f;
            RectF labelBg = new RectF(
                    scaledBox.left,
                    scaledBox.top - textHeight - padding * 2,
                    scaledBox.left + textWidth + padding * 2,
                    scaledBox.top
            );
            canvas.drawRect(labelBg, textBackgroundPaint);

            // Draw label text
            canvas.drawText(text, scaledBox.left + padding, scaledBox.top - padding, textPaint);
        }
    }
}
