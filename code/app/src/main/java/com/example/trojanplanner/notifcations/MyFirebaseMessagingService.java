package com.example.trojanplanner.notifcations;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.trojanplanner.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            // Handle the notification payload
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Handle the new token if needed
        // You may want to send this token to your server for later push targeting
        System.out.println("New FCM Token: " + token);
    }

    private void sendNotification(String title, String messageBody) {
        // Create a NotificationManager instance
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        createNotificationChannel();

        // Create a notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)  // Set your own small icon
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Check for POST_NOTIFICATIONS permission on Android 13+ and request if missing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Optionally, you can request the permission here if necessary
                // You can trigger a permission request and then handle it in onRequestPermissionsResult
                return;
            }
        }

        // Send the notification
        notificationManager.notify(0, notificationBuilder.build());
    }

    // Method to create the notification channel for Android O and higher
    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for app notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }
}