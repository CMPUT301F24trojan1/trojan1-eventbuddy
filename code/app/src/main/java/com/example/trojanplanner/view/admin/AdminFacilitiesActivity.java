package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.admin.AdminFacilitiesArrayAdapter;
import com.example.trojanplanner.model.Admin;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminFacilitiesActivity extends AppCompatActivity {
    private TextView empty_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_facilities_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AdminFacilitiesConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        RecyclerView recyclerView = findViewById(R.id.adminfacilitiesRecyclerView);
        empty_text = findViewById(R.id.empty_text);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Facility> facilities = getFacilityList();  // You will need to fetch this data
        AdminFacilitiesArrayAdapter adapter = new AdminFacilitiesArrayAdapter(this, facilities);
        recyclerView.setAdapter(adapter);

        // Handle empty state
        if (facilities.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
        }


        setupNavigation();
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.admin_bottom_nav_menu);
        navView.setSelectedItemId(R.id.navigation_facilities);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminFacilitiesActivity.this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_images) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminFacilitiesActivity.this, AdminImagesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_users) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminFacilitiesActivity.this, AdminUsersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.navigation_facilities; // Stay in the same activity
        });
    }

    // Simulated method to fetch facility data
    private List<Facility> getFacilityList() {
        // In real use, this could be fetching from a database or API
        List<Facility> facilities = new ArrayList<>();

        // Example data, you should replace this with real data
        facilities.add(new Facility("Gym", "F1", "Building A", new Organizer("John Doe"), "path/to/pic1.jpg", null));
        facilities.add(new Facility("Library", "F2", "Building B", new Organizer("Jane Doe"), "path/to/pic2.jpg", null));

        return facilities;
    }
}