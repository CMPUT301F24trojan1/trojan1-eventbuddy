package com.example.trojanplanner;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// https://stackoverflow.com/questions/11411395/how-to-get-current-foreground-activity-context-in-android
// https://stackoverflow.com/a/72531859 - idea to use lifecyclecallbacks to enable getting the current activity from anywhere

/**
 * A class which is instantiated by the App class and manages storing the up-to-date activity that is
 * currently running on the app. This provides a way for non-fragment/non-activity classes to get
 * the currently running activity on the app in case they need to register actions or access UI components (e.g. PhotoPicker)
 */
public class ActivityManager implements Application.ActivityLifecycleCallbacks {

    private Activity activity;

    public ActivityManager(App app) {
        app.registerActivityLifecycleCallbacks(this);
    }


    public Activity getActivity(){
        return activity;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        this.activity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
