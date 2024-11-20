package com.example.trojanplanner.events.facility;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.MainActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * A fragment that handles the setup of a new facility. It allows the user to input the
 * facility's name, owner's name, and upload a photo, and then saves the facility details
 * in the database.
 */
public class FacilitySetupFragment extends Fragment {
    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView facilityPhoto;
    private EditText facilityNameEditText;
    private EditText facilityLocationEditText;
    private Uri facilityPhotoUri;
    private MainActivity mainActivity;

    /**
     * Inflates the layout for this fragment and sets up the user interface components.
     *
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The container view to attach the fragment to.
     * @param savedInstanceState The saved instance state for the fragment, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_setup, container, false);

        facilityPhoto = view.findViewById(R.id.facility_photo);
        facilityNameEditText = view.findViewById(R.id.facility_name);
        facilityLocationEditText = view.findViewById(R.id.location);
        Button uploadPhotoButton = view.findViewById(R.id.upload_photo_button);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        uploadPhotoButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveFacility());

        cancelButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.emptyEventsFragment);
        });

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();

            // Override default photopicker callback function
            mainActivity.facilityPhotoPicker.dummyCallback = new PhotoPicker.PhotoPickerCallback() {
                @Override
                public void OnPhotoPickerFinish(Bitmap bitmap) {
                    facilityPhoto.setImageBitmap(bitmap);
                }
            };

        }

        return view;
    }

    /**
     * Hides the action bar when the fragment is resumed to provide a full-screen experience.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
        }
    }

    /**
     * Restores the action bar visibility when the fragment is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        // Restore the action bar visibility when leaving this fragment
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
        }
    }

    /**
     * Opens the photo picker to allow the user to select a photo for the facility.
     */
    private void openImagePicker() {
        mainActivity.facilityPhotoPicker.openPhotoPicker(App.currentUser);
    }

    /**
     * Handles the result from the photo picker activity and sets the selected photo URI.
     *
     * @param requestCode The request code passed in startActivityForResult().
     * @param resultCode The result code returned by the photo picker activity.
     * @param data The intent containing the result data, including the selected photo URI.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            facilityPhotoUri = data.getData();
            facilityPhoto.setImageURI(facilityPhotoUri);
        }
    }

    /**
     * Saves the facility data to the database. If no photo is selected, a default image is used.
     * Displays a toast message indicating whether the facility was saved successfully or not.
     */
    private void saveFacility() {
        String name = facilityNameEditText.getText().toString().trim();
        String location = facilityLocationEditText.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap;
        Entrant currentUser = (Entrant) App.currentUser;
        Organizer currentOrganizer = currentUser.returnOrganizer();

        // If no photo is selected, use a default image from resources
        if (facilityPhotoUri == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            String defaultUriString = "default_image_uri"; // Default placeholder image URI

            String newFacilityId = currentOrganizer.getDeviceId() + "-" + System.currentTimeMillis();
            Facility facility = new Facility(
                    name,
                    newFacilityId,
                    location,
                    currentOrganizer,
                    defaultUriString,  // Save URI string for default image
                    bitmap);  // Store the bitmap itself (for the case of default image)

            currentOrganizer.setFacility(facility);
            App.currentUser.setIsOrganizer(true);

            // Insert the facility into the database
            Database db = Database.getDB();
            db.insertFacility(facility);
            db.insertUserDocument(currentOrganizer);

            Toast.makeText(getActivity(), "Facility saved", Toast.LENGTH_SHORT).show();

            // Navigate after saving with a delay
            new android.os.Handler().postDelayed(() -> {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.facilitySetupFragment);
            }, 2000);  // Delay in milliseconds
        } else {
            // If a photo was selected, get the bitmap from the URI
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), facilityPhotoUri);

                // If the bitmap is using HARDWARE config, copy it to ARGB_8888 format
                if (bitmap.getConfig() == Bitmap.Config.HARDWARE) {
                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);  // Convert to ARGB_8888 format
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to save photo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Upload the image to Firebase Storage and get the URI
            // Call the `uploadImage` method that you already have
            Database.getDB().uploadImage(bitmap, currentUser); // Upload image and get URI asynchronously

            // After uploading, use the returned URI (firebase storage path) to save it in the database
            String imageUriString = currentUser.getPfpFilePath(); // Assuming the URI is saved in the pfpFilePath

            String newFacilityId = currentOrganizer.getDeviceId() + "-" + System.currentTimeMillis();

            // Now use the URI (string) instead of directly storing the Bitmap in Firestore
            Facility facility = new Facility(
                    name,
                    newFacilityId,
                    location,
                    currentOrganizer,
                    imageUriString,  // Use the URI string after upload
                    bitmap);  // Optionally, store the bitmap if you need it locally

            currentOrganizer.setFacility(facility);
            App.currentUser.setIsOrganizer(true);

            // Insert the facility into the database
            Database db = Database.getDB();
            db.insertFacility(facility);
            db.insertUserDocument(currentOrganizer);

            Toast.makeText(getActivity(), "Facility saved", Toast.LENGTH_SHORT).show();

            // Navigate after saving
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.emptyEventsFragment);
        }
    }

}
