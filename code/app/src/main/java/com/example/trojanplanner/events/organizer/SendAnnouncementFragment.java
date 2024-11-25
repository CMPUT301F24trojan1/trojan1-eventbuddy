package com.example.trojanplanner.events.organizer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;

public class SendAnnouncementFragment extends Fragment {

    private EditText userIdsInput, titleInput, messageInput;
    private Button sendButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_annoucement, container, false);

        // Initialize UI components
        userIdsInput = view.findViewById(R.id.edit_user_ids);
        titleInput = view.findViewById(R.id.edit_title);
        messageInput = view.findViewById(R.id.edit_message);
        sendButton = view.findViewById(R.id.button_send_notification);

        // Set up button click listener
        sendButton.setOnClickListener(v -> {
            String userIds = userIdsInput.getText().toString().trim();
            String title = titleInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (TextUtils.isEmpty(userIds) || TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Split user IDs and send announcements for each
            String[] userIdArray = userIds.split(",");
            for (String userId : userIdArray) {
                sendAnnouncement(userId.trim(), title, message);
            }

            Toast.makeText(getContext(), "Notifications sent successfully!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    /**
     * Sends a notification using Android's notification system.
     *
     * @param userId Unique identifier for the event/user to create a channel and notification.
     * @param title   The title of the notification.
     * @param message The message/body of the notification.
     */
    private void sendAnnouncement(String userId, String title, String message) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), "User ID, title, and message are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String channelId = "UserChannel_" + userId; // Unique channel ID for the user

            // Get NotificationManager
            NotificationManager notificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) {
                Toast.makeText(getContext(), "Notification Manager not available.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Notification Channel for Android 8.0+ (Oreo and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "User Notifications for " + userId,
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("Notifications specifically for user " + userId);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            // Build the Notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), channelId)
                    .setSmallIcon(R.drawable.logo) // Replace with your app's notification icon
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            // Show the Notification
            int notificationId = (userId + title + message).hashCode(); // Generate unique notification ID
            notificationManager.notify(notificationId, notificationBuilder.build());

            Toast.makeText(getContext(), "Notification sent to user " + userId, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error sending notification to user " + userId, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}