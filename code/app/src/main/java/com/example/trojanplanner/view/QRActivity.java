package com.example.trojanplanner.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojanplanner.HelperFragments.HelpFragment;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QRActivity extends AppCompatActivity {
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

        startQRScanner();

        // custom back press handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing, ignore back button click
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
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
        HelpFragment helpFragment = new HelpFragment();
        helpFragment.show(getSupportFragmentManager(), "HelpFragment");
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
}
