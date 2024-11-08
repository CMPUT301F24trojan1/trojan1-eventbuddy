package com.example.trojanplanner.view;

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
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class CreateEventFragment extends Fragment {

    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
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
        createEventButton = view.findViewById(R.id.createEventButton);

        createEventButton.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {
        Log.d("CreateEventFragment", "createEvent called");

        String name = eventNameEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the user and check its type
        Object userObj = getActivity().getIntent().getSerializableExtra("user");
        Organizer owner = null;

        if (userObj instanceof Organizer) {
            owner = (Organizer) userObj;
        } else if (userObj instanceof Entrant) {
            Entrant entrant = (Entrant) userObj;
            owner = new Organizer(
                    entrant.getLastName(),
                    entrant.getFirstName(),
                    entrant.getEmail(),
                    entrant.getPhoneNumber(),
                    entrant.getDeviceId(),
                    "Entrant", // Set role
                    true, // Is organizer
                    false, // Is admin
                    new ArrayList<>(), // No created events
                    null // No facility linked
            );
        } else {
            Toast.makeText(getContext(), "User not found or invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the new Event object with required fields
        String eventId = owner.getDeviceId() + "_" + System.currentTimeMillis(); // Set event ID
        String eventName = owner.getDeviceId() + "_" + System.currentTimeMillis(); // Set event name

        // Define the event start and end dates
        Date eventStart = new Date(); // Set to the current date/time for simplicity
        Date eventEnd = new Date(eventStart.getTime() + 86400000); // End time: 1 day later

        // Create the new Event object using the constructor
        Event newEvent = new Event(eventName, description, 12.99f, null, eventStart, eventEnd, 10, 100L, 100L);
        newEvent.setEventId(eventId); // Set event ID
        newEvent.setRequiresGeolocation(true); // Set geolocation requirement
        newEvent.setStatus("active"); // Set status
        newEvent.setEnrolledList(new ArrayList<>()); // Set enrolled list
        newEvent.setPendingList(new ArrayList<>()); // Set pending list
        newEvent.setCancelledList(new ArrayList<>()); // Set cancelled list
        newEvent.setWaitingList(new ArrayList<>()); // Set waitlist
        newEvent.setWaitlistCapacity(100); // Set waitlist capacity
        newEvent.setWaitlistOpen(new Date()); // Example date for waitlist open
        newEvent.setWaitlistClose(new Date()); // Example date for waitlist close
        newEvent.setRecurrenceType(Event.RecurrenceType.AFTER_OCCURRENCES); // Set recurrence type
        newEvent.setRecurrenceDays(new HashSet<>(List.of("M", "W", "F"))); // Example recurrence days
        newEvent.setRecurrenceEndDate(new Date(eventStart.getTime() + 2592000000L)); // Set the end date for the recurrence (30 days later)

        // Insert the new event into the database using the provided method
        database.insertEvent(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("CreateEventFragment", "Event created successfully in Firestore.");
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("CreateEventFragment", "Failed to create event: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, newEvent);
    }
}
