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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Organizer;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.MainActivity;
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

        view.findViewById(R.id.createEventButton).setOnClickListener(v -> {
            // Ensure we are navigating from the correct fragment
            boolean ret = createEvent();
            if (ret) {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.createEventsFragment_to_fragment_events_list);
            }
        });
    }

    /**
     *
     * @return True if all checks to upload passed, false if not
     */
    private boolean createEvent() {
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

        //Organizer currentUser = (Organizer) getActivity().getIntent().getSerializableExtra("user"); // TODO access the MainActivity currentUser attribute directly
        Entrant currentUser = (Entrant) ((MainActivity) App.activityManager.getActivity()).currentUser; // ASK JARED
        Organizer currentOrganizer = currentUser.returnOrganizer();

        System.out.println("name:" + currentOrganizer.getFirstName() + " " + currentOrganizer.getLastName());
        System.out.println("createdEvents:" + currentOrganizer.getCreatedEvents());



        if (currentUser == null) { // TODO also check that this is an organizer (after the halfway point)
            Toast.makeText(getContext(), "Organizer not found", Toast.LENGTH_SHORT).show();
            System.out.println("Organizer not found");
            return false;
        }

        // Assuming you want to set some default values for the event date fields
        Date startDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed
        Date endDateTime = new Date(); // Placeholder: Replace with actual date parsing if needed

        Event newEvent = new Event(name, description, price, null, startDateTime, endDateTime,
                30, 100L, 100L); // Adjust parameters as needed

        // Generate a unique event ID (you may want to implement this logic)
        newEvent.setEventId(currentOrganizer.getDeviceId() + "-" + System.currentTimeMillis()); // Simple unique ID generation

        System.out.println("event Name:" + newEvent.getName());
        System.out.println("event Description:" + newEvent.getDescription());
        System.out.println("event Date:" + newEvent.getStartDateTime());
        System.out.println("event id:" + newEvent.getEventId());

        // Insert the new event into the database
        database.insertEvent(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                // Add that the event is a created event for the organizer
                currentOrganizer.addEvent(newEvent);
                database.insertUserDocument(currentOrganizer);
                requireActivity().onBackPressed(); // Or navigate to another fragment
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, newEvent);

        return true;
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
