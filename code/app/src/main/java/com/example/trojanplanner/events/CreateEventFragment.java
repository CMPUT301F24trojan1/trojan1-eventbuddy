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

        createEventButton.setOnClickListener(v -> {
            boolean ret = createEvent(view);
            if (ret) {
                navigateToEventsListFragment(view);
            }
        });
    }

    /**
     * Attempts to create the event and returns true if creation succeeds.
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

        Entrant currentUser = ((MainActivity) App.activityManager.getActivity()).currentUser;
        Organizer currentOrganizer = currentUser.returnOrganizer();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Organizer not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Assuming default values for event date fields
        Date startDateTime = new Date();
        Date endDateTime = new Date();

        Event newEvent = new Event(name, description, price, null, startDateTime, endDateTime, 30, 100L, 100L);
        newEvent.setEventId(currentOrganizer.getDeviceId() + "-" + System.currentTimeMillis());

        // Insert the new event into the database
        database.insertEvent(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (isAdded()) {
                    Toast.makeText(App.activityManager.getActivity(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                    currentOrganizer.addEvent(newEvent);
                    database.insertUserDocument(currentOrganizer);
                    navigateToEventsListFragment(view);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (isAdded()) {
                    Toast.makeText(App.activityManager.getActivity(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
