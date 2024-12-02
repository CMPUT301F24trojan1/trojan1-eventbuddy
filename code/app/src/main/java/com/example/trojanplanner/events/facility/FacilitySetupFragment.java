package com.example.trojanplanner.events.facility;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.ProfileUtils.PfpClickPopupFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.MainActivity;
import com.example.trojanplanner.view.ProfileActivity;

import java.util.Objects;

/**
 * A fragment that handles the setup of a new facility. It allows the user to input the
 * facility's name, facility's location, and upload a photo, and then saves the facility details
 * in the database.
 */
public class FacilitySetupFragment extends Fragment {
    private ImageView facilityImageView;
    private Bitmap facilityImageBitmap = null;
    private boolean changedPfp = false;

    private EditText facilityNameEditText;
    private EditText facilityLocationEditText;

    private PhotoPicker photoPicker; // Will borrow the photoPicker that was initialized in MainActivity or ProfileActivity depending who sent us here
    private Database database;

    private Organizer organizer;
    private Facility facility;
    private boolean organizerInsertNeeded = false;

    /**
     * Inflates the layout for this fragment and sets up the user interface components.
     *
     * @param inflater           The LayoutInflater object to inflate the view.
     * @param container          The container view to attach the fragment to.
     * @param savedInstanceState The saved instance state for the fragment, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_setup, container, false);

        database = Database.getDB();

        facilityImageView = view.findViewById(R.id.facility_photo);
        facilityNameEditText = view.findViewById(R.id.facility_name);
        facilityLocationEditText = view.findViewById(R.id.location);
        Button uploadPhotoButton = view.findViewById(R.id.upload_photo_button);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        uploadPhotoButton.setOnClickListener(v -> createPfpPopup());
        saveButton.setOnClickListener(v -> saveFacility());

        // Set action for the "Back" button (returns to the fragment that we came from)
        cancelButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                // Navigate to emptyEventsFragment
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.emptyEventsFragment);
            }
            else { // Otherwise we're in ProfileActivity
                // Navigate back in the fragment stack (return to the previous fragment)
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Ensure the app bar is visible
        if (getActivity() instanceof AppCompatActivity) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
        }

        // Get the photoPicker (the one we borrow depends on what activity we're in)
        if (getActivity() instanceof MainActivity) {
            photoPicker = ((MainActivity) App.activity).mainActivityPhotoPicker;
        }
        else { // The activity is ProfileActivity in this case
            photoPicker = ((ProfileActivity) App.activity).profileActivityPhotoPicker;
        }


        // Get the current organizer (or turn the current entrant into an organizer if they're not one)
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                organizerInsertNeeded = false; // deviceID was already an organizer
                Log.d("FacilitySetupFragment", "getOrganizer succeeded: Ready to run the fragment");
                organizer = (Organizer) object;
                facility = organizer.getFacility();
                resetState(facility);
            }
        };

        // If the getOrganizer query fails, it's probably because this user isn't an organizer yet.
        // Query them as an entrant and turn them into one.
        Database.QueryFailureAction failureAction = new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                Log.d("FacilitySetupFragment", "getOrganizer failed: new organizer?");
                Database.QuerySuccessAction nestedSuccessAction = new Database.QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
                        organizerInsertNeeded = true; // deviceID was not yet an organizer, we will have to update that
                        Log.d("FacilitySetupFragment", "getEntrant succeeded: Ready to run the fragment");
                        organizer = ((Entrant) object).returnOrganizer();
                        resetState(null);
                    }
                };
                Database.QueryFailureAction nestedFailureAction = new Database.QueryFailureAction() {
                    @Override
                    public void OnFailure() {
                        Log.d("FacilitySetupFragment: FAILURE", "FAILED TO GET THE ORGANIZER");
                        Toast myToast = Toast.makeText(App.activity, "FAILED TO GET THE ORGANIZER!", Toast.LENGTH_LONG);
                        myToast.show();
                    }
                };
                database.getEntrant(nestedSuccessAction, nestedFailureAction, App.deviceId);
            }
        };

        database.getOrganizer(successAction, failureAction, App.deviceId);



        // Define and add a callback to the photopicker
        PhotoPicker.PhotoPickerCallback photoPickerCallback = new PhotoPicker.PhotoPickerCallback() {
            @Override
            public void OnPhotoPickerFinish(Bitmap bitmap) {
                onPhotoSelected(bitmap);
            }
        };
        photoPicker.setCallback(photoPickerCallback);

        return view;
    }

    /**
     * The callback action to take when the photoPicker selects a photo.
     * (If the photoPicker returns null (didn't select), perform no action)
     * @param bitmap The bitmap received by the PhotoPicker to set
     */
    private void onPhotoSelected(Bitmap bitmap) {
        System.out.println("FacilitySetupFragment photopickercallback triggered!");
        if (bitmap != null && bitmap != facilityImageBitmap) {
            changedPfp = true;
            facilityImageBitmap = bitmap;
            facilityImageView.setImageBitmap(bitmap);
        }
    }


