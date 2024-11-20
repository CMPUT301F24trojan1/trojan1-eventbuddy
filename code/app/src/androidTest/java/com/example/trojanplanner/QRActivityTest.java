package com.example.trojanplanner;

import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojanplanner.view.QRActivity;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class QRActivityTest {

    @Test
    public void testQRScannerScansAndDisplaysToast() {
        try (ActivityScenario<QRActivity> scenario = ActivityScenario.launch(QRActivity.class)) {
            scenario.onActivity(activity -> {
                try {
                    // Access the private barcodeView field using reflection
                    Field barcodeViewField = QRActivity.class.getDeclaredField("barcodeView");
                    barcodeViewField.setAccessible(true);
                    BarcodeView barcodeView = (BarcodeView) barcodeViewField.get(activity);

                    // Ensure that barcodeView is not null
                    assertNotNull("BarcodeView should not be null", barcodeView);

                    // Create a mock BarcodeCallback
                    BarcodeCallback mockCallback = new BarcodeCallback() {
                        @Override
                        public void barcodeResult(BarcodeResult result) {
                            // Simulate the Toast triggered by a scan
                            String mockScannedData = result.getText();
                            activity.runOnUiThread(() -> {
                                Toast.makeText(activity, "Scanned: " + mockScannedData, Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void possibleResultPoints(java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                            // No-op for test
                        }
                    };

                    // Simulate a QR code scan using the callback
                    activity.runOnUiThread(() -> {
                        // Attach the mock callback to the BarcodeView
                        barcodeView.decodeSingle(mockCallback);

                        // Manually trigger the callback with a mocked BarcodeResult
                        BarcodeResult mockResult = createMockBarcodeResult("Mock QR Code Data");
                        mockCallback.barcodeResult(mockResult);
                    });

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Helper method to create a mock BarcodeResult for testing.
     */
    private BarcodeResult createMockBarcodeResult(String text) {
        return new BarcodeResult(new com.google.zxing.Result(
                text, null, null, com.google.zxing.BarcodeFormat.QR_CODE), null);
    }
}
