package com.example.trojanplanner.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojanplanner.ProfileUtils.ProfileFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.databinding.ActivityProfileBinding;
import com.example.trojanplanner.model.Entrant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    private @NonNull ActivityProfileBinding binding;

    public String deviceId;
    public Entrant currentUser;

    public PhotoPicker photoPicker;
    public ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Safely retrieve data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            deviceId = extras.getString("deviceId");
            currentUser = (Entrant) extras.getSerializable("user");

            if (deviceId == null || currentUser == null) {
                // Handle the case where essential data is missing
                Toast.makeText(this, "Missing essential data", Toast.LENGTH_SHORT).show();
                finish(); // Optionally finish the activity if the data is missing
                return;
            }
        } else {
            // Handle the case where extras are not provided
            Toast.makeText(this, "No extras provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display ProfileFragment in the fragment container
        if (savedInstanceState == null) {
            profileFragment = new ProfileFragment(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_fragment_container, profileFragment)
                    .commit();
        }

        // Initialize photo picker
        photoPicker = new PhotoPicker();
        photoPicker.initPhotoPicker(profileFragment.photoPickerCallback);

        // Setup bottom navigation
        setupNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Ensure ProfileFragment is reset with the latest user data
        if (profileFragment != null) {
            profileFragment.resetState(currentUser);
        }
    }

    /**
     * Sets up the navigation for the BottomNavigationView.
     * @author Dricmoy Bhattacharjee
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Set the selected item to profileActivity
        navView.setSelectedItemId(R.id.profileActivity);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                // Navigate to MainActivity
                if (currentUser != null) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.qrActivity) {
                // Navigate to QRActivity
                if (currentUser != null) {
                    Intent intent = new Intent(ProfileActivity.this, QRActivity.class);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else {
                // Stay in the same activity
                return item.getItemId() == R.id.profileActivity;
            }
        });
    }
}
