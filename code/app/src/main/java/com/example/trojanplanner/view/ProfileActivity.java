package com.example.trojanplanner.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojanplanner.App;
import com.example.trojanplanner.ProfileUtils.ProfileFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * ProfileActivity displays the user's profile, including their photo and related information.
 * This activity allows the user to interact with different sections of the app via a bottom navigation bar.
 * It also initializes the profile photo picker and handles fragment display for the user profile.
 * <p>
 * The activity uses {@link PhotoPicker} to let the user select a profile photo and {@link ProfileFragment}
 * to display the user's profile details.
 * </p>
 *
 * <p>
 * The bottom navigation allows the user to navigate between different activities:
 * <ul>
 *     <li>{@link MainActivity} - Navigates to the main activity</li>
 *     <li>{@link QRActivity} - Navigates to the QR activity</li>
 *     <li>Stays in the current activity - when the user selects the profile tab</li>
 * </ul>
 *
 */
public class ProfileActivity extends AppCompatActivity {
    private @NonNull ActivityProfileBinding binding;
    public PhotoPicker profileActivityPhotoPicker;
    public ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init a photopicker for the user as well as the facility
        profileActivityPhotoPicker = new PhotoPicker();
        profileActivityPhotoPicker.initPhotoPicker();

        // Display ProfileFragment in the fragment container
        if (savedInstanceState == null) {
            profileFragment = new ProfileFragment(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_fragment_container, profileFragment)
                    .commit();
        }

        setupNavigation();


        // Future code will be written in onStart to make sure the fragment fully loads properly

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
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.qrActivity) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(ProfileActivity.this, QRActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.profileActivity; // Stay in the same activity
        });
    }
}