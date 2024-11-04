package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
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

import com.example.trojanplanner.R;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private EditText usernameInput, emailInput, phoneInput, cityInput, passwordInput;
    private Spinner countrySpinner;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileImage = view.findViewById(R.id.profile_image);
        usernameInput = view.findViewById(R.id.username_input);
        emailInput = view.findViewById(R.id.email_input);
        phoneInput = view.findViewById(R.id.phone_input);
        cityInput = view.findViewById(R.id.city_input);
        countrySpinner = view.findViewById(R.id.country_spinner);
        passwordInput = view.findViewById(R.id.password_input);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        // Set up button click listeners
        cancelButton.setOnClickListener(v -> handleCancel());
        saveButton.setOnClickListener(v -> handleSave());

        return view;
    }

    private void handleCancel() {
        // Handle cancel action, e.g., clear fields or go back
    }

    private void handleSave() {
        // Handle save action, e.g., validate input and save to a database or API
    }
}