    /**
     * Resets facility picture to the current picture of the facility.
     * If facility is null, reset to the default pfp
     *
     * @param facility The facility (or null) to get the picture from and display on the screen again
     * @author Jared Gourley
     */
    private void clearFacilityPhoto(Facility facility) {
        // If the (existing) facility's saved picture was already null, then this is not changing the picture
        if (facility != null && facility.getPfpFacilityFilePath() == null) {
            changedPfp = false;
        }
        // If the (existing) facility's saved picture was not null, then this IS changing the picture
        else if (facility != null && facility.getPfpFacilityFilePath() != null) {
            changedPfp = true;
        }
        // If there is no existing facility then this is not changing the picture (since there can't be one)
        else {
            changedPfp = false;
        }

        facilityImageView.setImageBitmap(Facility.getDefaultPicture());
        facilityImageBitmap = null;
    }


    /**
     * Resets all input fields and the facility photo back to the currently saved values.
     * If the facility parameter is null, sets them to blank and a default facility picture.
     * @param facility The facility object to reset fields to match
     * @author Jared Gourley
     */
    private void resetState(Facility facility) {
        changedPfp = false;

        if (facility != null) {
            // Assume name and location field must be non-null
            facilityNameEditText.setText(facility.getName());
            facilityLocationEditText.setText(facility.getLocation());
            facilityImageView.setImageBitmap(facility.getPfpFacilityBitmap()); // Sets default if null
        }
        else {
            facilityNameEditText.setText("");
            facilityLocationEditText.setText("");
            facilityImageView.setImageBitmap(Facility.getDefaultPicture()); // Reset to a default image
        }
    }


    /**
     * Creates a popup to allow either changing or removing the selected facility picture.
     */
    private void createPfpPopup() {
        PfpClickPopupFragment.PfpPopupFunctions popupFunctions = new PfpClickPopupFragment.PfpPopupFunctions() {
            @Override
            public void changePFP() {
                photoPicker.openPhotoPicker();
            }
            @Override
            public void removePFP() {
                clearFacilityPhoto(facility);
            }
        };

        new PfpClickPopupFragment(popupFunctions).show(((AppCompatActivity) App.activity).getSupportFragmentManager(), "Change Facility Picture");
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
        if (organizer == null) {
            Toast.makeText(getActivity(), "Please wait a moment to load the organizer", Toast.LENGTH_SHORT).show();
            return;
        }


        // Create a new facility
        // Either create the facility here or update its attributes
        if (facility != null) {
            facility.setName(name);
            facility.setLocation(location);
        }
        else {
            organizerInsertNeeded = true; // Need to update the facility reference attribute
            String facilityId = organizer.getDeviceId() + "-" + System.currentTimeMillis();
            facility = new Facility(name, facilityId, location, organizer, null); // We'll set the pfp right away
        }


        // Decide on a new image filepath and upload it if we're changing the pfp
        String newPfpFilepath;
        if (changedPfp) {
            if (facilityImageBitmap != null) {
                newPfpFilepath = organizer.getDeviceId() + "/" + System.currentTimeMillis() + ".png";
                facility.setPfpFacilityFilePath(newPfpFilepath);
                facility.setPfpFacilityBitmap(facilityImageBitmap);
                database.uploadImage(facilityImageBitmap, organizer, newPfpFilepath);
            }
            else {
                database.deleteImage(facility.getPfpFacilityFilePath());
                facility.setPfpFacilityFilePath(null);
                facility.setPfpFacilityBitmap(null);
            }
        }

        // Attach the facility to the organizer and upload the facility since it's built
        organizer.setFacility(facility);
        database.insertFacility(facility);

        if (organizerInsertNeeded) { // Only if the user was not yet an organizer or if the facility used to be null
            database.insertUserDocument(organizer);
        }
        organizerInsertNeeded = false;

        App.currentUser.setIsOrganizer(true); // TODO idk if this is the best idea?


        // Navigate back to emptyEventsFragment (IF STILL ON MAINACTIVITY) after saving with a delay
        if (App.activity.getClass() == MainActivity.class) {
            new android.os.Handler().postDelayed(() -> {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.emptyEventsFragment);
            }, 2000);  // Delay in milliseconds
        }

    }



}