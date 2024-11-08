package com.example.trojanplanner.events;

import android.os.Bundle;
import android.provider.Settings;
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
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Organizer;

import java.util.ArrayList;
import java.util.Date;

public class CreateEventsFragment extends Fragment {
    private EditText eventName;
    private EditText eventDescription;
    private EditText ticketCost;
    private Button createEventButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_events, container, false);

        eventName = view.findViewById(R.id.eventNameEditText);
        eventDescription = view.findViewById(R.id.eventDescriptionEditText);
        ticketCost = view.findViewById(R.id.eventPriceEditText);
        createEventButton = view.findViewById(R.id.createEventButton);

        createEventButton.setOnClickListener(v -> createEvent());
        return view;
    }

    private void createEvent() {
        // Get event details from user input
        String name = eventName.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String ticketCostText = ticketCost.getText().toString().trim();

        // Validate the inputs (ensure none of the fields are empty or invalid)
        if (name.isEmpty()) {
            eventName.setError("Event name cannot be empty");
            Toast.makeText(getContext(), "Please enter a valid event name.", Toast.LENGTH_SHORT).show();
            return; // Exit if the event name is empty
        }

        if (description.isEmpty()) {
            eventDescription.setError("Event description cannot be empty");
            Toast.makeText(getContext(), "Please enter a valid event description.", Toast.LENGTH_SHORT).show();
            return; // Exit if the description is empty
        }

        if (ticketCostText.isEmpty()) {
            ticketCost.setError("Ticket cost cannot be empty");
            Toast.makeText(getContext(), "Please enter a ticket cost.", Toast.LENGTH_SHORT).show();
            return; // Exit if ticket cost is empty
        }

        float price;
        try {
            price = Float.parseFloat(ticketCostText); // Parse ticket cost as float
        } catch (NumberFormatException e) {
            ticketCost.setError("Invalid ticket cost");
            Toast.makeText(getContext(), "Please enter a valid number for ticket cost.", Toast.LENGTH_SHORT).show();
            return; // Exit if ticket cost is not a valid number
        }

        // Create a mock Organizer (the owner of the facility)
        Organizer mockOwner = new Organizer(
                "Bdd", "Dricc", "owner@example.com", "0221234567",
                "owner123", null, true, false, new ArrayList<>(), null
        );

        // Create a mock Facility using the mock owner
        Facility facility = new Facility(
                "facility123", "Sample Facility", "Sample Location",
                mockOwner, null, null
        );

        // Generate unique device ID
        String deviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String eventId = deviceID + "_" + System.currentTimeMillis(); // Combining device ID with current time

        // Create the Event object with the required constructor
        Event event = new Event(
                name,  // Event name from user input
                description,  // Event description from user input
                price,  // Event price from user input
                facility.getName(),  // Facility name (you could use the facility ID or name)
                new Date(),  // Current date as start date
                new Date(System.currentTimeMillis() + 3600000),  // End date 1 hour after start date
                5,  // Days left to register (for example)
                100,  // Total spots available for the event
                100   // Available spots (initially equal to total spots)
        );

        // Add the event to the organizer instead of the facility
        mockOwner.addEvent(event); // Add event to the Organizer (owner of the facility)

        // Create the Database object and insert the event into the database
        Database db = new Database();
        db.insertEvent(
                success -> {
                    // Handle success
                    Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                },
                failure -> {
                    // Handle failure
                    Toast.makeText(getContext(), "Failed to create event. Please try again.", Toast.LENGTH_SHORT).show();
                },
                event // Pass the event to insert it into the database
        );
    }
}
