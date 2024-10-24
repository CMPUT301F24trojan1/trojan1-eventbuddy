package com.example.trojanplanner;


import android.app.Activity;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

/**
 * Class that provides the ability to open the user's photo library and select a photo
 * Refer to the selectedPhoto attribute to get the last selected photo.
 * <br>
 * - Note that if reusing the same PhotoPicker the selectedPhoto attribute could be from a
 * previous prompt if the user has not selected again yet
 */
public class PhotoPicker {

    private Uri selectedPhoto = null; // TODO: investigate if it's possible to break the sequence and close photo picker with this still true
    private boolean currentlyPicking = false;

    private ActivityResultRegistry registry;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher = null;
    private Activity activity;
    private LifecycleOwner owner;

    private boolean hasDatabase = false;
    private User user; // Just to assign a user for uploading

    public PhotoPicker() {
        activity = App.activityManager.getActivity();
        owner = (LifecycleOwner) activity;
        registry = ( (AppCompatActivity) activity).getActivityResultRegistry();
    }


    public boolean isCurrentlyPicking() {
        return currentlyPicking;
    }

    public Uri getSelectedPhoto() {
        return selectedPhoto;
    }

    public boolean hasDatabase() {
        return hasDatabase;
    }


    /**
     * Method to initialize the PhotoPicker instance. This must be called before attempting to call
     * openPhotoPicker and THIS METHOD MUST BE CALLED IN THE ACTIVITY'S ONCREATE METHOD.
     * <br>
     * If a database object is passed, the PhotoPicker will upload the photo to the database when selected.
     */
    public void initPhotoPicker(Database database) {
        // https://developer.android.com/training/data-storage/shared/photopicker#select-single-item
        // https://developer.android.com/training/basics/intents/result#separate - needed to modify call to work from non-Activity object
        photoPickerLauncher =
                registry.register("photoPickerResult", owner, new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        selectedPhoto = uri;
                        if (database != null) {
                            database.uploadImage(uri, user);
                        }
                        currentlyPicking = false;
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                        selectedPhoto = null;
                        currentlyPicking = false;
                    }
                });
        if (database != null) {
            hasDatabase = true;
        }
    }

    /**
     * Method to initialize the PhotoPicker instance. This must be called before attempting to call
     * openPhotoPicker and THIS METHOD MUST BE CALLED IN THE ACTIVITY'S ONCREATE METHOD.
     * <br>
     * If a database object is passed, the PhotoPicker will upload the photo to the database when selected.
     */
    public void initPhotoPicker() {
        initPhotoPicker(null);
    }


    // TODO: Is this function necessary?
    /**
     * A method that uninitializes the PhotoPicker if initPhotoPicker was called.
     * Should be called before switching activities
     */
    public void deinitPhotoPicker() {
        photoPickerLauncher.unregister();
    }

    public void openPhotoPicker(User user) {
        // Throw error if init wasn't called
        if (photoPickerLauncher == null) {
            throw new RuntimeException("initPhotoPicker was not called.");
        }

        // Block action if photo picker is already open
        if (currentlyPicking) {
            throw new RuntimeException("A Photopicker menu is already open");
        }

        currentlyPicking = true;
        this.user = user;

        // Launch the photo picker and let the user choose only images.
        photoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

    }

}
