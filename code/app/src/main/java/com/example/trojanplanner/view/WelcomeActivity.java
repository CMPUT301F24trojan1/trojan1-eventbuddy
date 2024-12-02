package com.example.trojanplanner.view;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * The WelcomeActivity is the initial screen displayed when the app is opened. It checks if the user is already logged in
 * and proceeds to either the main activity or the profile activity for user registration. It also handles notification
 * subscriptions and permissions.
 */
public class WelcomeActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    /**
     * Called when the activity is created. Sets up the view, handles system bar padding,
     * and animates the "funny text" after a delay. It also checks if the user is logged in.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        if (App.currentUser == null) {
            String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            addtoNotifications(deviceID);
        }

        progressBar = findViewById(R.id.progressBar);
        View funnyTextView = findViewById(R.id.funnyTextView);
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar during loading

        // Adjust padding of the main view to accommodate system bars (like status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Animate the funny text after a short delay
        new Handler().postDelayed(() -> {
            Animation popOutAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_out);
            funnyTextView.startAnimation(popOutAnimation);
            funnyTextView.setVisibility(View.VISIBLE); // Ensure it's visible before animation
        }, 500); // Delay for half a second before animating the text

        // Delay for 3 seconds before checking if the user is logged in
        new Handler().postDelayed(this::checkUserLogin, 3000);
    }

    /**
     * Checks if the user is logged in. If a user is logged in, the main activity is started,
     * otherwise, an attempt is made to retrieve the entrant info using the device ID.
     */
    private void checkUserLogin() {
        // If a user is already logged in, proceed to MainActivity
        if (App.currentUser != null) {
            startMainActivity();
        } else {
            // If not logged in, attempt to get entrant info from the device ID
            getEntrantFromDeviceId(App.deviceId);
        }
    }

    /**
     * Attempts to retrieve the entrant from the database using the device ID. If successful, it
     * sets the current user, requests notification permission, and subscribes to notifications.
     * If no user is found, it redirects to the ProfileActivity for user registration.
     *
     * @param deviceId The device ID used to retrieve the entrant from the database.
     */
    private void getEntrantFromDeviceId(String deviceId) {
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                App.currentUser = (Entrant) object;  // Set the current user
                requestNotificationPermission(); // Request notification permission
                addtoNotifications(App.currentUser.getDeviceId());
                Toast.makeText(WelcomeActivity.this, "Welcome back, " + App.currentUser.getFirstName() + "!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                // If no user found, move to the ProfileActivity for profile creation
                startProfileActivity();
            }
        };

        Database database = Database.getDB();
        database.getEntrant(successAction, failureAction, deviceId);
    }

    /**
     * Starts the MainActivity and hides the progress bar.
     */
    private void startMainActivity() {
        progressBar.setVisibility(View.GONE); // Hide progress bar
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent); // Start the MainActivity
        finish(); // Finish the WelcomeActivity to remove it from the back stack
    }

    /**
     * Starts the ProfileActivity for user registration and hides the progress bar.
     */
    private void startProfileActivity() {
        progressBar.setVisibility(View.GONE); // Hide progress bar
        Intent intent = new Intent(WelcomeActivity.this, ProfileActivity.class);
        requestNotificationPermission(); // Request notification permission
        startActivity(intent); // Start the ProfileActivity for profile creation
        finish(); // Finish the WelcomeActivity to remove it from the back stack
    }

    /**
     * Subscribes the device to a topic using Firebase Cloud Messaging.
     *
     * @param topic The topic to which the device will subscribe for notifications.
     */
    private void addtoNotifications(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notifications", "Successfully subscribed to the topic: " + topic);
                    } else {
                        Log.e("Notifications", "Failed to subscribe to the topic: " + topic + ". Error: " + task.getException());
                    }
                });
    }

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Requests notification permission from the user if the app is running on Android Tiramisu (API level 33) or higher.
     * If the permission is not granted, the user is directed to the app's notification settings.
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the app has notification permission
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!notificationManager.areNotificationsEnabled()) {
                // Permission not granted, ask user to allow notifications
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // For Android 12 and below, the permission is granted by default.
            Log.d("Notifications", "No need to request permission for notifications");
        }
    }
}