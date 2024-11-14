package com.example.trojanplanner;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.IdlingResource;
import android.view.View;

public class ViewVisibilityIdlingResource implements IdlingResource {
    private final View view;
    private final int expectedVisibility;
    private volatile ResourceCallback resourceCallback;

    public ViewVisibilityIdlingResource(View view, int expectedVisibility) {
        this.view = view;
        this.expectedVisibility = expectedVisibility;
    }

    @Override
    public String getName() {
        return this.getClass().getName() + ":" + view.getId();
    }

    @Override
    public boolean isIdleNow() {
        boolean isIdle = view.getVisibility() == expectedVisibility;
        if (isIdle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
