package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.controller.admin.AdminUsersArrayAdapter;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {
    private TextView emptyText;
    private List<User> usersList; // List to hold users, you will likely get this from a database or API
    private RecyclerView recyclerView;
    private AdminUsersArrayAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AdminUsersConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.adminusersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch users or initialize the list (Here, we simulate an empty list)
        usersList = getUsers();  // Replace this with your actual user-fetching logic
        emptyText = findViewById(R.id.empty_text);

        if (usersList.isEmpty()) {
            emptyText.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            emptyText.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            usersAdapter = new AdminUsersArrayAdapter(AdminUsersActivity.this, usersList);
            recyclerView.setAdapter(usersAdapter);
        }

        setupNavigation();
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.admin_bottom_nav_menu);
        navView.setSelectedItemId(R.id.navigation_users);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminUsersActivity.this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_facilities) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminUsersActivity.this, AdminFacilitiesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_images) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminUsersActivity.this, AdminImagesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.navigation_users; // Stay in the same activity
        });
    }

    private List<User> getUsers() {
        // Create a list to hold User objects
        List<User> users = new ArrayList<>();

        // Add concrete subclasses of User (not User directly, since it's abstract)
        users.add(new AdminUser("device123", "johndoe@example.com", "John", true, false, "Doe", "/path/to/pfp1.jpg", "123-456-7890"));
        users.add(new AdminUser("device124", "janedoe@example.com", "Jane", false, true, "Doe", "/path/to/pfp2.jpg", "098-765-4321"));
        users.add(new AdminUser("device125", "samsmith@example.com", "Sam", true, true, "Smith", "/path/to/pfp3.jpg", "555-123-4567"));

        return users;
    }


    public class AdminUser extends User {

        // Constructor
        public AdminUser(String deviceId, String email, String firstName, boolean isAdmin,
                         boolean isOrganizer, String lastName, String pfpFilePath, String phoneNumber) {
            super(deviceId, email, firstName, isAdmin, isOrganizer, lastName, pfpFilePath, phoneNumber);
        }

        // Implementing the abstract method
        @Override
        public String getRole() {
            return "Admin";
        }
    }

}