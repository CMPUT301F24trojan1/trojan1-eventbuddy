package com.example.trojanplanner.ProfileUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.events.facility.FacilitySetupFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.ProfileActivity;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileFragment extends Fragment {
    private Database database;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private ImageView profileImage;
    private Bitmap profileImageBitmap = null; // Null if placeholder
    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private boolean changedPfp = false;

    private ProfileActivity profileActivity;
    public Entrant currentEntrant; // Should reference the same user as App.currentUser

    public PhotoPicker.PhotoPickerCallback photoPickerCallback;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch notificationsSwitch;
    private Switch switchProfileFacility;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity; // can get the user from this object
//        if (database == null) {
//            database = new Database();
//        }
        // Database.getDatabase();
        // Add a listener to the running getUser query if it's not set yet
//        if (App.currentUser == null) {
//            Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
//                @Override
//                public void OnSuccess(Object object) {
//
//                }
//            }


            //database.getEntrant(App.deviceId);
//        }

        photoPickerCallback = new PhotoPicker.PhotoPickerCallback() {
            @Override
            public void OnPhotoPickerFinish(Bitmap bitmap) {
                onSelectedPhoto(bitmap);
            }
        };
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
        profileImage.setOnClickListener(v -> createPfpPopup());
        cancelButton.setOnClickListener(v -> handleCancel());
        saveButton.setOnClickListener(v -> handleSave());

        notificationsSwitch = view.findViewById(R.id.switch1);
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
            });
        } else {
            // Log an error if notificationsSwitch is null
            Log.e("ProfileFragment", "notificationsSwitch is null!");
        }

        switchProfileFacility = view.findViewById(R.id.switch_profile_facility);

        switchProfileFacility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Display the FacilitySetupFragment
                FacilitySetupFragment facilitySetupFragment = new FacilitySetupFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.profile_fragment_container, facilitySetupFragment)
                        .addToBackStack(null) // Add to back stack for back navigation
                        .commit();
            }
        });

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


    private void createPfpPopup() {
        new PfpClickPopupFragment(profileActivity).show(profileActivity.getSupportFragmentManager(), "Change Profile Picture");
    }


    private void handleCancel() {
        // Handle cancel action, e.g., clear fields or go back
        System.out.println("Cancel!");
        System.out.println("user: " + App.currentUser);
        if (App.currentUser != null) {
            System.out.println("firstname: " + App.currentUser.getFirstName() + ", lastname: " + App.currentUser.getLastName());
        }
        resetState(App.currentUser); // for now, reset fields to current saved values
    }

    private void handleSave() {
        // Handle save action, e.g., validate input and save to a database or API
        System.out.println("Save!");
        // TODO: This probably should not be allowed to run if the database is currently still retrieving the current user?

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
        }

        Database database = Database.getDB();

        // If pfp image was changed, explicitly choose a new filepath here
        // and use it for both the Firebase Storage upload and the database document insert
        if (changedPfp) { // && profileImageBitmap != null
            Bitmap bitmap = profileImageBitmap;
            String newPfpFilepath = App.deviceId + "/" + System.currentTimeMillis() + ".png";
            App.currentUser.setPfpFilePath(newPfpFilepath);
            System.out.println("bitmap: " + bitmap);
            System.out.println("filepath: " + newPfpFilepath);
            // Upload the image if the change is a non-null image
            if (bitmap != null) {
                database.uploadImage(bitmap, App.currentUser, newPfpFilepath); // assume this doesn't fail??
            }
            else {
                App.currentUser.setPfpFilePath(null);
            }
        }

        // Upload
        database.insertUserDocument(App.currentUser);
    }

    private void handleNotifications(boolean isSubscribed) {
        if (!isSubscribed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }

            FirebaseMessaging.getInstance().subscribeToTopic("default_notifications")
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ? "Successfully subscribed to notifications." : "Subscription failed. Please try again.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("default_notifications")
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ? "Successfully unsubscribed from notifications." : "Unsubscription failed. Please try again.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Resets profile picture to the current profile picture of the user.
     * If user is null, reset to default
     * @param user
     */
    public void resetPFP(User user) {
        changedPfp = false;
        if (user != null && user.getPfpBitmap() != null) {
            profileImageBitmap = user.getPfpBitmap();
            profileImage.setImageBitmap(profileImageBitmap);
        }
        else {
            // If 'remove pfp' button was pressed, we are actually changing it if user pfp was not null before
            if (profileImageBitmap != null) {changedPfp = true; };
            profileImageBitmap = null;
            profileImage.setImageResource(R.drawable.placeholder_avatar);
        }

    }




    public void onSelectedPhoto(Bitmap bitmap) {
        if (bitmap != null && bitmap != profileImageBitmap) {
            changedPfp = true;
            profileImageBitmap = bitmap;
            profileImage.setImageBitmap(bitmap);
        }
    }



    /**
     *
     * @param user
     * @author Jared Gourley
     */
    public void resetState(User user) {
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

        // Reset profile image to current profile image
        changedPfp = false;
        if (App.currentUser != null && App.currentUser.getPfpBitmap() != null) {
            profileImageBitmap = App.currentUser.getPfpBitmap();
            profileImage.setImageBitmap(profileImageBitmap);
        }
        else {
            profileImageBitmap = null;
            profileImage.setImageResource(R.drawable.placeholder_avatar);
        }
    }
}