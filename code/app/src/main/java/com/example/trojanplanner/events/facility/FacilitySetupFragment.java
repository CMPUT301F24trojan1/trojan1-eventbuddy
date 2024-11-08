package com.example.trojanplanner.events.facility;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.view.MainActivity;

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
    private EditText facilityName;
    private EditText ownerName;
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
        facilityName = view.findViewById(R.id.facility_name);
        ownerName = view.findViewById(R.id.owner_name);
        Button uploadPhotoButton = view.findViewById(R.id.upload_photo_button);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        uploadPhotoButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveFacility());
        cancelButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
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
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
        }
    }

    /**
     * Opens the photo picker to allow the user to select a photo for the facility.
     */
    private void openImagePicker() {
        mainActivity.photoPicker.openPhotoPicker(mainActivity.currentUser);
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
        String name = facilityName.getText().toString().trim();
        String ownerNameText = ownerName.getText().toString().trim();

        if (name.isEmpty() || ownerNameText.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = null;

        // If no photo is selected, use a default image from resources
        if (facilityPhotoUri == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            String defaultUriString = "default_image_uri"; // Default image URI for placeholder
            Facility facility = new Facility(name, "generatedFacilityId", ownerNameText, null, defaultUriString, bitmap);

            // Insert the facility into the database
            Database db = new Database();
            db.insertFacility(facility);

            Toast.makeText(getActivity(), "Facility saved", Toast.LENGTH_SHORT).show();
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.facilitySetupFragment);  // Navigate after saving
        } else {
            // If the photo was selected, proceed as usual
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), facilityPhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to save photo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the Facility object with the selected photo URI
            Facility facility = new Facility(name, "generatedFacilityId", ownerNameText, null, facilityPhotoUri.toString(), bitmap);

            // Insert the facility into the database
            Database db = new Database();
            db.insertFacility(facility);

            Toast.makeText(getActivity(), "Facility saved", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();  // Navigate back after saving
        }
    }
}
