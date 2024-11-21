package com.example.trojanplanner.events;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * A Fragment that is displayed when there are no events available for the user.
 * <p>
 * This fragment displays a message to the user indicating that there are no events, and it provides
 * options for the user to either create a new event or set up a facility to become an event organizer.
 * The fragment includes buttons that navigate to the corresponding screens.
 */
public class EmptyEventsFragment extends Fragment {

    // TextView to display a message about no events
    private TextView messageTextView;
    private View loadingIndicator; // Optional loading indicator to show while data is loading

    /**
     * Called to create the view for this fragment. Inflates the layout and initializes the message view.
     *
     * @param inflater The LayoutInflater used to inflate the fragment's layout.
     * @param container The parent view that contains the fragment's UI.
     * @param savedInstanceState A bundle containing saved state data (if any).
     * @return The view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_empty_myevents, container, false);

        // Initialize the TextView to show the no events message
        messageTextView = view.findViewById(R.id.messageTextView);
        loadingIndicator = view.findViewById(R.id.loadingIndicator); // Initialize loading indicator

        // Initially hide the content and show the loading indicator
        loadingIndicator.setVisibility(View.VISIBLE);
        messageTextView.setVisibility(View.GONE);

        return view;
    }

    /**
     * Called after the fragment's view has been created. Sets up click listeners for the buttons that allow
     * the user to either become an event organizer or create a new event.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState A bundle containing saved state data (if any).
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if the user is logged in
        if (App.currentUser != null) {
            String deviceId = App.currentUser.getDeviceId();

            // Fetch all events for the current user using the new query method
            Database.getDB().getAllEventsFromDeviceId(new Database.QuerySuccessAction() {
                @Override
                public void OnSuccess(Object object) {
                    List<Event> events = (List<Event>) object;
                    System.out.println(events);

                    // Hide loading indicator and show message or navigate based on the result
                    loadingIndicator.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);

                    if (events != null && !events.isEmpty()) {
                        // Navigate to EventsListFragment if there are any events
                        NavController navController = NavHostFragment.findNavController(EmptyEventsFragment.this);
                        navController.navigate(R.id.action_emptyEventsFragment_to_eventsListFragment);
                    } else {
                        // No events found; display the "no events" message
                        showNoEventsMessage();
                    }
                }
            }, new Database.QueryFailureAction() {
                @Override
                public void OnFailure() {
                    // Hide loading indicator and show error message
                    loadingIndicator.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "Failed to load events. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }, deviceId);
        }

        // Set up the buttons based on user role
        setupButtons(view);
    }

    private void setupButtons(View view) {
        // Check if the user is logged in
        if (App.currentUser != null) {
            if (App.currentUser.isOrganizer()) {
                // Show the "Create Event" button for organizers
                view.findViewById(R.id.createEventButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.becomeOrganizerButton).setVisibility(View.GONE);

                // Set click listener for the "Create Event" button
                view.findViewById(R.id.createEventButton).setOnClickListener(v -> {
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_eventsFragment_to_eventEditFragment);
                });
            } else {
                // Show the "Become Organizer" button for non-organizers
                view.findViewById(R.id.becomeOrganizerButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.createEventButton).setVisibility(View.GONE);

                // Set click listener for the "Become Organizer" button
                view.findViewById(R.id.becomeOrganizerButton).setOnClickListener(v -> {
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_emptyEventsFragment_to_facilitySetupFragment);
                });
            }
        } else {
            // If no user is logged in, hide both buttons
            view.findViewById(R.id.createEventButton).setVisibility(View.GONE);
            view.findViewById(R.id.becomeOrganizerButton).setVisibility(View.GONE);
        }
    }


    /**
     * Displays a message to the user when there are no events available.
     */
    private void showNoEventsMessage() {
        // Set the message text to inform the user that there are no events
        messageTextView.setText("No events found. Come back here to see events you've created or joined!");
    }
}
