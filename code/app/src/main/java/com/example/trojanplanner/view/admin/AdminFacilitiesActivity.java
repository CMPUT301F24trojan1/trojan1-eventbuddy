package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Facility;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminFacilitiesActivity extends AppCompatActivity {
    private TextView empty_text;
    private RecyclerView recyclerView;
    private AdminFacilitiesArrayAdapter adapter;
    private List<Facility> facilities;
    private Button previousButton, nextButton;
    private int currentPage = 1;
    private final int pageSize = 4; // Number of facilities per page
    private long totalDocuments = 0; // Total number of documents in Firestore
    private String lastFetchedFacilityDocument = null; // Track the last document for pagination


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_facilities_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AdminFacilitiesConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.adminfacilitiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        empty_text = findViewById(R.id.empty_text);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        facilities = new ArrayList<>();
        adapter = new AdminFacilitiesArrayAdapter(this, facilities);
        recyclerView.setAdapter(adapter);

        // Fetch total documents and initialize the first page
        Database.getDB().getTotalFacilityDocumentCount(
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

    private void loadPage(int page, String lastDocumentId) {
        facilities.clear();
        adapter.notifyDataSetChanged();
        if (page == 1) {
            lastDocumentId = null; // No need for lastDocumentId when fetching the first page
        }

        Database.getDB().getFacilityDocumentIDs(page, pageSize, lastDocumentId,
                documentIDs -> {
                    if (!((List<String>)documentIDs).isEmpty()) {
                        // Save the last document ID to use for the next fetch
                        lastFetchedFacilityDocument = ((List<String>)documentIDs).get(((List<String>)documentIDs).size() - 1);
                    }
                    Log.d("AdminActivity", "Fetched document IDs: " + documentIDs); // Log document IDs
                    Log.d("AdminActivity", "Last document ID: " + lastFetchedFacilityDocument); // Log last document ID
                    for (String id : (List<String>)documentIDs) {
                        Database.getDB().getFacility(facility -> {
                            Log.d("AdminActivity", "Fetched facility: " + ((Facility)facility).getName()); // Log the facility data
                            facilities.add((Facility) facility);
                            adapter.notifyDataSetChanged();
                            toggleEmptyView();
                        }, () -> Log.e("AdminActivity", "Failed to fetch facility details"), id);
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
        if (facilities.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
        }
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
}

/*
// Simulated method to fetch facility data
private List<Facility> getFacilityList() {
    // In real use, this could be fetching from a database or API
    List<Facility> facilities = new ArrayList<>();

    // Example data, you should replace this with real data
    facilities.add(new Facility("Gym", "F1", "Building A", new Organizer("John Doe"), "path/to/pic1.jpg", null));
    facilities.add(new Facility("Library", "F2", "Building B", new Organizer("Jane Doe"), "path/to/pic2.jpg", null));

    return facilities;
}
*\
 */