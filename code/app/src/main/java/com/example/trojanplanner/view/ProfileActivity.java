package com.example.trojanplanner.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojanplanner.App;
import com.example.trojanplanner.ProfileUtils.ProfileFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.databinding.ActivityProfileBinding;
import com.example.trojanplanner.model.Entrant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    private @NonNull ActivityProfileBinding binding;


    public PhotoPicker photoPicker;
    public ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Display ProfileFragment in the fragment container
        if (savedInstanceState == null) {
            profileFragment = new ProfileFragment(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_fragment_container, profileFragment)
                    .commit();
        }

        // Init a photopicker which uses a callback set in ProfileFragment
        photoPicker = new PhotoPicker();
        photoPicker.initPhotoPicker(profileFragment.photoPickerCallback);

        setupNavigation();

        // Future code will be written in onStart to make sure the fragment fully loads properly

    }

    // Call future things from here because in onCreate the fragment container is not fully set up yet
    @Override
    protected void onStart() {
        super.onStart();
        profileFragment.resetState(App.currentUser);
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
                if (App.currentUser != null) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    // Bundle attributes to be passed here i.e. intent.putExtra(...)
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.qrActivity) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(ProfileActivity.this, QRActivity.class);
                    // Bundle attributes to be passed here i.e. intent.putExtra(...)
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.profileActivity; // Stay in the same activity
        });
    }
}