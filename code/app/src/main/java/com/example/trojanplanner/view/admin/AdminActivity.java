package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.view.MainActivity;
import com.example.trojanplanner.view.ProfileActivity;
import com.example.trojanplanner.view.QRActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "com.example.trojanplanner.PREFS";
    private static final String KEY_DIALOG_SHOWN = "admin_dialog_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AdminEventsConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Show AlertDialog only if it's the user's first time here
        if (isFirstTimeSwitchingToAdminView()) {
            showAdminIntroDialog();
        }

        setupNavigation();
    }

    private boolean isFirstTimeSwitchingToAdminView() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return !prefs.getBoolean(KEY_DIALOG_SHOWN, false);
    }

    private void showAdminIntroDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Welcome to Admin View")
                .setMessage("You've swapped to the admin view. Here, you can manage events and other administrative tasks. The switch button on the home page will take you back to the user view anytime you want.")
                .setPositiveButton("Got it!", (dialog, which) -> {
                    // Mark the dialog as shown in SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    prefs.edit().putBoolean(KEY_DIALOG_SHOWN, true).apply();
                })
                .setCancelable(false)
                .show();
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.admin_bottom_nav_menu);
        navView.setSelectedItemId(R.id.navigation_home);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_facilities) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminActivity.this, AdminFacilitiesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_images) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminActivity.this, AdminImagesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_users) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminActivity.this, AdminUsersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.navigation_home; // Stay in the same activity
        });
    }

}