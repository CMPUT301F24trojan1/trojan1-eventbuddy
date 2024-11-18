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

        // Show the "no events" message
        showNoEventsMessage();

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

        // Check if the user is already an organizer
        if (App.currentUser != null) {
            if (App.currentUser.isOrganizer()) {
                // If the user is an organizer, show the "Create Event" button and hide "Become Organizer" button
                view.findViewById(R.id.createEventButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.becomeOrganizerButton).setVisibility(View.GONE);

                // Set up a click listener for the "Create Event" button
                view.findViewById(R.id.createEventButton).setOnClickListener(v -> {
                    // Navigate to the Create Event fragment
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_eventsFragment_to_eventEditFragment);
                });
            } else {
                // If the user is not an organizer, show the "Become Organizer" button
                view.findViewById(R.id.becomeOrganizerButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.createEventButton).setVisibility(View.GONE);

                // Set up a click listener for the "Become Organizer" button
                view.findViewById(R.id.becomeOrganizerButton).setOnClickListener(v -> {
                    // Navigate to the Facility Setup fragment
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_emptyEventsFragment_to_facilitySetupFragment);
                });
            }
        } else {
            // If the user is not logged in or no user exists, hide both buttons
            view.findViewById(R.id.becomeOrganizerButton).setVisibility(View.GONE);
            view.findViewById(R.id.createEventButton).setVisibility(View.GONE);
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
