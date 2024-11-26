package com.example.trojanplanner.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trojanplanner.App;
import com.example.trojanplanner.QRUtils.QRHelpFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityQrBinding;
import com.example.trojanplanner.events.entrant.EventDetailsDialogFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

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
    private @NonNull ActivityQrBinding binding;
    private Database database;
    private static final String BACKEND_URL = App.BACKEND_URL;
    private final OkHttpClient client = new OkHttpClient();

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

        database = Database.getDB();  // or use a singleton if you have one

        barcodeView = findViewById(R.id.barcode_scanner);
        FocusOverlayView focusOverlayView = findViewById(R.id.focus_overlay);
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
        try {
            // Set the formats for the scanner
            Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);
            barcodeView.setDecoderFactory(new DefaultDecoderFactory(formats));

            // Continuous decoding
            barcodeView.decodeContinuous(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    if (result == null) {
                        Log.d("QRActivity", "No result from barcode scanner.");
                        return;
                    }

                    String qrHash = result.getText();  // Use the scanned QR code text
                    Log.d("QRActivity", "Scanned QR Hash: " + qrHash);

                    Toast.makeText(QRActivity.this, "Scanned: " + qrHash, Toast.LENGTH_SHORT).show();

                    // Define the success and failure actions for the `getQRData` query
                    Database.QuerySuccessAction getQRDataSuccessAction = eventId -> {
                        Log.d("QRActivity", "QR Data success. Event ID: " + eventId);

                        // Define success and failure actions for retrieving the event document
                        Database.QuerySuccessAction getEventSuccessAction = new Database.QuerySuccessAction() {
                            @Override
                            public void OnSuccess(Object object) {
                                Event event = (Event) object;  // Cast the object to Event

                                if (event != null) {
                                    // Log the retrieved event
                                    Log.d("QRActivity", "Event retrieved: " + event.getName());

                                    // Navigate to the EventDetailsFragment
                                    navigateToEventDetailsFragment(event);
                                } else {
                                    Log.d("QRActivity", "Event not found");
                                    Toast.makeText(QRActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        Database.QueryFailureAction getEventFailureAction = new Database.QueryFailureAction() {
                            @Override
                            public void OnFailure() {
                                Log.d("QRActivity", "Failed to retrieve event.");
                                Toast.makeText(QRActivity.this, "Failed to fetch event", Toast.LENGTH_SHORT).show();
                            }
                        };

                        // Query the `getEvent` method with the scanned QR hash as event ID
                        Log.d("QRActivity", "Querying for event data with hash: " + eventId);
                        database.getEvent(getEventSuccessAction, getEventFailureAction, (String)eventId);
                    };

                    Database.QueryFailureAction getQRDataFailureAction = new Database.QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            Log.d("QRActivity", "QR Data failure. Invalid QR Hash or missing event.");
                            Toast.makeText(QRActivity.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        }
                    };

                    // Query the `getQRData` method with the scanned QR hash
                    Log.d("QRActivity", "Querying for QR Data with hash: " + qrHash);
                    database.getQRData(getQRDataSuccessAction, getQRDataFailureAction, qrHash);

                    Log.d("QRActivity", "Pausing barcode scanner after processing QR Hash: " + qrHash);
                    barcodeView.pause();  // Pause scanning after processing the QR code
                }

                @Override
                public void possibleResultPoints(List<ResultPoint> resultPoints) {
                    // Handle possible result points (optional)
                }
            });


            barcodeView.resume();
        } catch (Exception e) {
            Log.e("QRActivity", "Unexpected error in QR scanner", e);
            Toast.makeText(QRActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAndInsertLocation(String eventID) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        String deviceId = App.currentUser.getDeviceId(); // Assuming this is available

                        Log.d("QRActivity", "Device location retrieved: Lat=" + latitude + ", Long=" + longitude);

                        // Call insertLocation with the event ID and current device location
                        Database.QuerySuccessAction successAction = obj -> {
                            Log.d("QRActivity", "Location successfully inserted for Event ID: " + eventID);
                        };

                        Database.QueryFailureAction failureAction = () -> {
                            Log.d("QRActivity", "Failed to insert location for Event ID: " + eventID);
                        };

                        database.insertLocation(eventID, deviceId, latitude, longitude, successAction, failureAction);
                    } else {
                        Log.d("QRActivity", "Failed to retrieve location.");
                        Toast.makeText(this, "Unable to fetch location. Please ensure GPS is enabled.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QRActivity", "Error fetching location", e);
                    Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Navigates to the EventDetailsFragment with the event and user data.
     *
     * @param event The event data that should be passed to the EventDetailsFragment.
     */
    private void navigateToEventDetailsFragment(Event event) {
        // Show the EventDetailsDialogFragment as a Dialog
        EventDetailsDialogFragment eventDetailsDialogFragment = EventDetailsDialogFragment.newInstance(event, (Entrant) App.currentUser); // Assuming you still need the Entrant object
        eventDetailsDialogFragment.show(getSupportFragmentManager(), "EventDetailsDialog");
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

    /**
     * Opens the help fragment to provide guidance to the user on using the QR scanner.
     */
    private void openHelpFragment() {
        QRHelpFragment QRHelpFragment = new QRHelpFragment();
        QRHelpFragment.show(getSupportFragmentManager(), "HelpFragment");
    }

    /**
     * Logic to send an announcement for the given topic.
     *
     * @param topic   The topic to which the notification will be sent.
     * @param title   The title of the notification.
     * @param message The message of the notification.
     */
    public void sendAnnouncement(String topic, String title, String message) {
        if (topic == null || title == null || message == null) {
            return;
        }

        // Create JSON payload
        JSONObject jsonPayload = new JSONObject();
        try {
            jsonPayload.put("topic", topic);
            jsonPayload.put("title", title);
            jsonPayload.put("message", message);
        } catch (JSONException e) {
            Log.e("Notification", "JSON creation failed: " + e.getMessage());
            return;
        }

        // Create the request body with JSON
        RequestBody body = RequestBody.create(
                jsonPayload.toString(),
                MediaType.get("application/json")
        );

        // Create the POST request to your backend
        Request request = new Request.Builder()
                .url(BACKEND_URL)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Notification", "Notification sent successfully!");
                } else {
                    Log.e("Notification", "Notification failed with response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e("Notification", "Error sending notification: " + e.getMessage());
            }
        });
    }
}

/*
*
            // Get the preview size and set it to the overlay
            barcodeView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                int previewWidth = barcodeView.getWidth();
                int previewHeight = barcodeView.getHeight();
                focusOverlayView.setPreviewSize(previewWidth, previewHeight);
            });
*
*                 @Override
                public void possibleResultPoints(List<ResultPoint> resultPoints) {
                    // Pass the detected points to the overlay for visualization
                    if (!resultPoints.isEmpty()) {
                        Log.d("QRActivity", "Possible result points: " + resultPoints.size());
                        // Update the overlay with new points
                        //focusOverlayView.addFocusPoints(resultPoints);
                    }
                }
* */