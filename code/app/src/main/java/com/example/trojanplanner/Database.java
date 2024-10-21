package com.example.trojanplanner;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class Database {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    public Database() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    /**
     *
     * @param owner The user who will claim ownership of the uploaded image through their device ID
     */
    public void uploadImage(User owner) {
        String filePath = owner.getDeviceId() + "/" + System.currentTimeMillis() + ".png"; // StorageReference mountainImagesRef = storageRef.child("images/" + chat_id + Utils.getCurrentTimeStamp() + ".jpg");

        StorageReference refToSave = storageRef.child(filePath);

        String string = "This is my test";
        byte[] data = string.getBytes(Charset.forName("UTF-8"));

        UploadTask uploadTask = refToSave.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            System.out.println("FAIL");
            Toast myToast = Toast.makeText(MainActivity.getAppContext(), R.string.firebase_upload_fail, Toast.LENGTH_SHORT);
            myToast.show();
        }).addOnSuccessListener(taskSnapshot -> {
            System.out.println("SUCCESS");
            Toast myToast = Toast.makeText(MainActivity.getAppContext(), R.string.firebase_upload_success, Toast.LENGTH_SHORT);
            myToast.show();
        });

    }


}
