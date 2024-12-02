package com.example.trojanplanner.ProfileUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.events.facility.FacilitySetupFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.ProfileActivity;
import com.example.trojanplanner.view.admin.AdminActivity;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileFragment extends Fragment {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private ImageView profileImageView;
    private Bitmap profileImageBitmap = null; // Null if placeholder
    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private boolean changedPfp = false;

    private ProfileActivity profileActivity;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch notificationsSwitch;
    private Button switchProfileFacility;
    private Button switchAdminView;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity; // can get the user and photoPicker from this object
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileImageView = view.findViewById(R.id.profile_image);
        firstNameInput = view.findViewById(R.id.firstname_input);
        lastNameInput = view.findViewById(R.id.lastname_input);
        emailInput = view.findViewById(R.id.email_input);
        phoneInput = view.findViewById(R.id.phone_input);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        // Set state to either fill fields blank or with the current user's values
        resetState(App.currentUser);

        // Initialize the callback for the photopicker
        // Define and add a callback action for the photoPicker initialized in ProfileActivity
        PhotoPicker.PhotoPickerCallback photoPickerCallback = new PhotoPicker.PhotoPickerCallback() {
            @Override
            public void OnPhotoPickerFinish(Bitmap bitmap) {
                onSelectedPhoto(bitmap);
            }
        };
        profileActivity.profileActivityPhotoPicker.setCallback(photoPickerCallback);

        // Set up button click listeners
        profileImageView.setOnClickListener(v -> createPfpPopup());
        cancelButton.setOnClickListener(v -> handleCancel());
        saveButton.setOnClickListener(v -> handleSave());

        notificationsSwitch = view.findViewById(R.id.switch1);

        // Retrieve the state from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", false); // Default is false if not set

        // Set the switch state based on the saved value
        notificationsSwitch.setChecked(notificationsEnabled);

        // Set up switch toggle listener with proper toast notifications
        if (notificationsSwitch != null) {
            notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    Toast.makeText(getContext(), "Disabling notifications from Organizers and Admins...", Toast.LENGTH_SHORT).show();
                    handleNotifications(true);
                } else {
                    Toast.makeText(getContext(), "Enabling notifications...", Toast.LENGTH_SHORT).show();
                    handleNotifications(false);
                }

                // Save the state to SharedPreferences
                SharedPreferences sharedState = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedState.edit();
                editor.putBoolean("notificationsEnabled", isChecked);
                editor.apply();  // Save the state asynchronously
            });
        } else {
            // Log an error if notificationsSwitch is null
            Log.e("ProfileFragment", "notificationsSwitch is null!");
        }

        switchAdminView = view.findViewById(R.id.switch_admin_view);
        switchAdminView.setVisibility(View.GONE); // Don't show unless isAdmin is true
        if (App.currentUser != null && App.currentUser.isAdmin()) {
            switchAdminView.setVisibility(View.VISIBLE);
            switchAdminView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                profileActivity.finish();
            });
        }

        switchProfileFacility = view.findViewById(R.id.switch_profile_facility);
        // Hide the button if user is not an organizer
        if (App.currentUser == null){
            switchProfileFacility.setVisibility(View.GONE);
        } else {
            if (!App.currentUser.isOrganizer()) {
                switchProfileFacility.setVisibility(View.GONE);  // Hide the button
            } else {
                switchProfileFacility.setVisibility(View.VISIBLE);  // Show the button
                // Handle button click to navigate to FacilitySetupFragment
                switchProfileFacility.setOnClickListener(v -> {
                    // Display the FacilitySetupFragment
                    FacilitySetupFragment facilitySetupFragment = new FacilitySetupFragment();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.profile_fragment_container, facilitySetupFragment)
                            .addToBackStack("ProfileFragment") // Add to back stack for back navigation
                            .commit();
                });
            }
        }


        // Initialize the ActivityResultLauncher for requesting permissions
        requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, subscribe to notifications
                        handleNotifications(true);
                    } else {
                        // Permission denied, show a message
                        Toast.makeText(getContext(), "Notification permission denied. Cannot subscribe.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return view;
    }

    /**
     * Creates a popup to allow either changing or removing the selected profile picture.
     */
    private void createPfpPopup() {
        PfpClickPopupFragment.PfpPopupFunctions popupFunctions = new PfpClickPopupFragment.PfpPopupFunctions() {
            @Override
            public void changePFP() {
                profileActivity.profileActivityPhotoPicker.openPhotoPicker();
            }
            @Override
            public void removePFP() {
                clearPFP(App.currentUser);
            }
        };

        new PfpClickPopupFragment(popupFunctions).show(profileActivity.getSupportFragmentManager(), "Change Profile Picture");
    }

    /**
     * The action to take when the cancel button is pressed. This resets the input fields and photo
     * to the currently saved values of the user (or blank if there is no user profile yet)
     */
    private void handleCancel() {
        resetState(App.currentUser); // for now, reset fields to current saved values
    }

    /**
     * The action to take when saving the profile to the database.
     */
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
        if (App.currentUser != null) {
            App.currentUser.setFirstName(firstName);
            App.currentUser.setLastName(lastName);
            App.currentUser.setEmail(email);
            App.currentUser.setPhoneNumber(phone);
        }
        else {
            deviceId = App.deviceId;
            App.currentUser = new Entrant(lastName, firstName, email, phone, deviceId, "Entrant", false, false);
            addtoNotifications(deviceId);
            addtoNotifications("organizer" + deviceId);
            addtoNotifications("admin" + deviceId);
        }

        Database database = Database.getDB();

        // If pfp image was changed, explicitly choose a new filepath here
        // and use it for both the Firebase Storage upload and the database document insert
        if (changedPfp) {
            if (profileImageBitmap != null) {
                String newPfpFilepath = App.deviceId + "/" + System.currentTimeMillis() + ".png";
                App.currentUser.setPfpFilePath(newPfpFilepath);
                App.currentUser.setPfpBitmap(profileImageBitmap);
                database.uploadImage(profileImageBitmap, App.currentUser, newPfpFilepath); // assume this doesn't fail??
            }
            else {
                database.deleteImage(App.currentUser.getPfpFilePath()); // Delete the image since it's no longer being used
                App.currentUser.setPfpFilePath(null);
                App.currentUser.setPfpBitmap(null);
            }
        }

        // Upload
        database.insertUserDocument(App.currentUser);
    }

    private void handleNotifications(boolean isSubscribed) {
        if (App.currentUser == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }

            Toast.makeText(getContext(), "You must be logged in to make any changes to notifications.", Toast.LENGTH_SHORT).show();
        }
        if (!isSubscribed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }

            FirebaseMessaging.getInstance().subscribeToTopic("organizer" + App.currentUser.getDeviceId())
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ? "Successfully subscribed to Organizer notifications." : "Subscription failed. Please try again.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        App.sendAnnouncement(App.currentUser.getDeviceId(), "Trojan Planner", "You have been subscribed to notifications!");
                    });
            FirebaseMessaging.getInstance().subscribeToTopic("admin" + App.currentUser.getDeviceId())
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ? "Successfully subscribed to Admin notifications." : "Subscription failed. Please try again.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("organizer" + App.currentUser.getDeviceId())
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ? "Successfully unsubscribed from organizer notifications." : "Unsubscription failed. Please try again.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
            FirebaseMessaging.getInstance().unsubscribeFromTopic("admin" + App.currentUser.getDeviceId())
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ? "Successfully unsubscribed from admin notifications." : "Unsubscription failed. Please try again.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        }
    }


    /**
     * Resets profile picture to the default profile picture.
     * @param user The user (or null) interacting with the profile
     */
    public void clearPFP(User user) {
        // If the (existing) user's saved pfp was already null, then this is not changing the pfp
        if (user != null && user.getPfpFilePath() == null) {
            changedPfp = false;
            profileImageView.setImageBitmap(User.getDefaultPicture(user.getUserName()));
        }
        // If the (existing) user's saved pfp was not null, then this IS changing the pfp
        else if (user != null && user.getPfpFilePath() != null) {
            changedPfp = true;
            profileImageView.setImageBitmap(User.getDefaultPicture(user.getUserName()));
        }
        // If there is no existing user then this is not changing the pfp (since there can't be one)
        else {
            changedPfp = false;
            profileImageView.setImageBitmap(User.getDefaultPicture());
        }
        profileImageBitmap = null;

    }

    /**
     * The callback action to take when the photoPicker selects a photo.
     * (If the photoPicker returns null (didn't select), perform no action)
     * @param bitmap The bitmap to use as the new photo
     */
    public void onSelectedPhoto(Bitmap bitmap) {
        System.out.println("ProfileFragment photopickercallback triggered!");
        if (bitmap != null && bitmap != profileImageBitmap) {
            changedPfp = true;
            profileImageBitmap = bitmap;
            profileImageView.setImageBitmap(bitmap);
        }
    }


    /**
     * Resets all input fields and the user pfp back to the currently saved values.
     * If the user parameter is null, sets them to blank and a default user pfp.
     * @param user The user object to reset fields to match
     * @author Jared Gourley
     */
    public void resetState(User user) {
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

        // Reset profile image to current profile image
        changedPfp = false;
        if (App.currentUser != null && App.currentUser.getPfpFilePath() != null) {
            profileImageBitmap = App.currentUser.getPfpBitmap();
            profileImageView.setImageBitmap(profileImageBitmap);
        }
        else {
            profileImageBitmap = null;
            String userName = (App.currentUser != null) ? App.currentUser.getUserName() : "";
            profileImageView.setImageBitmap(User.getDefaultPicture(userName));
        }
    }

    private void addtoNotifications(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notifications", "Successfully subscribed to the topic: " + topic);
                    } else {
                        Log.e("Notifications", "Failed to subscribe to the topic: " + topic + ". Error: " + task.getException());
                    }
                });
    }
}