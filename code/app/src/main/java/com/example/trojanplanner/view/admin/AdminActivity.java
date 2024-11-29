package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.admin.AdminEventArrayAdapter;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.trojanplanner.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "com.example.trojanplanner.PREFS";
    private static final String KEY_DIALOG_SHOWN = "admin_dialog_shown";
    private TextView empty_text;
    private RecyclerView recyclerView;
    private AdminEventArrayAdapter adapter;
    private List<Event> events;
    private Button previousButton, nextButton, switchAdmin;
    private int currentPage = 1;
    private final int pageSize = 4; // Number of events per page
    private long totalDocuments = 0; // Total number of documents in Firestore
    private String lastFetchedEventDocument = null; // Track the last document for pagination

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AdminEventsConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (isFirstTimeSwitchingToAdminView()) {
            showAdminIntroDialog();
        }

        recyclerView = findViewById(R.id.admineventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        empty_text = findViewById(R.id.empty_text);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        events = new ArrayList<>();
        adapter = new AdminEventArrayAdapter(this, events, event -> {
            // Create an AlertDialog with a warning about the event deletion
            new AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event? \nEvent Name:"
                            + event.getName() + "?\n\n" +
                            "This will potentially affect:\n" +
                            "1. The creator \n" +
                            "2. Waiting and selected users\n" +
                            "3. Delete any relvant reference to the event in the database.\n\n" +
                            "This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // If the user clicks "Yes", delete the event from the database
                        deleteEventFromDatabase(event);  // Add your database deletion logic here
                        Toast.makeText(this, "Event deleted: " + event.getName(), Toast.LENGTH_SHORT).show();
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
        Database.getDB().getTotalEventDocumentCount(
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
                loadPage(currentPage, lastFetchedEventDocument);
                updateButtonStates();
            }
        });
        nextButton.setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            if (currentPage < totalPages) {
                currentPage++;
                loadPage(currentPage, lastFetchedEventDocument);
                updateButtonStates();
            }
        });

        switchAdmin = findViewById(R.id.switchAdmin);
        switchAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void deleteEventFromDatabase(Event event) {
        // Create an Executor with a fixed thread pool (you can adjust the number of threads)
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Submit the task to delete the event on the background thread
        executor.submit(() -> {
            // Perform the database operation here (delete the event)
            Database.getDB().deleteEvent(event.getEventId());

            App.sendAnnouncement(App.currentUser.getDeviceId(), "Admin", "Successfully deleted event: " + event.getName() + ".");

            // Check and send announcements for waiting list
            if (event.getWaitingList() != null) {
                for (User user : event.getWaitingList()) {
                    if (user != null && user.getDeviceId() != null) {
                        App.sendAnnouncement("admin" + user.getDeviceId(), "Admin", "Event " + event.getName() + " has been cancelled");
                    }
                }
            }

            // Check and send announcements for pending list
            if (event.getPendingList() != null) {
                for (User user : event.getPendingList()) {
                    if (user != null && user.getDeviceId() != null) {
                        App.sendAnnouncement("admin" + user.getDeviceId(), "Admin", "Event " + event.getName() + " has been cancelled");
                    }
                }
            }

            // Check and send announcements for cancelled list
            if (event.getCancelledList() != null) {
                for (User user : event.getCancelledList()) {
                    if (user != null && user.getDeviceId() != null) {
                        App.sendAnnouncement("admin" + user.getDeviceId(), "Admin", "Event " + event.getName() + " has been cancelled");
                    }
                }
            }

            // Check and send announcements for Enrolled list
            if (event.getEnrolledList() != null) {
                for (User user : event.getEnrolledList()) {
                    if (user != null && user.getDeviceId() != null) {
                        App.sendAnnouncement("admin" + user.getDeviceId(), "Admin", "Event " + event.getName() + " has been cancelled");
                    }
                }
            }

            runOnUiThread(() -> {
            });
        });

        // Optionally, shut down the executor when done (to avoid memory leaks)
        executor.shutdown();
    }

    private boolean isFirstTimeSwitchingToAdminView() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return !prefs.getBoolean(KEY_DIALOG_SHOWN, false);
    }

    private void toggleEmptyView() {
        if (events.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
        }
    }

    private void showAdminIntroDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Welcome to Admin View")
                .setMessage("You're in admin mode. Here, you can manage events and other administrative tasks. The switch button on the home page will take you back to the user view anytime you want.")
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
            } else if (item.getItemId() == R.id.navigation_qr) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminActivity.this, AdminQRActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.navigation_home; // Stay in the same activity
        });
    }

    private void loadPage(int page, String lastDocumentId) {
        events.clear();
        adapter.notifyDataSetChanged();
        if (page == 1) {
            lastDocumentId = null; // No need for lastDocumentId when fetching the first page
        }

        Database.getDB().getEventDocumentIDs(page, pageSize, lastDocumentId,
                documentIDs -> {
                    if (!((List<String>)documentIDs).isEmpty()) {
                        // Save the last document ID to use for the next fetch
                        lastFetchedEventDocument = ((List<String>)documentIDs).get(((List<String>)documentIDs).size() - 1);
                    }
                    Log.d("AdminActivity", "Fetched document IDs: " + documentIDs); // Log document IDs
                    Log.d("AdminActivity", "Last document ID: " + lastFetchedEventDocument); // Log last document ID
                    for (String id : (List<String>)documentIDs) {
                        Database.getDB().getEvent(event -> {
                            Log.d("AdminActivity", "Fetched event: " + ((Event)event).getName()); // Log the event data
                            events.add((Event) event);
                            adapter.notifyDataSetChanged();
                            toggleEmptyView();
                        }, () -> Log.e("AdminActivity", "Failed to fetch event details"), id);
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
            lastFetchedEventDocument = null;
        } else {
            previousButton.setText("Previous");
        }
    }
}

    /*
    private List<Event> getEventList() {
        List<Event> events = new ArrayList<>();
        events.add(new Event("1", "Event 1", "Description 1", 100.0f));
        events.add(new Event("2", "Event 2", "Description 2", 150.0f));
        return events;
    }
*/