package com.example.trojanplanner.view.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.trojanplanner.controller.admin.AdminImagesArrayAdapter;
import com.example.trojanplanner.model.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminImagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminImagesArrayAdapter adapter;
    private TextView empty_text;
    private List<Bitmap> imagesList; // List to hold images (Bitmaps)
    private Button previousButton, nextButton;
    private int currentPage = 1;
    private final int pageSize = 4; // Number of events per page
    private long totalDocuments = 0; // Total number of documents in Firestore
    private String lastFetchedImageDocument = null; // Track the last document for pagination
    private final List<String> imagefilepath =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_images_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AdminImagesConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.adminimagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        empty_text = findViewById(R.id.empty_text);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        imagesList = new ArrayList<>();
        adapter = new AdminImagesArrayAdapter(AdminImagesActivity.this, imagesList, image -> {
            // Create an AlertDialog with a warning about the user's deletion
            new AlertDialog.Builder(AdminImagesActivity.this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this Image?\n\n" +
                            "This will potentially affect:\n" +
                            "1. Events that this user is attending.\n" +
                            "2. Events created by this user if they are the organizer.\n\n" +
                            "This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // If the user clicks "Yes", proceed with deletion
                        deleteImageFromDatabase(imagefilepath.get(image));  // Method to delete the user and their associated data
                        Toast.makeText(AdminImagesActivity.this, "Image deleted: " + image, Toast.LENGTH_SHORT).show();
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
                loadPage(currentPage, lastFetchedImageDocument);
                updateButtonStates();
            }
        });
        nextButton.setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            if (currentPage < totalPages) {
                currentPage++;
                loadPage(currentPage, lastFetchedImageDocument);
                updateButtonStates();
            }
        });
    }

    private void deleteImageFromDatabase(String imageFilePath) {
        Database.getDB().deleteImage(imageFilePath);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPage(int page, String lastDocumentId) {
        imagesList.clear();
        adapter.notifyDataSetChanged();
        listAllDirectories();
        updateButtonStates();
    }


    private void listAllDirectories() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();

        rootRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        // Each prefix corresponds to a directory like '0e754d12425d8b84/'
                        String directory = prefix.getPath();  // Path of the dynamic directory
                        Log.d("Directory:", directory);

                        // Now that we have the directory, list the images inside it
                        listImagesInDirectory(directory);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("listDirectories", "Error listing directories", e);
                });
    }

    private void listImagesInDirectory(String directory) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Reference to the dynamic directory
        StorageReference directoryRef = storageRef.child(directory);

        // List all items (files) in the directory
        directoryRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        // Each item is a file in the directory
                        String filePath = item.getPath();  // Path of the image file
                        Log.d("Image File:", filePath);

                        // Now you can download the image
                        downloadImageForDisplay(filePath);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("listImages", "Error listing images in directory", e);
                });
    }


    private void updateButtonStates() {
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        // Enable/Disable buttons based on the current page
        previousButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);

        // Change the text of the Previous button when on the last page
        if (currentPage == totalPages) {
            previousButton.setText("TO: Start");
            lastFetchedImageDocument = null;
        } else {
            previousButton.setText("Previous");
        }
    }
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.admin_bottom_nav_menu);
        navView.setSelectedItemId(R.id.navigation_images);

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminImagesActivity.this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_facilities) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminImagesActivity.this, AdminFacilitiesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_users) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminImagesActivity.this, AdminUsersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_qr) {
                if (App.currentUser != null) {
                    Intent intent = new Intent(AdminImagesActivity.this, AdminQRActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else return item.getItemId() == R.id.navigation_images; // Stay in the same activity
        });
    }
    private void toggleEmptyView() {
        if (imagesList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
        }
    }
    private void downloadImageForDisplay(String filePath) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(filePath);
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imagesList.add(bitmap);
            imagefilepath.add(filePath);
            adapter.notifyItemInserted(imagesList.size() - 1);
            toggleEmptyView();
        }).addOnFailureListener(e -> Log.e("ImageDownload", "Error downloading image", e));
    }
}