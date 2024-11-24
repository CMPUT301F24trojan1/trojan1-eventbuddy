package com.example.trojanplanner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojanplanner.R;

/**
 * {@link EmptyFragmentActivity} is a simple activity that acts as a container for a fragment.
 * This activity is often used when you want to display a fragment within an empty layout or when
 * you need a placeholder activity to host fragments dynamically.
 * <p>
 * It sets the content view to a layout that contains a fragment placeholder, allowing fragments
 * to be added programmatically during the lifecycle of this activity.
 * </p>
 * <p>
 * The main purpose of this activity is to serve as a minimal host for fragments without
 * including extra UI elements like a toolbar or navigation buttons.
 * </p>
 *
 * @author Dricmoy Bhattacharjee
 */
public class EmptyFragmentActivity extends AppCompatActivity {

    /**
     * Called when the activity is created. It sets the content view to the layout for this activity,
     * which is typically a simple layout that holds a fragment.
     *
     * @param savedInstanceState The saved instance state of the activity, if available.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_fragment);
    }
}
