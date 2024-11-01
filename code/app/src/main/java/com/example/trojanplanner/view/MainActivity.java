package com.example.trojanplanner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trojanplanner.R;
import com.example.trojanplanner.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static Activity activity; // Important to allow non-activity classes to trigger UI components, i.e. PhotoPicker
    private String deviceId;
    private Button tempButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        tempButton = findViewById(R.id.tempButton);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Get or generate the device ID
        deviceId = getOrCreateDeviceId();

        // Display the device ID to verify it's created
        Toast.makeText(this, "Device ID: " + deviceId, Toast.LENGTH_LONG).show();

        tempButton.setOnClickListener(v -> {
            // Show the device ID as a Toast message
            Toast.makeText(MainActivity.this, "Device ID: " + deviceId, Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.qrActivity);
        });

        System.out.println("onCreate done");
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

    @SuppressLint("HardwareIds")
    private String getOrCreateDeviceId() {
        // Access SharedPreferences to check if a device ID is already stored
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String savedDeviceId = sharedPreferences.getString("device_id", null);

        // If the device ID is not already stored, create and save it
        if (savedDeviceId == null) {
            savedDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            sharedPreferences.edit().putString("device_id", savedDeviceId).apply();
        }

        return savedDeviceId;
    }
}
