package com.example.detection_model;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private PreviewView previewView;
    private OverlayView overlayView;
    private ExecutorService cameraExecutor;
    private ObjectDetector objectDetector;
    private boolean modelLoaded = false;

    private static final String TAG = "DETECTION_DEBUG";
    private static final int REQUEST_CAMERA = 100;
    private static final float CONFIDENCE_THRESHOLD = 0.3f;
    private static final int MAX_RESULTS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        overlayView = findViewById(R.id.overlayView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        loadModel();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    private void loadModel() {
        Log.d(TAG, "=== Loading model ===");
        try {
            BaseOptions baseOptions = BaseOptions.builder()
                    .build();

            ObjectDetector.ObjectDetectorOptions options =
                    ObjectDetector.ObjectDetectorOptions.builder()
                            .setBaseOptions(baseOptions)
                            .setScoreThreshold(CONFIDENCE_THRESHOLD)
                            .setMaxResults(MAX_RESULTS)
                            .build();

            objectDetector = ObjectDetector.createFromFileAndOptions(
                    this,
                    "model.tflite",
                    options
            );

            modelLoaded = true;
            Log.d(TAG, "MODEL LOADED OK - threshold=" + CONFIDENCE_THRESHOLD
                    + " maxResults=" + MAX_RESULTS);
        } catch (Exception e) {
            Log.e(TAG, "MODEL LOAD FAILED", e);
            modelLoaded = false;
        }
    }

    private void startCamera() {
        Log.d(TAG, "=== Starting camera ===");

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Use default YUV_420_888 format — most reliable for detection
                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);
                Log.d(TAG, "CAMERA: ImageAnalysis analyzer set");

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

                Log.d(TAG, "CAMERA: Bound to lifecycle OK");

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "CAMERA: Error starting", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Convert YUV_420_888 ImageProxy to NV21 byte array, then to Bitmap via YuvImage.
     * This is the most reliable conversion method for CameraX image analysis.
     */
    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

        // YUV_420_888 has 3 planes: Y, U, V
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        // NV21 format: Y plane followed by interleaved VU
        byte[] nv21 = new byte[ySize + uSize + vSize];

        // Copy Y plane
        yBuffer.get(nv21, 0, ySize);
        // Copy VU planes (NV21 expects V before U)
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // Convert NV21 to JPEG, then decode to Bitmap
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 90, out);
        byte[] jpegBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
    }

    private void analyzeImage(ImageProxy imageProxy) {
        if (!modelLoaded || objectDetector == null) {
            Log.w(TAG, "SKIP: model not loaded");
            imageProxy.close();
            return;
        }

        try {
            int width = imageProxy.getWidth();
            int height = imageProxy.getHeight();
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();

            Log.d(TAG, "FRAME: " + width + "x" + height
                    + " format=" + imageProxy.getFormat()
                    + " rotation=" + rotationDegrees
                    + " planes=" + imageProxy.getPlanes().length);

            // Convert YUV ImageProxy to Bitmap
            Bitmap bitmap = imageProxyToBitmap(imageProxy);

            if (bitmap == null) {
                Log.e(TAG, "FRAME: bitmap conversion returned null!");
                return;
            }

            Log.d(TAG, "BITMAP: " + bitmap.getWidth() + "x" + bitmap.getHeight()
                    + " config=" + bitmap.getConfig());

            // Rotate bitmap to correct orientation
            if (rotationDegrees != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationDegrees);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Log.d(TAG, "ROTATED: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            }

            int finalWidth = bitmap.getWidth();
            int finalHeight = bitmap.getHeight();

            // Convert to TensorImage and run detection
            TensorImage image = TensorImage.fromBitmap(bitmap);
            List<Detection> results = objectDetector.detect(image);

            Log.d(TAG, "DETECTIONS: " + results.size());

            for (Detection detection : results) {
                if (!detection.getCategories().isEmpty()) {
                    String label = detection.getCategories().get(0).getLabel();
                    float score = detection.getCategories().get(0).getScore();
                    Log.d(TAG, "  >> " + label + " = "
                            + String.format("%.1f%%", score * 100)
                            + " box=" + detection.getBoundingBox());
                }
            }

            // Update overlay on UI thread
            if (!results.isEmpty()) {
                final Bitmap finalBitmap = bitmap;
                runOnUiThread(() -> overlayView.setDetections(
                        results, finalWidth, finalHeight));
            } else {
                runOnUiThread(() -> overlayView.clear());
            }

        } catch (Exception e) {
            Log.e(TAG, "ANALYZE ERROR", e);
        } finally {
            imageProxy.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}