package com.example.trojanplanner.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.App;
import com.example.trojanplanner.ProfileUtils.ProfileFragment;
import com.example.trojanplanner.QRUtils.QRCodeUtil;
import com.example.trojanplanner.QRUtils.QRHelpFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityQrBinding;
import com.example.trojanplanner.events.EventDetailsDialogFragment;
import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.ArrayList;
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
    private FocusOverlayView focusOverlayView;
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

        deviceId = App.deviceId;
        currentUser = (Entrant) App.currentUser;

        database = Database.getDB();  // or use a singleton if you have one

        barcodeView = findViewById(R.id.barcode_scanner);
        focusOverlayView = findViewById(R.id.focus_overlay);
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

        startQRScanner();

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
        // Set the formats for the scanner
        Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);
        barcodeView.setDecoderFactory(new DefaultDecoderFactory(formats));

        // Get the preview size and set it to the overlay
        barcodeView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int previewWidth = barcodeView.getWidth();
            int previewHeight = barcodeView.getHeight();
            focusOverlayView.setPreviewSize(previewWidth, previewHeight);
        });

        // Continuous decoding
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result == null) {
                    Log.d("QRActivity", "No result from barcode scanner.");
                    return;
                }

                String eventCode = result.getText();  // Use the scanned QR code text

                // Show a toast with the scanned result
                Toast.makeText(QRActivity.this, "Scanned: " + eventCode, Toast.LENGTH_SHORT).show();

                // Define the success action for retrieving the event
                OnSuccessListener<DocumentSnapshot> successAction = documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Manually create the Event object
                        Event event = new Event();
                        event.setEventId(documentSnapshot.getString("eventID"));
                        event.setName(documentSnapshot.getString("name"));
                        event.setDescription(documentSnapshot.getString("description"));
                        event.setPrice(0);
                        event.setStatus(documentSnapshot.getString("status"));
                        event.setTotalSpots(4L);
                        event.setWaitlistCapacity(2L);
                        event.setPictureFilePath(documentSnapshot.getString("eventPhoto"));

                        // Log the retrieved event
                        Log.d("QRActivity", "Event retrieved: " + event.getName());

                        // Navigate to the EventDetailsFragment
                        navigateToEventDetailsFragment(event);
                    } else {
                        Log.d("QRActivity", "Event not found");
                        Toast.makeText(QRActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                };

                // Define the failure action
                OnFailureListener failureAction = e -> {
                    Log.d("QRActivity", "Failed to retrieve event");
                    Toast.makeText(QRActivity.this, "Failed to fetch event", Toast.LENGTH_SHORT).show();
                };

                Log.d("QRActivity", "Querying for event with ID: " + eventCode);
                // Query Firestore for the event document by its ID
                database.getEventDocumentById(eventCode, successAction, failureAction);

                Log.d("QRActivity", "Pausing barcode right now for event code: " + eventCode);
                barcodeView.pause();  // Pause scanning after a successful scan
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Pass the detected points to the overlay for visualization
                if (!resultPoints.isEmpty()) {
                    Log.d("QRActivity", "Possible result points: " + resultPoints.size());

                    //focusOverlayView.addFocusPoints(resultPoints);  // Update the overlay with new points
                }
            }
        });

        barcodeView.resume();
    }

    /**
     * Navigates to the EventDetailsFragment with the event and user data.
     *
     * @param event The event data that should be passed to the EventDetailsFragment.
     */
    private void navigateToEventDetailsFragment(Event event) {
        // Show the EventDetailsDialogFragment as a Dialog
        EventDetailsDialogFragment eventDetailsDialogFragment = EventDetailsDialogFragment.newInstance(event, (Entrant) App.currentUser); // Assuming you still need the Entrant object

        // Show the dialog fragment
        eventDetailsDialogFragment.show(getSupportFragmentManager(), "EventDetailsDialog");
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
                // Bundle attributes to be passed here i.e. intent.putExtra(...)
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.profileActivity) {
                Intent intent = new Intent(QRActivity.this, ProfileActivity.class);
                // Bundle attributes to be passed here i.e. intent.putExtra(...)
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
