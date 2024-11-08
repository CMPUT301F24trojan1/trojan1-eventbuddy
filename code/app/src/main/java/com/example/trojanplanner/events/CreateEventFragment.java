package com.example.trojanplanner.events;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.trojanplanner.model.Organizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class CreateEventFragment extends Fragment {

    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private String eventFacilityEditText; // Temporarily using a string instead of EditText
    private EditText eventDateEditText; // Add other fields as needed
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
        eventFacilityEditText = "test"; // Temporary test string for facility
        eventDateEditText = view.findViewById(R.id.eventDateEditText); // Add other fields as needed
        createEventButton = view.findViewById(R.id.createEventButton);

        createEventButton.setOnClickListener(v -> createEvent());


        //TEMP TEST
        Button testButton = view.findViewById(R.id.testCreateEventButton);
        // Set click listener for the test Create Event button
        testButton.setOnClickListener(v -> testCreateEvent());
    }

    private void createEvent() {
        String name = eventNameEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();
        String facility = eventFacilityEditText; // Use the temporary test string
        String date = eventDateEditText.getText().toString(); // Handle other fields as needed
        float price = 0;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(facility) || TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Organizer currentUser = (Organizer) getActivity().getIntent().getSerializableExtra("user");
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming you want to set some default values for the event date fields
        Date startDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed
        Date endDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed

        Event newEvent = new Event(name, description, price, facility, startDateTime, endDateTime,
                30, 100L, 100L); // Adjust parameters as needed

        // Generate a unique event ID (you may want to implement this logic)
        newEvent.setEventId("event_" + System.currentTimeMillis()); // Simple unique ID generation

        // Insert the new event into the database
        database.insertEvent(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                // Optionally, navigate back or update UI
                requireActivity().onBackPressed(); // Or navigate to another fragment
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, newEvent);
    }

    // Test Create Event function
    private void testCreateEvent() {
        // Simulate the same process as createEvent() but without requiring user input
        eventNameEditText.setText("Test Event");
        eventDescriptionEditText.setText("This is a test event created via the test button.");
        eventFacilityEditText = "Test Facility"; // Set to a default test value
        eventDateEditText.setText("2024-12-01"); // Set a default date for testing

        // Call the original createEvent method to simulate success
        createEvent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle back press to return to the previous fragment or Activity
                requireActivity().onBackPressed(); // Or use NavController to navigate back
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}