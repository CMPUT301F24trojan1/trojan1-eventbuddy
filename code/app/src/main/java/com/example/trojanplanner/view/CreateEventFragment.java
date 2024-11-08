package com.example.trojanplanner.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class CreateEventFragment extends Fragment {

    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private String eventFacilityEditText; // Temporarily using a string for facility
    private EditText eventDateEditText;
    private Button createEventButton;
    private Database database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database
        database = new Database();

        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = view.findViewById(R.id.eventDescriptionEditText);
        eventDateEditText = view.findViewById(R.id.eventDateEditText);
        createEventButton = view.findViewById(R.id.createEventButton);

        eventFacilityEditText = "Test Facility"; // Temporary hardcoded value for testing

        createEventButton.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {
        Log.d("CreateEventFragment", "createEvent called");

        String name = eventNameEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();
        String facilityName = eventFacilityEditText; // Use the temporary value for facility
        String facilityId = "facility_" + System.currentTimeMillis(); // Generate a unique facility ID
        String location = "Test Location"; // Example location
        Organizer owner = (Organizer) getActivity().getIntent().getSerializableExtra("user");

        // Validate the input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(facilityName)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (owner == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Date startDateTime = new Date(); // Placeholder for the start date
        Date endDateTime = new Date(); // Placeholder for the end date

        // Initialize the Facility object
        Facility facilityObj = new Facility(facilityName, facilityId, location, owner, null, null);

        // Create the new Event object
        Event newEvent = new Event(name, description, 0.0f, facilityObj, startDateTime, endDateTime, 30, 100L, 100L);

        newEvent.setEventId("event_" + System.currentTimeMillis()); // Generate a unique event ID

        // Insert the new event into the database
        database.insertEvent(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, newEvent);
    }
}
