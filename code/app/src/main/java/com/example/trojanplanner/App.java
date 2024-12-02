package com.example.trojanplanner;

import android.app.Activity;
import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trojanplanner.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

// https://stackoverflow.com/questions/11411395/how-to-get-current-foreground-activity-context-in-android

/**
 * An Application class which gets created when the app is initially started. Its purpose is to
 * store global variables constant across the whole app so that any class can access them.
 * It also maintains an up-to-date reference to the currently running activity context, done
 * through the use of an ActivityManager object. This
 * provides a way for non-fragment/non-activity classes to get the currently running activity
 * on the app in case they need to register actions or access UI components (e.g. PhotoPicker)
 */
public class App extends Application {
    private static ActivityManager activityManager; // Keeps the App activity attribute up to date
    public static final String BACKEND_URL = "https://trojan1-backend.onrender.com/sendNotification"; // Needs to be updated to current url
    public static Activity activity;
    public static String deviceId;
    public static User currentUser; // Could currently be an entrant, organizer, or admin

    // For notification services
    private static final OkHttpClient client = new OkHttpClient();


    @Override
    public void onCreate() {
        super.onCreate();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        activityManager = new ActivityManager(this);
    }

    /**
     * Logic to send an announcement for the given topic.
     *
     * @param topic   The topic to which the notification will be sent.
     * @param title   The title of the notification.
     * @param message The message of the notification.
     */
    public static void sendAnnouncement(String topic, String title, String message) {
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
