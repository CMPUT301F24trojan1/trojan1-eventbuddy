package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.admin.AdminQRArrayAdapter;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AdminQRActivity extends AppCompatActivity {
    private TextView empty_text;
    private RecyclerView recyclerView;
    private AdminQRArrayAdapter adapter;
    private List<QRModel> qr_codes;
    private Button previousButton, nextButton, switchAdmin;
    private int currentPage = 1;
    private final int pageSize = 4; // Number of events per page
    private long totalDocuments = 0; // Total number of documents in Firestore
    private String lastfetchedQRDocument = null; // Track the last document for pagination


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_qr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AdminQRConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.adminQRRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        empty_text = findViewById(R.id.empty_text);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        qr_codes = new ArrayList<>();
        adapter = new AdminQRArrayAdapter(this, qr_codes);
        recyclerView.setAdapter(adapter);

        // Fetch total documents and initialize the first page
        Database.getDB().getTotalQRDocumentCount(
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
                loadPage(currentPage, lastfetchedQRDocument);
                updateButtonStates();
            }
        });
        nextButton.setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            if (currentPage < totalPages) {
                currentPage++;
                loadPage(currentPage, lastfetchedQRDocument);
                updateButtonStates();
            }
        });
    }

    private void toggleEmptyView() {
        if (qr_codes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
        }
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.admin_bottom_nav_menu);
        navView.setSelectedItemId(R.id.navigation_qr);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminQRActivity.this, AdminFacilitiesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_facilities) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminQRActivity.this, AdminFacilitiesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_images) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminQRActivity.this, AdminImagesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_users) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminQRActivity.this, AdminUsersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.navigation_qr; // Stay in the same activity
        });
    }

    private void loadPage(int page, String lastDocumentId) {
        qr_codes.clear();
        adapter.notifyDataSetChanged();

        if (page == 1) {
            lastDocumentId = null; // No need for lastDocumentId when fetching the first page
        }

        // Create a CountDownLatch to wait for all tasks to finish
        CountDownLatch latch = new CountDownLatch(1); // Starts with a count of 1 to wait for all tasks

        // Fetch QR document IDs
        Database.getDB().getQRDocumentIDs(page, pageSize, lastDocumentId,
                documentIDs -> {
                    if (!((List<String>)documentIDs).isEmpty()) {
                        // Save the last document ID to use for the next fetch
                        lastfetchedQRDocument = ((List<String>)documentIDs).get(((List<String>)documentIDs).size() - 1);
                    }
                    Log.d("AdminQRActivity", "Fetched document IDs: " + documentIDs); // Log document IDs
                    Log.d("AdminQRActivity", "Last document ID: " + lastfetchedQRDocument); // Log last document ID

                    // Iterate over each QR document ID and fetch event details
                    for (String qrDocId : (List<String>) documentIDs) {
                        // Each task is submitted to the latch, which will count down when done
                        latch.countDown(); // Decrement latch count as we process each document

                        // Fetch eventID for each QR document
                        Database.getDB().getEventIDForQRDoc(qrDocId, eventID -> {
                                    // Fetch event details using the eventID
                                    Database.getDB().getEvent(event -> {
                                                String eventName = ((Event) event).getName();
                                                Log.d("AdminActivity", "Fetched event: " + eventName); // Log event name

                                                // Create a new QRModel with the fetched event name and QR hash
                                                QRModel qrModel = new QRModel(qrDocId, eventName);
                                                qr_codes.add(qrModel); // Add to the list

                                                // Notify adapter of the update (run on the main UI thread)
                                                runOnUiThread(() -> {
                                                    adapter.notifyDataSetChanged();
                                                    toggleEmptyView(); // Check if the view should be toggled
                                                });
                                            },
                                            () ->
                                                {
                                                    Log.e("AdminActivity", "Failed to fetch event details");
                                                    QRModel qrModel = new QRModel(qrDocId, "Unknown (maybe deleted): " + eventID);
                                                    qr_codes.add(qrModel); // Add to the list

                                                    // Notify adapter of the update (run on the main UI thread)
                                                    runOnUiThread(() -> {
                                                        adapter.notifyDataSetChanged();
                                                        toggleEmptyView(); // Check if the view should be toggled
                                                    });
                                                },
                                            (String) eventID);
                                },
                                () -> {
                                    Log.e("AdminActivity", "Failed to fetch event ID");
                                    latch.countDown(); // Ensure latch is counted down on failure
                                });
                    }

                    // After submitting all tasks, wait for them to finish
                    try {
                        latch.await(); // This will block until latch count reaches zero
                        Log.d("AdminQRActivity", "All QR events have been processed.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Reset the interrupted flag
                        Log.e("AdminQRActivity", "Interrupted while waiting for tasks to finish.");
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
            lastfetchedQRDocument = null;
        } else {
            previousButton.setText("Previous");
        }
    }

    public static class QRModel {
        private String qrHash;
        private String eventName;

        public QRModel(String qrHash, String eventName) {
            this.qrHash = qrHash;
            this.eventName = eventName;
        }

        public String getQrHash() {
            return qrHash;
        }

        public String getEventName() {
            return eventName;
        }
    }
}