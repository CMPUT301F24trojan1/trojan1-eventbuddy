package com.example.trojanplanner.controller;


import android.app.Activity;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.example.trojanplanner.App;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.MainActivity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that provides the ability to open the user's photo library and select a photo
 * Refer to the selectedPhoto attribute to get the last selected photo.
 * <br>
 * - To use, MUST call initPhotoPicker in the onCreate function of the ACTIVITY the PhotoPicker
 * will be used inside. Callback functions to trigger on photo selection can be registered at any time.
 */
public class PhotoPicker {

    private Bitmap selectedPhoto = null; // TODO: investigate if it's possible to break the sequence and close photo picker with this still true
    private String selectedPhotoPath = null;

    private boolean currentlyPicking = false;

    private ActivityResultRegistry registry;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher = null;
    private Activity activity;
    private LifecycleOwner owner;

    private boolean hasDatabase = false;
    private User user; // Just to assign a user for uploading

    private ArrayList<PhotoPickerCallback> callbacks = new ArrayList<PhotoPickerCallback>(); // Note: this is an array but can only have one callback

    public PhotoPicker() {
        activity = App.activity;
        owner = (LifecycleOwner) activity;
        registry = ( (AppCompatActivity) activity).getActivityResultRegistry();
    }


    public boolean isCurrentlyPicking() {
        return currentlyPicking;
    }

    public Bitmap getSelectedPhoto() {
        return selectedPhoto;
    }

    public boolean hasDatabase() {
        return hasDatabase;
    }


    /**
     * A callback so that classes can call the PhotoPicker and receive the selected photo when it
     * is chosen.
     */
    public interface PhotoPickerCallback {
        void OnPhotoPickerFinish(Bitmap bitmap);
    }

    /**
     * Notify all callbacks that the PhotoPicker has finished and selected an image. This array
     * is important to allow adding callbacks after being the PhotoPicker is already initialized.
     * @param bitmap
     */
    private void notifyCallbacks(Bitmap bitmap) {
        for (PhotoPickerCallback callback : callbacks) {
            callback.OnPhotoPickerFinish(bitmap);
        }
    }

    /**
     * Sets the callback that will be triggered when the PhotoPicker successfully selects a photo.
     *
     * @param callback The callback that should be called when the PhotoPicker selects a photo.
     */
    public void setCallback(PhotoPickerCallback callback) {
        callbacks.clear();
        if (callback != null) {
            callbacks.add(callback);
        }
    }

    /**
     * Method to initialize the PhotoPicker instance. This must be called before attempting to call
     * openPhotoPicker and THIS METHOD MUST BE CALLED IN THE ACTIVITY'S ONCREATE METHOD.
     * <br>
     * On a selected photo, the photoPicker will call the callback set by setCallback().
     * <br>
     * If a database object is passed, the PhotoPicker will upload the photo to the database when selected.
     * @param database The database to upload to (set to null to avoid uploading)
     * @author Jared Gourley
     */
    public void initPhotoPicker(Database database) {

        // https://developer.android.com/training/data-storage/shared/photopicker#select-single-item
        // https://developer.android.com/training/basics/intents/result#separate - needed to modify call to work from non-Activity object
        photoPickerLauncher =
                registry.register("photoPickerResult", owner, new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        Bitmap bitmap;
                        // Attempt to convert received URI to bitmap
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                            selectedPhoto = bitmap;
                        }
                        catch (IOException e) {
                            Log.d("PhotoPicker", "uri invalid/no permissions");
                            selectedPhoto = null;
                            currentlyPicking = false;
                            notifyCallbacks(selectedPhoto);
                            return;
                        }
                        // Trigger the successful callback with the photo and upload if desired
                        notifyCallbacks(selectedPhoto);
                        if (database != null) {
                            database.uploadImage(bitmap, user);
                        }
                        currentlyPicking = false;
                    // If no photo was selected
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                        selectedPhoto = null;
                        currentlyPicking = false;
                        notifyCallbacks(selectedPhoto);
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
     * If a callback is passed, the callback will be triggered when the PhotoPicker is closed
     * and the chosen photo will be given (could be null if PhotoPicker cancelled).
     * @author Jared Gourley
     */
    public void initPhotoPicker() {
        initPhotoPicker(null);
    }


    // TODO: Is this function necessary?
    /**
     * A method that uninitializes the PhotoPicker if initPhotoPicker was called.
     * Should be called before switching activities
     * @author Jared Gourley
     */
    public void deinitPhotoPicker() {
        photoPickerLauncher.unregister();
    }

    /**
     * Creates and opens a PhotoPicker UI screen to allow choosing a photo from the user's photo album.
     * Results of the selection can be checked through the callback function defined in initPhotoPicker.
     * <br>
     * Requires calling the initPhotoPicker function before using this one.
     * @param user The current user of the app (required if planning to use database upload)
     * @author Jared Gourley
     */
    public void openPhotoPicker(User user) {
        // Throw error if init wasn't called
        if (photoPickerLauncher == null) {
            throw new RuntimeException("initPhotoPicker was not called.");
        }

        // Block action if photo picker is already open
        if (currentlyPicking) {
            return; // Prevents spam clicking button from causing problems
        }

        currentlyPicking = true;
        this.user = user;

        // Launch the photo picker and let the user choose only images.
        photoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

    }


    /**
     * Creates and opens a PhotoPicker UI screen to allow choosing a photo from the user's photo album.
     * Results of the selection can be checked through the callback function defined in initPhotoPicker.
     * <br>
     * Requires calling the initPhotoPicker function before using this one.
     * @author Jared Gourley
     */
    public void openPhotoPicker() {
        if (hasDatabase) {
            throw new RuntimeException("Must provide a user parameter when initialized with database.");
        }

        openPhotoPicker(null);
    }



}
