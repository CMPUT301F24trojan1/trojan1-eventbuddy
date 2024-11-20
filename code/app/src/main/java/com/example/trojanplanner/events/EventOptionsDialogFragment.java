package com.example.trojanplanner.events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.R;

import java.io.Serializable;

/**
 * A DialogFragment that provides a set of options for an event. The options include actions like
 * sending an announcement, viewing attendees, viewing the event's map, showing a check-in code,
 * generating an event code, or deleting the event.
 */
public class EventOptionsDialogFragment extends DialogFragment {

    private static final String ARG_EVENT = "event";
    private Event event;

    /**
     * Creates a new instance of the EventOptionsDialogFragment with the given event passed as an argument.
     *
     * @param event The event object for which options are to be displayed.
     * @return A new instance of EventOptionsDialogFragment.
     */
    public static EventOptionsDialogFragment newInstance(Event event) {
        EventOptionsDialogFragment fragment = new EventOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, (Serializable) event);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. Retrieves the event from the fragment arguments.
     *
     * @param savedInstanceState The saved instance state (if any).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the event from the arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        }
    }

    /**
     * Creates the dialog that displays event options.
     *
     * @param savedInstanceState The saved instance state (if any).
     * @return The dialog to be shown to the user.
     */
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create an AlertDialog to show the event options
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Event Options")
                .setItems(R.array.event_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleOptionSelection(which);
                    }
                });

        return builder.create();
    }

    /**
     * Handles the selection of an option from the event options dialog.
     *
     * @param optionIndex The index of the selected option.
     */
    private void handleOptionSelection(int optionIndex) {
        switch (optionIndex) {
            case 0:
                sendAnnouncement();
                break;
            case 1:
                viewAttendees();
                break;
            case 2:
                viewMap();
                break;
            case 3:
                showCheckinCode();
                break;
            case 4:
                generateEventCode();
                break;
            case 5:
                deleteEvent();
                break;
            default:
                break;
        }
    }

    /**
     * Logic to send an announcement for the event.
     */
    private void sendAnnouncement() {
        // Add your logic to send an announcement
        Toast.makeText(getContext(), "Send Announcement clicked", Toast.LENGTH_SHORT).show();
    }

    /**
     * Logic to view attendees of the event.
     */
    private void viewAttendees() {
        // Add your logic to view attendees
        Toast.makeText(getContext(), "View Attendees clicked", Toast.LENGTH_SHORT).show();
    }

    /**
     * Logic to view the event location on a map.
     */
    private void viewMap() {
        // Add your logic to view the event location on a map
        Toast.makeText(getContext(), "View Map clicked", Toast.LENGTH_SHORT).show();
    }

    /**
     * Logic to show the event's check-in code (e.g., generating a QR code).
     */
    private void showCheckinCode() {
        if (event == null || event.getEventId() == null) {
            Log.e("EventOptionsDialog", "Event or Event ID is null");
            Toast.makeText(requireContext(), "No event data available", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EventOptionsDialog", "Querying for Event ID: " + event.getEventId());

        // Use application context for toast messages
        Context appContext = requireContext().getApplicationContext();

        // Define success and failure actions
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                if (object instanceof Event) {
                    Event fetchedEvent = (Event) object;
                    String deviceId = fetchedEvent.getEventId(); // Assuming eventId is the device ID
                    Log.d("EventOptionsDialog", "Fetched Device ID: " + deviceId);

                    // Use application context for the toast
                    Toast.makeText(appContext, "Checkin Code: " + deviceId, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("EventOptionsDialog", "Unexpected data type received");
                    Toast.makeText(appContext, "Unexpected data received", Toast.LENGTH_SHORT).show();
                }
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                Log.e("EventOptionsDialog", "Query failed");
                Toast.makeText(appContext, "Failed to fetch Device ID", Toast.LENGTH_SHORT).show();
            }
        };

        // Execute the database query
        Database.getDB().getEvent(successAction, failureAction, event.getEventId());
    }

    /**
     * Logic to generate an event code for the event.
     */
    private void generateEventCode() {
        // Add your logic to generate an event code
        Toast.makeText(getContext(), "Generate Event Code clicked", Toast.LENGTH_SHORT).show();
    }

    /**
     * Logic to delete the event, potentially by calling an API or updating the database.
     */
    private void deleteEvent() {
        // Add your logic to delete the event
        Toast.makeText(getContext(), "Delete Event clicked", Toast.LENGTH_SHORT).show();
    }
}
