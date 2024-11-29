package com.example.trojanplanner.events.organizer;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;

import java.util.Date;

/**
 * A fragment for creating a new event. It collects event information
 * from the user through input fields and creates an event in the database.
 */
public class CreateEventFragment extends Fragment {
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText eventDateEditText; // Add other fields as needed
    private Button createEventButton;
    private Button cancelEventButton;
    private Database database;
    private Switch eventGeolocationSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  // Ensure options menu is handled by the fragment
    }

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

        if (requireActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back button
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);  // Ensure home button is shown
        }

        // Initialize database
        database = Database.getDB();
        eventGeolocationSwitch = view.findViewById(R.id.eventGeolocationSwitch);
        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = view.findViewById(R.id.eventDescriptionEditText);
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

        // Test button that fills up with dummy inputs for faster testing and debugging, comment this out when done
        Button testButton = view.findViewById(R.id.testButton); // Initialize test button
        testButton.setOnClickListener(v -> {
            fillFieldsWithDummyData();
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
        // Retrieve the input fields from the UI
        String name = eventNameEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();
        String date = eventDateEditText.getText().toString();  // Handle other fields as needed

        // Check for missing fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // We will query for the organizer with this device ID, and then set the event using that
        // Define the success action for getting the organizer
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                // Successfully fetched the organizer
                Organizer currentOrganizer = (Organizer) object;
                Log.d("CreateEvent", "Organizer fetched: " + currentOrganizer.getDeviceId());


                // Create the event ID using the organizer's device ID and current timestamp
                String newEventId = currentOrganizer.getDeviceId() + "-" + System.currentTimeMillis();

                // Set default values for event start and end time (you can replace these with actual values)
                Date startDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed
                Date endDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed

                Facility facility = currentOrganizer.getFacility();

                // Create the event object
                Event newEvent = new Event(newEventId, name, description, 0.0f, facility, startDateTime, endDateTime,
                        30, 100L, 100L); // Adjust parameters as needed

                newEvent.setRequiresGeolocation(eventGeolocationSwitch.isChecked()); // Enable geolocation by default for testing
                // Insert the new event into the database
                database.insertEvent(newEvent);

                // Add the event to the organizer's list of created events
                currentOrganizer.addEvent(newEvent);
                // Log the updated list of events for the current organizer
                Log.d("CreateEvent", "Organizer's events after addition: " + currentOrganizer.getCreatedEvents().size() + " events.");
                for (Event event : currentOrganizer.getCreatedEvents()) {
                    Log.d("CreateEvent", "Organizer Event: " + event.getEventId() + ", " + event.getName());
                }

                // Update the organizer in the database
                database.insertUserDocument(currentOrganizer);

                // Notify the user and navigate to the events list fragment
                Toast.makeText(App.activity, "Event created successfully!", Toast.LENGTH_SHORT).show();
                navigateToEventsListFragment(view);
            }
        };

        // Define the failure action for getting the organizer
        Database.QueryFailureAction failureAction = new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                // Log the failure and notify the user
                Log.e("CreateEvent", "Failed to fetch organizer.");
                Toast.makeText(App.activity, "Failed to fetch organizer. Please try again.", Toast.LENGTH_SHORT).show();
            }
        };

        // Call the database method to get the organizer asynchronously
        database.getOrganizer(successAction, failureAction, App.currentUser.getDeviceId());

        return true;
    }

    private void navigateToEventsListFragment(View view) {
        // Ensure the view is valid and the fragment is properly attached to its host activity
        if (getView() != null) {
            NavController navController = Navigation.findNavController(getView());
            navController.navigate(R.id.emptyEventsFragment);
        }
    }

    /**
     * Fills the event fields with dummy data for testing purposes.
     */
    private void fillFieldsWithDummyData() {
        eventNameEditText.setText("Sample Event Name");
        eventDescriptionEditText.setText("This is a sample event description for testing.");
        eventDateEditText.setText("2024-12-25");  // Set a dummy date
        Toast.makeText(getContext(), "Fields filled with dummy data", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Show the same confirmation dialog as the Cancel button
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Event Creation")
                    .setMessage("Are you sure you want to discard this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        NavController navController = NavHostFragment.findNavController(CreateEventFragment.this);
                        navController.navigateUp();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


    /*
    /**
     * Handles options menu item selections.
     *
     * @param item The menu item that was selected.
     * @return True if the item was successfully handled, false otherwise.
     *
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Show the same confirmation dialog as the Cancel button
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Event Creation")
                    .setMessage("Are you sure you want to discard this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Navigate up (equivalent to clicking Cancel)
                        NavController navController = NavHostFragment.findNavController(this);
                        navController.navigateUp();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
