package com.example.trojanplanner.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.QRUtils.QRHelpFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityQrBinding;
import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
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
 * <p>
 * The activity handles the camera permission request, manages QR code scanning,
 * and facilitates navigation to other app components like event details and profile.
 * It also includes a help button that opens a help fragment with instructions for the user.
 * </p>
 *
 * @author Dricmoy Bhattacharjee
 */
public class QRActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private BarcodeView barcodeView;
    private EditText etInput;
    private @NonNull ActivityQrBinding binding;
    private Database database;
    private String deviceId;
    private Entrant currentUser;

    /**
     * Called when the activity is created. It sets up the layout, camera permissions,
     * navigation, and other necessary components. Also, it sets a listener on the help button.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deviceId = getIntent().getExtras().getString("deviceId");
        currentUser = (Entrant) getIntent().getExtras().getSerializable("user");

        Database db = new Database();

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

    /**
     * Opens the slideshow activity when the help button is clicked.
     */
    private void openSlideShowActivity() {
        Intent intent = new Intent(QRActivity.this, SlideShowActivity.class);
        startActivity(intent);
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
     * Starts the continuous QR scanner. It sets the decoder factory to recognize
     * QR codes and defines the behavior when a QR code is scanned. The scanned
     * result is displayed in an EditText field and a toast message is shown.
     */
    private void startQRScanner() {
        Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);
        barcodeView.setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(new BarcodeCallback() {
            public void barcodeResult(Result result) {
                if (result != null) {
                    String eventCode = result.getText();  // Use the scanned QR code as the eventId

                    Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {
                            if (object instanceof Event) {
                                Event event = (Event) object;  // Cast to Event
                                Log.d("QRActivity", "Event retrieved: " + event.getName());

                                // Now you can pass the event data to another fragment or perform other actions
                                navigateToEventDetailsFragment(event);
                            }
                        }
                    };

                    // Define failure action
                    Database.QueryFailureAction failureAction = new Database.QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            // Handle failure (e.g., show an error message)
                            Log.d("QRActivity", "Failed to retrieve event");
                            Toast.makeText(QRActivity.this, "Failed to fetch event", Toast.LENGTH_SHORT).show();
                        }
                    };

                    database.getEvent(successAction, failureAction,eventCode);  // Fetch the event using the eventId
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
     * Navigates to the EventDetailsFragment with the event and user data.
     *
     * @param event The event data that should be passed to the EventDetailsFragment.
     */
    private void navigateToEventDetailsFragment(Event event) {
        // Create a bundle to pass the event and currentUser data
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);  // Put the event object in the Bundle
        bundle.putSerializable("currentUser", currentUser);  // Put the currentUser in the Bundle

        // Use NavController to navigate to the EventDetailsFragment and pass the data
        NavController navController = Navigation.findNavController(this, R.id.qrActivity);  // Assuming you have a NavHostFragment in your layout
        navController.navigate(R.id.eventDetailsFragment, bundle);
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
     * This method initializes the BottomNavigationView and sets the selected item
     * to the QR activity. It also establishes a listener for item selection
     * events.
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.qrActivity);

        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                Intent intent = new Intent(QRActivity.this, MainActivity.class);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("user", currentUser);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.profileActivity) {
                Intent intent = new Intent(QRActivity.this, ProfileActivity.class);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("user", currentUser);
                startActivity(intent);
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
