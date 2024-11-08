package com.example.trojanplanner.events;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button; // Import Button if it's a regular button

import androidx.fragment.app.Fragment;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.R;

import java.io.Serializable;

public class EventDetailsFragment extends Fragment {
    private static final String ARG_EVENT = "event";

    private Event event;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public static EventDetailsFragment newInstance(Event event) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, (Serializable) event);  // Passing the event object as an argument
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT); // Retrieve the event object
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        // Get references to the UI elements
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView availableSpotsTextView = view.findViewById(R.id.availableSpotsTextView);

        // Set data from the event object to the UI elements
        if (event != null) {
            // Set the image, if available, or default one
            Bitmap eventPicture = event.getPicture(requireContext());
            if (eventPicture != null) {
                eventImageView.setImageBitmap(eventPicture);
            } else {
                eventImageView.setImageResource(R.drawable.logo); // default image
            }

            eventNameTextView.setText(event.getName());
            eventDescriptionTextView.setText(event.getDescription());

            // Format the date for display
            String eventStartDate = event.getStartDateTime() != null ? event.getStartDateTime().toString() : "No start date available";
            eventDateTextView.setText("Start Date: " + eventStartDate);

            // Show available spots
            availableSpotsTextView.setText("Available Spots: " + event.getAvailableSpots());
        }

        // Set up the Button (event_options_button) click listener
        Button optionsButton = view.findViewById(R.id.event_options_button);
        if (optionsButton != null) {
            optionsButton.setOnClickListener(v -> showOptionsDialog());
        } else {
            Log.e("EventDetailsFragment", "Options button not found");
        }

        return view;
    }
    // Show options dialog when button is clicked
    private void showOptionsDialog() {
        // Show the EventOptionsDialogFragment
        EventOptionsDialogFragment optionsDialog = EventOptionsDialogFragment.newInstance(event);
        optionsDialog.show(getParentFragmentManager(), "EventOptionsDialog");
    }
}
