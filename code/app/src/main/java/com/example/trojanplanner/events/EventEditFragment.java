package com.example.trojanplanner.events;

import android.os.Bundle;
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

import com.example.trojanplanner.HelperFragments.WaitlistFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventEditFragment extends Fragment {

    private Event event;

    private EditText eventNameEditText;
    private EditText eventSpotsAvailableEditText;
    private EditText eventDescriptionEditText;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText eventPriceEditText;
    private Button saveChangesButton;
    private Button viewWaitlistButton;
    private Button returnToEventsButton;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_edit_fragment, container, false);

        // Initialize UI components
        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        eventSpotsAvailableEditText = view.findViewById(R.id.eventSpotsAvailableEditText);
        eventDescriptionEditText = view.findViewById(R.id.eventDescriptionEditText);
        eventDateEditText = view.findViewById(R.id.eventDateEditText);
        eventTimeEditText = view.findViewById(R.id.eventTimeEditText);
        eventPriceEditText = view.findViewById(R.id.eventPriceEditText);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        viewWaitlistButton = view.findViewById(R.id.viewWaitlistButton);
        returnToEventsButton = view.findViewById(R.id.returnToEventsButton);

        // Retrieve the Event object from arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }

        if (event == null) {
            Log.e("EventSettingsFragment", "Event is null!");
            Toast.makeText(getContext(), "Event data is unavailable", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Populate UI with event data
        populateEventDetails();

        // Set up button click listeners
        saveChangesButton.setOnClickListener(v -> saveChanges());
        viewWaitlistButton.setOnClickListener(v -> openWaitlistFragment());
        returnToEventsButton.setOnClickListener(v -> returnToEvents());

        return view;
    }

    private void populateEventDetails() {
        eventNameEditText.setText(event.getName());
        eventSpotsAvailableEditText.setText(String.valueOf(event.getTotalSpots()));
        eventDescriptionEditText.setText(event.getDescription());

        // Format and set date and time
        eventDateEditText.setText(dateFormat.format(event.getStartDateTime()));
        eventTimeEditText.setText(timeFormat.format(event.getStartDateTime()));
        eventPriceEditText.setText(String.valueOf(event.getPrice()));
    }

    private void saveChanges() {
        String name = eventNameEditText.getText().toString().trim();
        String spotsAvailableText = eventSpotsAvailableEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        String dateText = eventDateEditText.getText().toString().trim();
        String timeText = eventTimeEditText.getText().toString().trim();
        String priceText = eventPriceEditText.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || spotsAvailableText.isEmpty() || description.isEmpty()
                || dateText.isEmpty() || timeText.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long spotsAvailable = Long.parseLong(spotsAvailableText); // Convert spots to int
            float price = Float.parseFloat(priceText);                 // Convert price to float
            Date date = dateFormat.parse(dateText);                    // Parse date
            Date time = timeFormat.parse(timeText);                    // Parse time

            // Update event object with new values
            event.setName(name);
            event.setTotalSpots(spotsAvailable);                       // Set spots as int
            event.setDescription(description);
            event.setStartDateTime(combineDateAndTime(date, time));    // Combine date and time
            event.setPrice(price);                                     // Set price as float

            // Here, you could save the event to the database if needed
            // For example:
            // database.saveEvent(event);

            Toast.makeText(getContext(), "Event details updated successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException | ParseException e) {
            Toast.makeText(getContext(), "Invalid input format", Toast.LENGTH_SHORT).show();
            Log.e("EventSettingsFragment", "Error parsing inputs", e);
        }
    }

    private Date combineDateAndTime(Date date, Date time) {
        // Combine date and time into a single Date object
        long combinedTime = date.getTime() + time.getTime() % (24 * 60 * 60 * 1000);
        return new Date(combinedTime);
    }

    private void openWaitlistFragment() {
        // Navigate to WaitlistFragment with the event
        WaitlistFragment waitlistFragment = new WaitlistFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        waitlistFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, waitlistFragment)
                .addToBackStack(null)
                .commit();
    }

    private void returnToEvents() {
        // Go back to the previous fragment
        getParentFragmentManager().popBackStack();
    }
}
