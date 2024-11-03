package com.example.trojanplanner.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.trojanplanner.QR.QRActivity;
import com.example.trojanplanner.events.EventsFragment;
import com.example.trojanplanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trojanplanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static Activity activity; // Important to allow non-activity classes to trigger UI components, i.e. PhotoPicker

    private Button tempButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        setupNavigation();
    }

    /**
     * Sets up the navigation for the BottomNavigationView and the ActionBar.
     * <p>
     * This method initializes the BottomNavigationView and NavController,
     * configuring them to manage navigation between different destinations in
     * the app. It sets up an AppBarConfiguration that defines the top-level
     * navigation destinations. Additionally, it establishes a listener for
     * item selection events within the BottomNavigationView, allowing the
     * user to navigate to the following screens:
     * <ul>
     * <li>{@link EventsFragment} when the events navigation item is selected.
     * <li>{@link QRActivity} when the QR activity item is selected.
     * <li>{@link ProfileActivity} when the profile navigation item is selected.
     * </ul>
     * This method should be called in the onCreate method of the MainActivity
     * to properly set up the navigation when the activity is created.
     *
     * @author Dricmoy Bhattacharjee
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.eventsFragment)
                .build();

        // Initialize NavController with the nav host fragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // Set up ActionBar and BottomNavigationView with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.eventsFragment) {
                navController.navigate(R.id.eventsFragment);
                return true;
            } else if (item.getItemId() == R.id.qrActivity) {
                startActivity(new Intent(MainActivity.this, QRActivity.class));
                return true;
            } else if (item.getItemId() == R.id.profileActivity) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    /**
     * Gets the application context. This is a static method so any other class is able to call this function
     * in order to get the application context itself
     * @return The application context
     */
//    public static Context getAppContext() {
//        return activity.getApplicationContext();
//    }
//
}