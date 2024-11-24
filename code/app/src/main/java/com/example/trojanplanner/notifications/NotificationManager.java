package com.example.trojanplanner.notifications;

import android.util.Log;
import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.List;

public class NotificationManager {

    private static final String BACKEND_URL = "http://10.0.2.2:3000/sendNotification"; // Replace with your backend URL
    private final OkHttpClient client;

    public NotificationManager() {
        this.client = new OkHttpClient();
    }

    /**
     * Notify all users in the waiting list.
     *
     * @param waitlist List of topics for users (e.g., "waitlist_event123").
     * @param title    Notification title.
     * @param message  Notification message.
     */
    public void notifyWaitlist(List<String> waitlist, String title, String message) {
        for (String topic : waitlist) {
            sendAnnouncement(topic, title, message);
        }
    }

    /**
     * Notify all selected users.
     *
     * @param selectedList List of topics for selected users (e.g., "selected_event123").
     * @param title        Notification title.
     * @param message      Notification message.
     */
    public void notifySelected(List<String> selectedList, String title, String message) {
        for (String topic : selectedList) {
            sendAnnouncement(topic, title, message);
        }
    }

    /**
     * Notify all cancelled users.
     *
     * @param cancelledList List of topics for cancelled users (e.g., "cancelled_event123").
     * @param title         Notification title.
     * @param message       Notification message.
     */
    public void notifyCancelled(List<String> cancelledList, String title, String message) {
        for (String topic : cancelledList) {
            sendAnnouncement(topic, title, message);
        }
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
