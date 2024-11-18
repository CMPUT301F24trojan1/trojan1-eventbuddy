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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Organizer;
import com.example.trojanplanner.view.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

/**
 * A fragment for creating a new event. It collects event information
 * from the user through input fields and creates an event in the database.
 */
public class CreateEventFragment extends Fragment {

    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private String eventFacilityEditText; // Temporarily using a string instead of EditText
    private EditText eventDateEditText; // Add other fields as needed
    private Button createEventButton;
    private Button cancelEventButton;
    private Database database;

    /**
     * Inflates the layout for this fragment and returns the root view.
     *
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The container view to attach the fragment to.
     * @param savedInstanceState The saved instance state for the fragment, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_events, container, false);
    }

    /**
     * Initializes the view components and sets up event listeners for user interactions.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved instance state for the fragment, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database
        database = Database.getDB();

        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = view.findViewById(R.id.eventDescriptionEditText);
        eventFacilityEditText = "test"; // Temporary test string for facility
        eventDateEditText = view.findViewById(R.id.eventDateEditText); // Add other fields as needed
        createEventButton = view.findViewById(R.id.createEventButton);
        cancelEventButton = view.findViewById(R.id.cancelEventButton); // Cancel button


        // Handle "Create Event" button click
        createEventButton.setOnClickListener(v -> {
            boolean ret = createEvent(view);
            if (ret) {
                navigateToEventsListFragment(view);
            }
        });

        // Handle "Cancel" button click
        cancelEventButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Event Creation")
                    .setMessage("Are you sure you want to discard this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        NavController navController = NavHostFragment.findNavController(this);
                        navController.navigateUp();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

    }

    /**
     * Creates a new event by collecting data from the user inputs and uploading it to the database.
     * It ensures that all required fields are filled out and that the current user is an organizer.
     * Attempts to create the event and returns true if creation succeeds.
     *
     * @return True if the event was successfully created and uploaded, false otherwise.
     */
    private boolean createEvent(View view) {
        String name = eventNameEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();
        String facility = eventFacilityEditText; // Use the temporary test string
        String date = eventDateEditText.getText().toString(); // Handle other fields as needed
        float price = 0;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(facility) || TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            System.out.println("Please fill in all fields");
            return false;
        }

        Entrant currentUser = (Entrant) App.currentUser;
        Organizer currentOrganizer = currentUser.returnOrganizer();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Organizer not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Set default values for event start and end time
        Date startDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed
        Date endDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed

        // Create the event object
        Event newEvent = new Event(name, description, price, null, startDateTime, endDateTime,
                30, 100L, 100L); // Adjust parameters as needed

        // Generate a unique event ID (simple logic)
        newEvent.setEventId(currentOrganizer.getDeviceId() + "-" + System.currentTimeMillis());

        // Insert the new event into the database
        database.insertEvent(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (isAdded()) {
                    Toast.makeText(App.activity, "Event created successfully!", Toast.LENGTH_SHORT).show();
                    // Add the event to the organizer's list of created events
                    currentOrganizer.addEvent(newEvent);
                    database.insertUserDocument(currentOrganizer);
                    navigateToEventsListFragment(view);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (isAdded()) {
                    Toast.makeText(App.activity, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, newEvent);

        return true;
    }

  
    private void navigateToEventsListFragment(View view) {
        if (view != null) {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.eventsListFragment);
        }
    }
  
  
  
    /**
     * Handles options menu item selections.
     *
     * @param item The menu item that was selected.
     * @return True if the item was successfully handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
