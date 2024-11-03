package com.example.trojanplanner.QR;

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
import com.example.trojanplanner.view.MainActivity;
import com.example.trojanplanner.view.ProfileActivity;
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

        helpButton.setOnClickListener(v -> openHelpFragment());

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

    // Function to check camera permission
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

    // Starts the continuous QR scanner
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

    // Handle the result of the permission request
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
