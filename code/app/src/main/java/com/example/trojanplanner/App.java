package com.example.trojanplanner;

import android.app.Application;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;

// https://stackoverflow.com/questions/11411395/how-to-get-current-foreground-activity-context-in-android

/**
 * An Application class which gets created when the app is initially started. Its purpose is to hold
 * an ActivityManager, which will always be up-to-date with the current activity. This
 * provides a way for non-fragment/non-activity classes to get the currently running activity
 * on the app in case they need to register actions or access UI components (e.g. PhotoPicker)
 */
public class App extends Application {

    public static ActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        activityManager = new ActivityManager(this);
    }
}
