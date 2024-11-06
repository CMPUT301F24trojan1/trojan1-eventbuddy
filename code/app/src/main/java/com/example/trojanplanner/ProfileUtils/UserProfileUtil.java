package com.example.trojanplanner.ProfileUtils;

import androidx.annotation.NonNull;

import com.example.trojanplanner.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class UserProfileUtil {
    private static final String USERS_NODE = "users"; // Firebase database node for users
    private final DatabaseReference databaseReference;

    public UserProfileUtil() {
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS_NODE);
    }

    // Create a new user in Firebase
    public void createUser(User user) {
        String userId = user.getDeviceId(); // Use deviceId as the unique identifier
        databaseReference.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("User created successfully");
                    } else {
                        System.out.println("User creation failed: " + task.getException());
                    }
                });
    }

    // Update existing user information
    public void updateUser(User user) {
        String userId = user.getDeviceId();
        databaseReference.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("User updated successfully");
                    } else {
                        System.out.println("User update failed: " + task.getException());
                    }
                });
    }

    // Retrieve a user by device ID
    public void getUser(String deviceId, ValueEventListener listener) {
        databaseReference.child(deviceId).addListenerForSingleValueEvent(listener);
    }

    // Example of how to listen for a user's data and handle it
    public void listenForUserData(String deviceId) {
        getUser(deviceId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    // Handle the user object (e.g., update UI, store in local state)
                    assert user != null;
                    System.out.println("User retrieved: " + user.getFirstName() + " " + user.getLastName());
                } else {
                    System.out.println("User not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error retrieving user: " + databaseError.getMessage());
            }
        });
    }
}
