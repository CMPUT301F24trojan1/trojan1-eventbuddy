package com.example.trojanplanner.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trojanplanner.App;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A class that handles adding/querying/modifying/removing documents from the Firestore Database,
 * as well as uploading/downloading/deleting images from the Firebase Storage.
 */
public class Database {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Activity activity;

    private PhotoPicker photoPicker;

    public Database() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        activity = App.activityManager.getActivity();
    }

// ======================== PhotoPicker integration ========================

    /**
     * A method that initializes the PhotoPicker if you intend to use the uploadFromPhotoPicker method.
     * THIS METHOD MUST BE CALLED IN THE ACTIVITY'S ONCREATE METHOD
     */
    public void initPhotoPicker() {
        photoPicker = new PhotoPicker();
        photoPicker.initPhotoPicker(this);
    }

    // TODO: Is this function necessary?
    /**
     * A method that uninitializes the PhotoPicker if initPhotoPicker was called.
     * Should be called before switching activities
     */
    public void deinitPhotoPicker() {
        photoPicker.deinitPhotoPicker();
    }


    /**
     * Method that creates a PhotoPicker and uploads whatever image gets selected to the database.
     * MUST first call initPhotoPicker to use this function.
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     */
    public void uploadFromPhotoPicker(User owner) {
        // Throw error if init wasn't called or if database was not added during init
        if (photoPicker == null) {
            throw new RuntimeException("initPhotoPicker was not called.");
        }
        if (!photoPicker.hasDatabase()) {
            throw new RuntimeException("Database object was not passed during init");
        }

        // Open PhotoPicker (will upload on selection since database was passed on init
        photoPicker.openPhotoPicker(owner);

    }


// ======================== Upload to Firebase Storage ========================

    /**
     * Uploads an image to the Firebase Storage area tied to a given user.
     * @param uri The uri reference to the image that should be uploaded
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     * @param successListener The action that should be taken on a successful upload
     * @param failureListener The action that should be taken on a failed upload
     *
     */
    public void uploadImage(Uri uri, @NonNull User owner, OnSuccessListener successListener, OnFailureListener failureListener) {
        String filePath = owner.getDeviceId() + "/" + System.currentTimeMillis() + ".png";
        StorageReference refToSave = storageRef.child(filePath);

        // Attempt to get a bitmap of the uri reference
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        }
        catch (IOException e) {
            System.out.println("uri invalid/no permissions");
            return;
        }

        // Compress and convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload!
        UploadTask uploadTask = refToSave.putBytes(data);
        uploadTask.addOnSuccessListener(successListener);
        uploadTask.addOnFailureListener(failureListener);

    }

    /**
     * Uploads an image to the Firebase Storage area tied to a given user.
     * A wrapper function for the 4-parameter uploadImage designed for easier use. Sets a generic successlistener
     * and failurelistener which should be suitable for most cases.
     * @param uri The uri reference to the image that should be uploaded
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     */
    public void uploadImage(Uri uri, User owner) {

        OnSuccessListener successListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("SUCCESS");
                Toast myToast = Toast.makeText(activity, R.string.firebase_upload_success, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FAIL");
                Toast myToast = Toast.makeText(activity, R.string.firebase_upload_fail, Toast.LENGTH_SHORT);
                myToast.show();
            }
        };

        // Call the proper uploadImage method
        uploadImage(uri, owner, successListener, failureListener);

    }


    // ===================== Download from Firebase Storage =====================



}
