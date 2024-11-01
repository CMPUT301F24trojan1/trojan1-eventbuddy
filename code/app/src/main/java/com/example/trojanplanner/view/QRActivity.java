package com.example.trojanplanner.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityQrBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QRActivity extends AppCompatActivity {
    private @NonNull ActivityQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

    }

    /**
     * Sets up the navigation for the BottomNavigationView.
     * <p>
     * This method initializes the BottomNavigationView and sets the selected item
     * to the QR activity. It also establishes a listener for item selection
     * events. When the user selects an item in the navigation bar, the following
     * actions occur:
     * <ul>
     *     <li>When the home navigation item is selected, the user is navigated
     *     to {@link MainActivity}.</li>
     *     <li>When the profile navigation item is selected, the user is navigated
     *     to {@link ProfileActivity}.</li>
     *     <li>When the QR activity navigation item is selected, the user remains
     *     in the current {@link QRActivity}.</li>
     * </ul>
     *
     * This method should be called during the creation of the activity to
     * ensure that the navigation setup is complete and responsive to user
     * interactions.
     *
     * @author Dricmoy Bhattacharjee
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Set the selected item to profileActivity
        navView.setSelectedItemId(R.id.qrActivity);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(QRActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.profileActivity) {
                startActivity(new Intent(QRActivity.this, ProfileActivity.class));
                return true;
            } else return item.getItemId() == R.id.qrActivity; // Stay in the same activity
        });
    }

}