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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileImage = view.findViewById(R.id.profile_image);
        firstNameInput = view.findViewById(R.id.firstname_input);
        lastNameInput = view.findViewById(R.id.lastname_input);
        emailInput = view.findViewById(R.id.email_input);
        phoneInput = view.findViewById(R.id.phone_input);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        // Set up button click listeners
        cancelButton.setOnClickListener(v -> handleCancel());
        saveButton.setOnClickListener(v -> handleSave());

        return view;
    }

    private void handleCancel() {
        // Handle cancel action, e.g., clear fields or go back
        System.out.println("Cancel!");
        System.out.println("user: " + user);
        if (user != null) {
            System.out.println("firstname: " + user.getFirstName() + ", lastname: " + user.getLastName());
        }
        populateFields(user); // for now, reset fields to current saved values
    }

    private void handleSave() {
        // Handle save action, e.g., validate input and save to a database or API
        System.out.println("Save!");

        // Input validation
        boolean errorCaught = false;
        String firstName = firstNameInput.getText().toString();
        if (firstName.isEmpty()) {
            firstNameInput.setError("First name cannot be empty.");
            errorCaught = true;
        }
        String lastName = lastNameInput.getText().toString();
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name cannot be empty.");
            errorCaught = true;
        }
        String email = emailInput.getText().toString();
        if (email.isEmpty()) {
            emailInput.setError("Email cannot be empty.");
            errorCaught = true;
        }
        String phone = phoneInput.getText().toString();

        // Do not upload if error was caught
        if (errorCaught) {
            return;
        }

        // If currentUser is not null, we will retain some of their other attributes.
        // If null, give basic entrant privileges
        boolean isOrganizer = false, isAdmin = false;
        String deviceId;
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            deviceId = user.getDeviceId();
        }
        else {
            deviceId = Settings.Secure.getString(App.activityManager.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            user = new Entrant(lastName, firstName, email, phone, deviceId, "Entrant", false, false);
        }

        if (database == null) {
            database = new Database();
        }

        // If pfp image was changed, explicitly choose a new filepath here
        // and use it for both the Firebase Storage upload and the database document insert
        if (changedPfp) {
            Bitmap bitmap = null; // TODO do it
            String newPfpFilepath = deviceId + "/" + System.currentTimeMillis() + ".png";
            user.setPfpFilePath(newPfpFilepath);
            database.uploadImage(bitmap, user, newPfpFilepath); // assume this doesn't fail??
        }

        // Upload
        database.insertUserDocument(user);
    }

    /**
     *
     * @param user
     * @author Jared Gourley
     */
    public void populateFields(User user) {
        // TODO: there's a bug here? if the user opens the app and switches to this tab before the initial query comes back, these fields don't populate (since user is still null)
        String firstName = "", lastName = "", email = "", phone = "";
        if (user != null) {
            if (user.getFirstName() != null) {
                firstName = user.getFirstName();
            }
            if (user.getLastName() != null) {
                lastName = user.getLastName();
            }
            if (user.getEmail() != null) {
                email = user.getEmail();
            }
            if (user.getPhoneNumber() != null) {
                phone = user.getPhoneNumber();
            }
        }
        firstNameInput.setText(firstName);
        lastNameInput.setText(lastName);
        emailInput.setText(email);
        phoneInput.setText(phone);
    }
    
    
}
