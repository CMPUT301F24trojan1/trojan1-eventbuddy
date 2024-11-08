package com.example.trojanplanner;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A class that manages and provides access to the currently active activity in the application.
 * It is registered as an {@link Application.ActivityLifecycleCallbacks} to keep track of the
 * active activity throughout the lifecycle of the app.
 * <p>
 * This class allows non-activity or non-fragment classes to get the current activity and interact
 * with it (e.g., to register actions or access UI components), which is particularly useful
 * for actions like displaying UI components or handling certain activity-specific logic in classes
 * that are not directly tied to the activity lifecycle.
 * </p>
 *
 * <p>For example, it allows other components, such as a photo picker, to know which activity is
 * currently visible and use that context to perform operations.</p>
 *
 * <p>It listens to the application’s lifecycle events and updates the reference to the current
 * activity accordingly.</p>
 *
 * @author Dricmoy Bhattacharjee
 */
public class ActivityManager implements Application.ActivityLifecycleCallbacks {

    private Activity activity;

    /**
     * Constructor that registers the activity lifecycle callbacks with the provided application.
     *
     * @param app The {@link Application} instance to register the callbacks with.
     */
    public ActivityManager(App app) {
        app.registerActivityLifecycleCallbacks(this);
    }

    /**
     * Returns the current activity that is actively running in the application.
     *
     * @return The current activity or {@code null} if no activity is currently running.
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Called when an activity is created. This method updates the reference to the current activity.
     *
     * @param activity The activity that was created.
     * @param bundle   The saved instance state, if available.
     */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        this.activity = activity;
    }

    /**
     * Called when an activity is started. This method updates the reference to the current activity.
     *
     * @param activity The activity that was started.
     */
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * Called when an activity is resumed. This method updates the reference to the current activity.
     *
     * @param activity The activity that was resumed.
     */
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * Called when an activity is paused. This method doesn't affect the current activity reference.
     *
     * @param activity The activity that was paused.
     */
    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        // No action required
    }

    /**
     * Called when an activity is stopped. This method doesn't affect the current activity reference.
     *
     * @param activity The activity that was stopped.
     */
    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // No action required
    }

    /**
     * Called when the activity state is saved. This method doesn't affect the current activity reference.
     *
     * @param activity The activity whose state is being saved.
     * @param bundle   The bundle containing the saved state of the activity.
     */
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        // No action required
    }

    /**
     * Called when an activity is destroyed. This method doesn't affect the current activity reference.
     *
     * @param activity The activity that was destroyed.
     */
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // No action required
    }
}
