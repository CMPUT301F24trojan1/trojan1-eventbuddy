package com.example.trojanplanner;

import android.app.Activity;
import android.app.Application;
import android.provider.Settings;

import com.example.trojanplanner.model.User;

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

    public static Activity activity;
    public static String deviceId;
    public static User currentUser; // Could currently be an entrant, organizer, or admin

    @Override
    public void onCreate() {
        super.onCreate();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        activityManager = new ActivityManager(this);
    }
}
