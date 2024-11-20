package com.example.trojanplanner.events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventDetailsDialogFragment extends DialogFragment {
    private Event event;
    private Entrant entrant;
    private Database database;
    private Button buttonEnterNow;

    @NonNull
    public static EventDetailsDialogFragment newInstance(Event event, Entrant entrant) {
        EventDetailsDialogFragment fragment = new EventDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        args.putSerializable("entrant", entrant);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the fragment with the event and entrant data passed in arguments.
     *
     * @param savedInstanceState The saved instance state (if any).
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            entrant = (Entrant) getArguments().getSerializable("entrant");
        }
        database = Database.getDB();
    }

    /**
     * Called to create the view for the fragment.
     * Initializes UI elements and populates the event details.
     *
     * @param inflater           The LayoutInflater to inflate the view.
     * @param container          The parent view group.
     * @param savedInstanceState The saved instance state (if any).
     * @return The view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_eventdetails, container, false);

        // Initialize views
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventLocationTextView = view.findViewById(R.id.eventLocationTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView recurringDatesTextView = view.findViewById(R.id.recurringDatesTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);

        // Initialize Buttons
        buttonEnterNow = view.findViewById(R.id.button_enter_now);
        // Populate event details
        if (event != null) {
            populateEventDetails(eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, eventDescriptionTextView);
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }

        // Set button click listeners
        buttonEnterNow.setOnClickListener(v -> joinWaitlist());

        return view;
    }

    /**
     * Shows a confirmation dialog for the entrant to join the event's waitlist.
     * If confirmed, the entrant will be added to the event's waitlist.
     */
    public void joinWaitlist() {
        if (event != null && App.currentUser != null) {
            // Cast the current user to Entrant
            Entrant currentEntrant = (Entrant) App.currentUser;

            // Ensure the event's waiting list is initialized
            if (event.getWaitingList() == null) {
                event.setWaitingList(new ArrayList<>());
            }
            Log.d("EventDetails", "Current Event Waitlist before changes " + event.getWaitingList());

            ArrayList<User> waitingList = event.getWaitingList();

            // Add the entrant to the waiting list if not already present
            if (!waitingList.contains(currentEntrant)) {
                waitingList.add(currentEntrant);
                event.setWaitingList(waitingList);

                // Log the addition
                Log.d("EventDetails", "Entrant " + currentEntrant.getDeviceId() +
                        " successfully added to the waitlist for event " + event.getEventId());
                Log.d("EventDetails", "Current Event Waitlist: " + waitingList);
                Log.d("EventDetails", "Current Entrant Waitlisted Events: " + currentEntrant.getCurrentWaitlistedEvents());

                // Update the database
                database.insertEvent(event); // Save the updated event
                database.insertUserDocument(currentEntrant); // Save the updated entrant

                // Notify the user and update UI
                Toast.makeText(getContext(), "Added to waitlist", Toast.LENGTH_SHORT).show();
                checkEntrantStatus(); // Refresh the UI
            } else {
                Log.d("EventDetails", "Entrant " + currentEntrant.getDeviceId() +
                        " is already on the waitlist for event " + event.getEventId());
                Toast.makeText(getContext(), "You are already on the waitlist.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEntrantStatus() {
        if (event != null && event.getWaitingList() != null) {
            if (event.getWaitingList().contains(entrant)) {
                buttonEnterNow.setVisibility(View.GONE);
            } else {
                buttonEnterNow.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("EventDetailsFragment", "Event or waiting list is null in checkEntrantStatus");
        }
    }

    /**
     * Populates the event details in the respective text views.
     * If event details are missing, default values will be shown.
     *
     * @param eventNameTextView        The TextView to display the event's name.
     * @param eventLocationTextView    The TextView to display the event's location.
     * @param eventDateTextView        The TextView to display the event's start and end date.
     * @param recurringDatesTextView   The TextView to display the event's recurrence days.
     * @param eventDescriptionTextView The TextView to display the event's description.
     */
    public void populateEventDetails(TextView eventNameTextView, TextView eventLocationTextView,
                                     TextView eventDateTextView, TextView recurringDatesTextView,
                                     TextView eventDescriptionTextView) {

        eventNameTextView.setText(event.getName());
        if (event.getFacility() != null) {
            eventLocationTextView.setText(event.getFacility().getFacilityId());
        }

        // Default values for dates in case they are null
        String defaultDate = "Not Available";  // Default date if event date is null
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Assign default value if startDateTime or endDateTime is null
        String startDate = (event.getStartDateTime() != null) ? dateFormat.format(event.getStartDateTime()) : defaultDate;
        String endDate = (event.getEndDateTime() != null) ? dateFormat.format(event.getEndDateTime()) : defaultDate;

        eventDateTextView.setText(startDate + " - " + endDate);

        // Convert abbreviations in recurrenceDays to full day names
        ArrayList<String> recurrenceDays = event.getRecurrenceDays();

        if (recurrenceDays != null) {
            String recurrenceDaysText = recurrenceDays.stream()
                    .map(this::getFullDayName) // Convert each unique abbreviation to full day name
                    .filter(name -> !name.isEmpty()) // Filter out any invalid/missing conversions
                    .reduce((a, b) -> a + ", " + b) // Join with commas
                    .orElse("No recurrence");

            recurringDatesTextView.setText(recurrenceDaysText);
        }
        eventDescriptionTextView.setText(event.getDescription());

    }

    /**
     * Converts a short abbreviation (e.g., "M" for Monday) to a full day name.
     *
     * @param abbreviation The abbreviation for the day of the week.
     * @return The full day name (e.g., "Monday").
     */
    // Helper method to get the full name for the day of the week based on unique abbreviation
    private String getFullDayName(String abbreviation) {
        switch (abbreviation) {
            case "U": return "Sunday";
            case "M": return "Monday";
            case "T": return "Tuesday";
            case "W": return "Wednesday";
            case "R": return "Thursday";
            case "F": return "Friday";
            case "S": return "Saturday";
            default: return ""; // Handle invalid abbreviations
        }
    }


}
