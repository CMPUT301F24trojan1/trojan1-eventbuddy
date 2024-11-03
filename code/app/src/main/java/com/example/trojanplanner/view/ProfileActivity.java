package com.example.trojanplanner.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojanplanner.HelperFragments.ProfileFragment;
import com.example.trojanplanner.QR.QRActivity;
import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    private @NonNull ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Display ProfileFragment in the fragment container
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_fragment_container, new ProfileFragment())
                    .commit();
        }

        setupNavigation();
    }

    /**
     * Sets up the navigation for the BottomNavigationView.
     * <p>
     * This method initializes the BottomNavigationView and sets the selected item
     * to the profile activity. It also establishes a listener for item selection
     * events. When the user selects an item in the navigation bar, the following
     * actions occur:
     * <ul>
     *  <li>If the home navigation item is selected, {@link MainActivity} is launched.
     *  <li>If the QR activity item is selected, {@link QRActivity} is launched.
     *  <li>When the QR activity navigation item is selected, the user remains in the current {@link ProfileActivity}
     * </ul>
     * This method should be called in the onCreate method of ProfileActivity
     * to properly set up the navigation when the activity is created.
     *
     * @author Dricmoy Bhattacharjee
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Set the selected item to profileActivity
        navView.setSelectedItemId(R.id.profileActivity);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.qrActivity) {
                startActivity(new Intent(ProfileActivity.this, QRActivity.class));
                finish();
                return true;
            } else return item.getItemId() == R.id.profileActivity; // Stay in the same activity
        });
    }
}