package com.example.trojanplanner.view.admin;

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
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.admin.AdminImagesArrayAdapter;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminImagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminImagesArrayAdapter adapter;
    private TextView empty_text;
    private List<Bitmap> imagesList;
    private final List<String> imagefilepath = new ArrayList<>();
    private List<String> directoriesList = new ArrayList<>(); // List to hold directories
    private int currentDirectoryIndex = 0; // To track the current directory index
    private Button nextButton, previousButton;
    private TextView directoryID;
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
        imagesList = new ArrayList<>();
        adapter = new AdminImagesArrayAdapter(AdminImagesActivity.this, imagesList, image -> {
            new AlertDialog.Builder(AdminImagesActivity.this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this Image?\n\n" +
                            "This will potentially affect:\n" +
                            "1. Events that this user is attending.\n" +
                            "2. Events created by this user if they are the organizer.\n\n" +
                            "This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteImageFromDatabase(imagefilepath.get(image));
                        Toast.makeText(AdminImagesActivity.this, "Image deleted: " + image, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });

        recyclerView.setAdapter(adapter);

        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        directoryID = findViewById(R.id.directory);
        nextButton.setOnClickListener( v -> onNextClicked(v));
        previousButton.setOnClickListener( v -> onPreviousClicked(v));

        listAllDirectories(); // Initially list all directories
        setupNavigation();
        updateDirectoryNavigation(); // Update navigation buttons' state
    }

    private void deleteImageFromDatabase(String filePath) {
        Database.getDB().deleteImage(filePath);
    }

    private void listAllDirectories() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();

        rootRef.listAll()
                .addOnSuccessListener(listResult -> {
                    directoriesList.clear(); // Clear any previous directories
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        directoriesList.add(prefix.getPath()); // Add directory paths to the list
                    }
                    if (!directoriesList.isEmpty()) {
                        listImagesInDirectory(directoriesList.get(currentDirectoryIndex));
                        updateDirectoryNavigation();
                    } else {
                        toggleEmptyView();
                    }
                })
                .addOnFailureListener(e -> Log.e("listDirectories", "Error listing directories", e));
    }

    private void listImagesInDirectory(String directory) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference directoryRef = storageRef.child(directory);

        directoryRef.listAll()
                .addOnSuccessListener(listResult -> {
                    imagesList.clear(); // Clear previous images
                    imagefilepath.clear(); // Clear image paths
                    for (StorageReference item : listResult.getItems()) {
                        String filePath = item.getPath(); // Image path
                        downloadImageForDisplay(filePath); // Download and display images
                    }
                })
                .addOnFailureListener(e -> Log.e("listImages", "Error listing images", e));
    }

    private void downloadImageForDisplay(String filePath) {
        Database.getDB().downloadImage(picture -> {
            Bitmap bitmap = (Bitmap) picture;
            imagesList.add(bitmap);
            imagefilepath.add(filePath);
            adapter.notifyItemInserted(imagesList.size() - 1);
            toggleEmptyView();
        }, () -> Log.e("ImageDownload", "Error downloading image"),  filePath);
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

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.admin_bottom_nav_menu);
        navView.setSelectedItemId(R.id.navigation_images);

        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                Intent intent = new Intent(AdminImagesActivity.this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_facilities) {
                Intent intent = new Intent(AdminImagesActivity.this, AdminFacilitiesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_users) {
                Intent intent = new Intent(AdminImagesActivity.this, AdminUsersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_qr) {
                Intent intent = new Intent(AdminImagesActivity.this, AdminQRActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            } else return item.getItemId() == R.id.navigation_images;
        });
    }

    // Update the `directory` navigation buttons' state
    private void updateDirectoryNavigation() {
        // Enable/Disable buttons based on the current position in directoriesList
        previousButton.setEnabled(currentDirectoryIndex > 0);
        nextButton.setEnabled(currentDirectoryIndex < directoriesList.size() - 1);

        // Update the directory text view to show the current directory
        if (directoriesList.size() > 0) {
            String currentDirectoryPath = directoriesList.get(currentDirectoryIndex);
            Database.getDB().getEntrant(
                    v->{
                        directoryID.setText("Viewing User: " + ((User)v).getFirstName());
                    },
                    () ->{
                        directoryID.setText("Viewing Deleted User's Images:" + currentDirectoryPath.replace("/", ""));
                    },
                    currentDirectoryPath.replace("/",""));
        } else {
            directoryID.setText("No directories available");
        }
    }


    public void onNextClicked(View view) {
        Log.d("DirectoryListDebug", "directoriesList: " + directoriesList.toString());
        Log.d("DirectoryListDebug", "Current directory index: " + currentDirectoryIndex);

        if (currentDirectoryIndex < directoriesList.size() - 1) {
            currentDirectoryIndex++;

            Log.d("DirectoryListDebug", "Next directory index: " + currentDirectoryIndex);
            Log.d("DirectoryListDebug", "Directory to load: " + directoriesList.get(currentDirectoryIndex));

            // Clear previous images from the list and notify the adapter
            imagesList.clear(); // Clears the previous list of images
            imagefilepath.clear(); // Clear file paths
            adapter.notifyDataSetChanged(); // Notify adapter that data is cleared

            // List images for the next directory
            listImagesInDirectory(directoriesList.get(currentDirectoryIndex));

            // Update directory navigation buttons
            updateDirectoryNavigation();
        } else {
            Log.d("DirectoryListDebug", "No more directories to navigate to.");
        }
    }

    public void onPreviousClicked(View view) {
        if (currentDirectoryIndex > 0) {
            currentDirectoryIndex--;

            // Clear previous images from the list and notify the adapter
            imagesList.clear(); // Clears the previous list of images
            imagefilepath.clear(); // Clear file paths
            adapter.notifyDataSetChanged(); // Notify adapter that data is cleared

            // List images for the previous directory
            listImagesInDirectory(directoriesList.get(currentDirectoryIndex));

            // Update directory navigation buttons
            updateDirectoryNavigation();
        }
    }
}
