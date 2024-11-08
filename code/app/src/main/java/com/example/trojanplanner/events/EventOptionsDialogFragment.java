package com.example.trojanplanner.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.R;

import java.io.Serializable;

//organizer
public class EventOptionsDialogFragment extends DialogFragment {

    private static final String ARG_EVENT = "event";
    private Event event;

    // This method creates a new instance of the dialog with the event passed as argument.
    public static EventOptionsDialogFragment newInstance(Event event) {
        EventOptionsDialogFragment fragment = new EventOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, (Serializable) event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the event from the arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        }
    }

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

    // Handle the user's selection from the dialog
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

    // Logic for sending an announcement
    private void sendAnnouncement() {
        // Add your logic to send an announcement
        Toast.makeText(getContext(), "Send Announcement clicked", Toast.LENGTH_SHORT).show();
    }

    // Logic for viewing attendees
    private void viewAttendees() {
        // Add your logic to view attendees
        Toast.makeText(getContext(), "View Attendees clicked", Toast.LENGTH_SHORT).show();
    }

    // Logic for viewing map
    private void viewMap() {
        // Add your logic to view the event location on a map
        Toast.makeText(getContext(), "View Map clicked", Toast.LENGTH_SHORT).show();
    }

    // Logic for showing check-in code
    private void showCheckinCode() {
        // Add your logic to show check-in code (e.g., generating QR code)
        Toast.makeText(getContext(), "Check-in Code clicked", Toast.LENGTH_SHORT).show();
    }

    // Logic for generating event code
    private void generateEventCode() {
        // Add your logic to generate an event code
        Toast.makeText(getContext(), "Generate Event Code clicked", Toast.LENGTH_SHORT).show();
    }

    // Logic for deleting the event
    private void deleteEvent() {
        // Add your logic to delete the event (e.g., calling an API, updating database)
        Toast.makeText(getContext(), "Delete Event clicked", Toast.LENGTH_SHORT).show();
    }
}
