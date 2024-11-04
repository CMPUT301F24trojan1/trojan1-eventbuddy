package com.example.trojanplanner.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trojanplanner.HelperFragments.QRHelpFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityQrBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * QRActivity is responsible for managing the QR code scanning functionality.
 * It initializes the camera permissions, starts the scanner, and handles
 * the navigation within the app. This activity also includes a help feature
 * that provides users with guidance on how to use the QR scanner.
 *
 * @author Dricmoy Bhattacharjee
 */
public class QRActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private BarcodeView barcodeView;
    private EditText etInput;
    private @NonNull ActivityQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        barcodeView = findViewById(R.id.barcode_scanner);
        ImageButton helpButton = findViewById(R.id.qr_help_button);

        setupNavigation();

        helpButton.setOnClickListener(v -> openSlideShowActivity());

        // Check and request camera permission
        checkCameraPermission();

        // custom back press handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing, ignore back button click
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void openSlideShowActivity() {
        Intent intent = new Intent(QRActivity.this, SlideShowActivity.class);
        startActivity(intent);
    }

    /**
     * Checks if the app has permission to use the camera. If permission is not
     * granted, it requests the permission from the user. If permission is granted,
     * it starts the QR code scanner.
     *
     * @author Dricmoy Bhattacharjee
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
     * Starts the continuous QR scanner. It sets the decoder factory to recognize
     * QR codes and defines the behavior when a QR code is scanned. The scanned
     * result is displayed in an EditText field and a toast message is shown.
     *
     * @author Dricmoy Bhattacharjee
     */
    private void startQRScanner() {
        Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);
        barcodeView.setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(new BarcodeCallback() {
            public void barcodeResult(Result result) {
                if (result != null) {
                    // Display the scanned result in the EditText and show a toast
                    Toast.makeText(QRActivity.this, "Scanned: " + result.getText(), Toast.LENGTH_LONG).show();
                    etInput.setText(result.getText());
                    barcodeView.pause();  // Pause scanning after a successful scan
                }
            }

            @Override
            public void barcodeResult(BarcodeResult result) { }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) { }
        });
        barcodeView.resume();
    }

    /**
     * Opens the help fragment to provide guidance to the user on using the QR scanner.
     *
     * @author Dricmoy Bhattacharjee
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
     * <p>
     * This method initializes the BottomNavigationView and sets the selected item
     * to the QR activity. It also establishes a listener for item selection
     * events. When the user selects an item in the navigation bar, the following
     * actions occur:
     * <ul>
     *     <li>When the home navigation item is selected, the user is navigated
     *     to {@link MainActivity}.</li>
     *     <li>When the profile navigation item is selected, the user is navigated
     *     to {@link ProfileActivity}.</li>
     *     <li>When the QR activity navigation item is selected, the user remains
     *     in the current {@link QRActivity}.</li>
     * </ul>
     *
     * This method should be called during the creation of the activity to
     * ensure that the navigation setup is complete and responsive to user
     * interactions.
     *
     * @author Dricmoy Bhattacharjee
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.qrActivity);

        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(QRActivity.this, MainActivity.class));
                startActivity(new Intent(QRActivity.this, SlideShowActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.profileActivity) {
                startActivity(new Intent(QRActivity.this, ProfileActivity.class));
                startActivity(new Intent(QRActivity.this, SlideShowActivity.class));
                finish();
                return true;
            } else return item.getItemId() == R.id.qrActivity;
        });
    }

    /**
     * Handles the result of the permission request. If the camera permission is granted,
     * it starts the QR scanner. If denied, it shows a message to the user indicating
     * that the permission is required to scan QR codes.
     *
     * @param requestCode The request code for the permission.
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission request.
     *
     * @author Dricmoy Bhattacharjee
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
