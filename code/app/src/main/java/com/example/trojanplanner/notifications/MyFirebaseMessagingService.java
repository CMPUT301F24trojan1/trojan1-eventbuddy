package com.example.trojanplanner.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.trojanplanner.R;
import com.example.trojanplanner.view.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Custom FirebaseMessagingService to handle incoming messages from Firebase Cloud Messaging (FCM).
 * This service listens for new tokens and incoming notifications, and displays notifications when appropriate.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "EventAnnouncements";

    /**
     * Called when a new token is generated for Firebase Cloud Messaging.
     * This method can be used to send the token to your server for later use.
     *
     * @param token The new token generated for FCM.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New token generated: " + token);
        // Send the token to your server if needed
    }

    /**
     * Called when a message is received from Firebase Cloud Messaging.
     * This method checks if the message contains a notification payload and displays the notification if necessary.
     *
     * @param remoteMessage The remote message received from FCM.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + body);

            // Check if the app has permission to post notifications
            if (hasNotificationPermission()) {
                sendNotification(title, body);
            } else {
                Log.d(TAG, "Notification permission not granted. Skipping notification.");
                // You can also inform the user to enable notification permissions
                Toast.makeText(this, "Notifications are disabled, please enable them in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Checks if the app has permission to post notifications.
     * This check is only relevant for Android 13 and above. For older Android versions, permission is granted by default.
     *
     * @return true if the app has permission to post notifications, false otherwise.
     */
    private boolean hasNotificationPermission() {
        // Check if the app has permission to post notifications (only for Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // For older Android versions, permission is granted by default
        }
    }

    /**
     * Sends a notification to the user with the provided title and body.
     * This method creates a notification channel (for Android 8.0 and above), builds a notification,
     * and then displays it to the user.
     *
     * @param title The title of the notification.
     * @param body The body text of the notification.
     */
    private void sendNotification(String title, String body) {
        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "default",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for specific event announcements.");
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to open an activity when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class); // Change to your target activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Replace with your app icon
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}
