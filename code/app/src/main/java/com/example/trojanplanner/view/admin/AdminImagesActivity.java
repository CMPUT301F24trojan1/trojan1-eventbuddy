package com.example.trojanplanner.view.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.example.trojanplanner.controller.BitmapGenerator;
import com.example.trojanplanner.controller.admin.AdminImagesArrayAdapter;
import com.example.trojanplanner.model.Admin;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminImagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminImagesArrayAdapter imagesAdapter;
    private List<Bitmap> imagesList; // List to hold images (Bitmaps)
    private TextView emptyText;

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
        emptyText = findViewById(R.id.empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch images or initialize the list (Here, we simulate a list of images)
        imagesList = getImages(); // Replace this with actual image-fetching logic

        if (imagesList.isEmpty()) {
            emptyText.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            emptyText.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);

            imagesAdapter = new AdminImagesArrayAdapter(AdminImagesActivity.this, imagesList);
            recyclerView.setAdapter(imagesAdapter);
        }

        setupNavigation();
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
            } else return item.getItemId() == R.id.navigation_images; // Stay in the same activity
        });
    }

    private List<Bitmap> getImages() {
        List<Bitmap> images = new ArrayList<>();

        images.add(getSampleBitmap("trash"));
        images.add(getSampleBitmap("test"));

        return images;
    }

    private Bitmap getSampleBitmap(String text) {
        // Define the width and height for the Bitmap
        int width = 200;  // Example width
        int height = 100; // Example height

        // Generate the Bitmap with a shape collage based on the input string
        return BitmapGenerator.generateShapeCollage(text, width, height);
    }

}