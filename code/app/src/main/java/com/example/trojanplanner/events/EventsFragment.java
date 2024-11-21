package com.example.trojanplanner.events;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.EventArrayAdapter;
import com.example.trojanplanner.databinding.FragmentEventsListBinding;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Fragment that displays a list of events using a RecyclerView.
 * <p>
 * The fragment loads events, either from a database or dummy data if none are available, and presents
 * them in a scrollable list. When an event is clicked, the user is navigated to an editing screen.
 */
public class EventsFragment extends Fragment implements EventArrayAdapter.OnEventClickListener {

    private FragmentEventsListBinding binding;
    private EventArrayAdapter eventsAdapter;
    private List<Event> eventList;
    private User currentUser;

    // SharedPreferences key to track first-time user
    private static final String PREF_FIRST_TIME_VISIT = "first_time_visit";

    /**
     * Called to create the view for this fragment. Initializes the RecyclerView, adapter, and loads the events.
     *
     * @param inflater The LayoutInflater used to inflate the fragment's layout.
     * @param container The parent view that contains the fragment's UI.
     * @param savedInstanceState A bundle containing saved state data (if any).
     * @return The view for the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Disable the back button in the app bar
        if (getActivity() != null) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }

        // Initialize the RecyclerView
        RecyclerView recyclerView = binding.eventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the event list and adapter
        eventList = new ArrayList<>();
        eventsAdapter = new EventArrayAdapter(App.activity, eventList, this);
        recyclerView.setAdapter(eventsAdapter);

        // Initialize database instance
        Database.getDB();

        // Fetch the current user
        currentUser = App.currentUser; // This method needs to return the logged-in user

        Button createEventOrBecomeOrganizerButton = root.findViewById(R.id.createEvent_or_become_an_organizer_button);

        // Set button text and navigation based on user's role
        setButton(createEventOrBecomeOrganizerButton);

        // Load events from the database
        loadEventsFromDatabase();

        // Show first-time visit dialog
        checkFirstTimeVisit();
        return root;
    }

    private void setButton(Button createEventOrBecomeOrganizerButton) {
        // Check if the user is logged in and update the button visibility based on their role
        if (currentUser != null) {
            if (currentUser.isOrganizer()) {
                // If the user is an organizer, show "Create Event" option
                createEventOrBecomeOrganizerButton.setVisibility(View.VISIBLE);
                createEventOrBecomeOrganizerButton.setText("Create an Event");

                // Set up click listener for the "Create Event" button
                createEventOrBecomeOrganizerButton.setOnClickListener(v -> {
                    // Navigate to the Event Edit Fragment
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_eventsListFragment_to_eventEditFragment);
                });
            } else {
                // If the user is not an organizer, show "Become an Organizer" option
                createEventOrBecomeOrganizerButton.setVisibility(View.VISIBLE);
                createEventOrBecomeOrganizerButton.setText("Become an Organizer");

                // Set up click listener for the "Become Organizer" button
                createEventOrBecomeOrganizerButton.setOnClickListener(v -> {
                    // Navigate to the Facility Setup Fragment
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_eventsListFragment_to_facilitySetupFragment);
                });
            }
        } else {
            // If the user is not logged in or no user exists, hide the button
            createEventOrBecomeOrganizerButton.setVisibility(View.GONE);
        }
    }

    private void checkFirstTimeVisit() {
        // Get SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Check if it's the first time visiting the fragment
        boolean isFirstTime = sharedPreferences.getBoolean(PREF_FIRST_TIME_VISIT, true);

        if (isFirstTime) {
            // Show the dialog
            showFirstTimeDialog();

            // Update SharedPreferences to mark that the user has visited the fragment
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_FIRST_TIME_VISIT, false);  // Set flag to false after first visit
            editor.apply();
        }
    }

    private void showFirstTimeDialog() {
        // Create an AlertDialog
        new AlertDialog.Builder(getContext())
                .setTitle("Your Events will always show up here")
                .setMessage("Click on an Event to view details")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss dialog
                    }
                })
                .setCancelable(false)  // Prevent dialog from being dismissed by tapping outside
                .show();
    }

    private void loadEventsFromDatabase() {
        eventList.clear();



        if (App.currentUser == null || App.currentUser.getDeviceId() == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        // Get current user ID (assuming App.currentUser holds this information)
        String userId = App.currentUser.getDeviceId();

        // Call getAllEventsFromDeviceId method to get events based on the user device ID
        Database.getDB().getAllEventsFromDeviceId(new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                // This will be triggered when the events are successfully retrieved
                ArrayList<Event> events = (ArrayList<Event>) object;
                if (events != null && !events.isEmpty()) {
                    eventList.addAll(events);
                    System.out.println("Loaded events: " + events.size());
                } else {
                    System.out.println("No events found for this user.");
                }
                // Notify the adapter that the event list has been updated
                eventsAdapter.notifyDataSetChanged();
            }
        }, new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                // This will be triggered if there's an error retrieving the events
                System.out.println("Failed to load events.");
            }
        }, userId);  // Pass the userId to get events related to the current device/user
    }

    /**
     * Called when an event in the list is clicked. It navigates the user to the EventEditFragment where the
     * event can be edited.
     *
     * @param event The event that was clicked.
     */
    @Override
    public void onEventClick(Event event) {
        System.out.println("Event clicked: " + event.getName());

        // Use NavController to navigate to EventEditFragment
        NavController navController = Navigation.findNavController(requireView());

        // Prepare the arguments
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);  // Pass the event data to the fragment

        // Navigate to EventEditFragment
        navController.navigate(R.id.action_eventsListFragment_to_eventDetailsFragment, bundle);
    }
}



/* can be used to test easily
*     private void addDummyEvent() {
        Event dummyEvent = new Event(
                "fakeId",
                "Sample Event",
                "This is a description for the sample event.",
                0.0f, // Price
                null, // Facility (can be null or a placeholder)
                new Date(), // Start date
                new Date(), // End date
                10, // Total spots
                10L, // Available spots
                0L  // Registered participants
        );

        dummyEvent.setEventId("dummy_event_id");
        eventList.add(dummyEvent);
        eventsAdapter.notifyDataSetChanged();
    }
* */