package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.controller.admin.AdminUsersArrayAdapter;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminUsersActivity extends AppCompatActivity {
    private TextView empty_text;
    private RecyclerView recyclerView;
    private AdminUsersArrayAdapter adapter;
    private List<User> users;
    private Button previousButton, nextButton;
    private int currentPage = 1;
    private final int pageSize = 4; // Number of users per page
    private long totalDocuments = 0; // Total number of documents in Firestore
    private String lastFetchedFacilityDocument = null; // Track the last document for pagination

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

        empty_text = findViewById(R.id.empty_text);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);


        users = new ArrayList<>();
        adapter = new AdminUsersArrayAdapter(AdminUsersActivity.this, users, user -> {
            // Create an AlertDialog with a warning about the user's deletion
            new AlertDialog.Builder(AdminUsersActivity.this)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?\n\n" +
                            "This will potentially affect:\n" +
                            "1. Events that this user is attending.\n" +
                            "2. The facility this user owns if they own one.\n" +
                            "3. Events created by this user if they are the organizer.\n\n" +
                            "This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // If the user clicks "Yes", proceed with deletion
                        deleteUserFromDatabase(user);  // Method to delete the user and their associated data
                        Toast.makeText(AdminUsersActivity.this, "User deleted: " + user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // If the user clicks "No", just dismiss the dialog
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });

        recyclerView.setAdapter(adapter);

        // Fetch total documents and initialize the first page
        Database.getDB().getTotalUserDocumentCount(
                totalDocs -> {
                    totalDocuments = totalDocs;
                    loadPage(currentPage, null); // Start with the first page
                    updateButtonStates();
                },
                e -> Log.e("Database", "Failed to count documents", e)
        );

        setupNavigation();

        previousButton.setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            if (currentPage == totalPages){
                currentPage = 1;
                loadPage(currentPage, null);
                updateButtonStates();
                return;
            }
            if (currentPage > 1) {
                // If not on the first page, decrement the current page
                currentPage--;
                loadPage(currentPage, lastFetchedFacilityDocument);
                updateButtonStates();
            }
        });
        nextButton.setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            if (currentPage < totalPages) {
                currentPage++;
                loadPage(currentPage, lastFetchedFacilityDocument);
                updateButtonStates();
            }
        });
    }

    private void deleteUserFromDatabase(User user) {
        // Use ExecutorService to manage background threads
        ExecutorService executorService = Executors.newSingleThreadExecutor();  // Single thread executor for serial task execution

        // Execute the database deletion on the background thread
        executorService.execute(() -> {
            try {
                // Attempt to delete the user from the database using the user's deviceId
                Database.getDB().deleteUser(user.getDeviceId());
                // Optionally, post success to the main thread if you need to update the UI
                runOnUiThread(() -> {
                    Toast.makeText(AdminUsersActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    recreate(); // VERY VERY IMPORTANT, recreates the activity to refresh the list
                });
            } catch (Exception e) {
                // Handle any errors that occur during deletion
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(AdminUsersActivity.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                // Shut down the executor service
                executorService.shutdown();
            }
        });
    }

    private void loadPage(int page, String lastDocumentId) {
        users.clear();
        adapter.notifyDataSetChanged();
        if (page == 1) {
            lastDocumentId = null; // No need for lastDocumentId when fetching the first page
        }

        Database.getDB().getUserDocumentIDs(page, pageSize, lastDocumentId,
                documentIDs -> {
                    if (!((List<String>)documentIDs).isEmpty()) {
                        // Save the last document ID to use for the next fetch
                        lastFetchedFacilityDocument = ((List<String>)documentIDs).get(((List<String>)documentIDs).size() - 1);
                    }
                    Log.d("AdminActivity", "Fetched document IDs: " + documentIDs); // Log document IDs
                    Log.d("AdminActivity", "Last document ID: " + lastFetchedFacilityDocument); // Log last document ID
                    for (String id : (List<String>)documentIDs) {
                        Database.getDB().getEntrant(user -> {
                            Log.d("AdminActivity", "Fetched user: " + ((User)user).getFirstName()); // Log the facility data
                            users.add((User) user);
                            adapter.notifyDataSetChanged();
                            toggleEmptyView();
                        }, () -> Log.e("AdminActivity", "Failed to fetch user details"), id);
                    }
                },
                () -> Log.e("AdminActivity", "Failed to fetch document IDs")
        );
    }

    private void updateButtonStates() {
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        // Enable/Disable buttons based on the current page
        previousButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);

        // Change the text of the Previous button when on the last page
        if (currentPage == totalPages) {
            previousButton.setText("TO: Start");
            lastFetchedFacilityDocument = null;
        } else {
            previousButton.setText("Previous");
        }
    }

    private void toggleEmptyView() {
        if (users.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
        }
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
            } else if (item.getItemId() == R.id.navigation_qr) {
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
}