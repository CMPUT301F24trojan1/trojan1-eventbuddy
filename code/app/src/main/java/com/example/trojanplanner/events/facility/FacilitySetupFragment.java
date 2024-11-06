package com.example.trojanplanner.events.facility;

import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.navigation.Navigation;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Facility;

import java.io.IOException;

public class FacilitySetupFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView facilityPhoto;
    private EditText facilityName;
    private EditText ownerName;
    private Uri facilityPhotoUri;

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide the action bar for full-screen effect
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Restore the action bar visibility when leaving this fragment
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
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

        if (name.isEmpty() || ownerNameText.isEmpty() || facilityPhotoUri == null) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), facilityPhotoUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to save photo", Toast.LENGTH_SHORT).show();
            return;
        }

        Facility facility = new Facility(name, "generatedFacilityId", ownerNameText, null, facilityPhotoUri.toString(), bitmap);
        Database db = new Database();
        db.insertFacility(facility);

        Toast.makeText(getActivity(), "Facility saved", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigateUp();
    }
}
