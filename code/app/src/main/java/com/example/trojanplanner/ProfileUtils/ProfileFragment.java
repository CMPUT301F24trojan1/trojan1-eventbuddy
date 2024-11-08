package com.example.trojanplanner.ProfileUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.User;

public class ProfileFragment extends Fragment {
    private Database database;
    private ImageView profileImage;
    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private boolean changedPfp = false;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(User user) {
        this.user = user;
        if (database == null) {
            database = new Database();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_setup, container, false);

        // Initialize views
        profileImage = view.findViewById(R.id.facility_photo);
        firstNameInput = view.findViewById(R.id.facility_name);
        lastNameInput = view.findViewById(R.id.owner_name);
        emailInput = view.findViewById(R.id.email_input);  // Add the actual ID for email
        phoneInput = view.findViewById(R.id.phone_input);  // Add the actual ID for phone

        Button openMapButton = view.findViewById(R.id.openMapButton);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        // Set up button click listeners
        openMapButton.setOnClickListener(v -> openMapFragment());
        cancelButton.setOnClickListener(v -> handleCancel());
        saveButton.setOnClickListener(v -> handleSave());

        return view;
    }

    private void openMapFragment() {
        MapFragment mapFragment = new MapFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment); // Ensure fragment_container exists in your activity layout
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void handleCancel() {
        // Handle cancel action
        System.out.println("Cancel!");
        populateFields(user); // Reset fields to current saved values
    }

    private void handleSave() {
        // Handle save action
        System.out.println("Save!");
        // (Include input validation and database save logic)
    }

    public void populateFields(User user) {
        String firstName = "", lastName = "", email = "", phone = "";
        if (user != null) {
            firstName = user.getFirstName() != null ? user.getFirstName() : "";
            lastName = user.getLastName() != null ? user.getLastName() : "";
            email = user.getEmail() != null ? user.getEmail() : "";
            phone = user.getPhoneNumber() != null ? user.getPhoneNumber() : "";
        }
        firstNameInput.setText(firstName);
        lastNameInput.setText(lastName);
        emailInput.setText(email);
        phoneInput.setText(phone);
    }
}