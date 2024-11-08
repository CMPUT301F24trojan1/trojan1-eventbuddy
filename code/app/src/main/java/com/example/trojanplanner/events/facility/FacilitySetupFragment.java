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
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.view.MainActivity;

import java.io.IOException;
import java.util.Objects;

public class FacilitySetupFragment extends Fragment {
    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView facilityPhoto;
    private EditText facilityName;
    private EditText ownerName;
    private Uri facilityPhotoUri;
    private MainActivity mainActivity;

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

    @Override
    public void onResume() {
        super.onResume();
        // Hide the action bar for full-screen effect
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Restore the action bar visibility when leaving this fragment
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
        }
    }

    private void openImagePicker() {
        mainActivity.photoPicker.openPhotoPicker(mainActivity.CurrentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            facilityPhotoUri = data.getData();
            facilityPhoto.setImageURI(facilityPhotoUri);
        }
    }

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
            // Use a default image from resources
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            // Optionally, use a default string path for the image (could be a placeholder URL)
            String defaultUriString = "default_image_uri";
            // Create the Facility with the default image URI string
            Facility facility = new Facility(name, "generatedFacilityId", ownerNameText, null, defaultUriString, bitmap);

            // Insert the facility into the database
            Database db = new Database();
            db.insertFacility(facility);

            Toast.makeText(getActivity(), "Facility saved", Toast.LENGTH_SHORT).show();
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.facilitySetupFragment);
        } else {
            // If the photo was selected, proceed as usual
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), facilityPhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to save photo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the Facility with the selected photo URI
            Facility facility = new Facility(name, "generatedFacilityId", ownerNameText, null, facilityPhotoUri.toString(), bitmap);

            // Insert the facility into the database
            Database db = new Database();
            db.insertFacility(facility);

            Toast.makeText(getActivity(), "Facility saved", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
        }
    }
}