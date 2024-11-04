package com.example.trojanplanner.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trojanplanner.HelperFragments.QRHelpFragment;
import com.example.trojanplanner.QRUtils.QRCodeUtil;
import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityQrBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.RGBLuminanceSource;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QRActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int DELAY_MILLIS = 3000; // 3-second delay
    private BarcodeView barcodeView;
    private EditText etInput;
    private ImageView qrCodeImageView;
    private @NonNull ActivityQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        barcodeView = findViewById(R.id.barcode_scanner);
        etInput = findViewById(R.id.etInput);
        qrCodeImageView = findViewById(R.id.qrCodeImageView); // ImageView to show the generated QR code
        ImageButton helpButton = findViewById(R.id.qr_help_button);

        setupNavigation();

        helpButton.setOnClickListener(v -> openHelpFragment());

        // Check and request camera permission
        checkCameraPermission();

        // Generate and display a random QR code with a delay before scanning
        String randomText = generateRandomString(10);  // Generate a random 10-character string
        generateQRCodeWithDelay(randomText);

        // Custom back press handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing, ignore back button click
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Checks if the app has permission to use the camera. If permission is not
     * granted, it requests the permission from the user. If permission is granted,
     * it starts the QR code scanner.
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startQRScanner(); // Start the scanner if permission is already granted
        }
    }

    /**
     * Starts the continuous QR scanner and defines the behavior when a QR code is scanned.
     */
    private void startQRScanner() {
        Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);
        barcodeView.setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(new BarcodeCallback() {
            public void barcodeResult(BarcodeResult result) {
                if (result != null && result.getText() != null) {
                    String qrContent = result.getText();

                    // Show the scanned result in EditText
                    etInput.setText(qrContent);

                    // Check if content is a URL and redirect if true
                    if (qrContent.startsWith("http://") || qrContent.startsWith("https://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrContent));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE); // Ensure it's handled as a URL
                        startActivity(intent);
                    } else {
                        // Handle other types of content if needed
                        Toast.makeText(QRActivity.this, "Scanned: " + qrContent, Toast.LENGTH_LONG).show();
                    }

                    // Pause the scanner to prevent multiple detections
                    barcodeView.pause();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) { }
        });
        barcodeView.resume();
    }

    /**
     * Generates a QR code from a random string, displays it, and then scans it after a delay.
     *
     * @param text The random text to be encoded as a QR code.
     */
    private void generateQRCodeWithDelay(String text) {
        Bitmap qrCodeBitmap = QRCodeUtil.generateQRCode(text);
        if (qrCodeBitmap != null) {
            // Display the generated QR code in ImageView
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);

            // Delay scanning by 3 seconds to allow user to see the QR code
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Scan the displayed QR code after delay
                String decodedText = decodeQRCodeBitmap(qrCodeBitmap);
                if (decodedText != null) {
                    etInput.setText(decodedText);
                    Toast.makeText(this, "Scanned: " + decodedText, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Failed to decode QR code", Toast.LENGTH_SHORT).show();
                }
                // Hide QR code after scanning
                qrCodeImageView.setVisibility(View.GONE);
            }, DELAY_MILLIS);
        } else {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Decodes a QR code from a given Bitmap.
     *
     * @param bitmap The Bitmap containing the QR code.
     * @return The decoded text from the QR code, or null if decoding fails.
     */
    private String decodeQRCodeBitmap(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * @param length The desired length of the generated string.
     * @return A random alphanumeric string.
     */
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    /**
     * Opens the help fragment to provide guidance to the user on using the QR scanner.
     */
    private void openHelpFragment() {
        QRHelpFragment QRHelpFragment = new QRHelpFragment();
        QRHelpFragment.show(getSupportFragmentManager(), "HelpFragment");
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    /**
     * Sets up the navigation for the BottomNavigationView.
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.qrActivity);

        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(QRActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.profileActivity) {
                startActivity(new Intent(QRActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else return item.getItemId() == R.id.qrActivity;
        });
    }

    /**
     * Handles the result of the permission request. If the camera permission is granted,
     * it starts the QR scanner. If denied, it shows a message to the user indicating
     * that the permission is required to scan QR codes.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner(); // Start the scanner if permission is granted
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
