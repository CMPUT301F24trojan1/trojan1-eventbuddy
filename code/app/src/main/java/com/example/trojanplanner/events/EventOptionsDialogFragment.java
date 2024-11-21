package com.example.trojanplanner.events;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.User;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                String eventId = event.getEventId();
                String message = "You're getting this because you expressed interest for this event!";
                String title = "Announcement Title";
                sendAnnouncement(eventId, title, message);
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
    private void sendAnnouncement(String eventId, String title, String message) {
        if (eventId == null || title == null || message == null) {
            Toast.makeText(getContext(), "Invalid event details. Cannot send announcement.", Toast.LENGTH_SHORT).show();
            return;
        }

        String channelId = "EventChannel_" + eventId; // Use the eventId to identify the channel

        // Create a Notification Manager
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8.0+ (Oreo and above), create a notification channel dynamically
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    "Event Updates for " + eventId,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setDescription("Notifications for updates on event " + eventId);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.drawable.logo) // Replace with your app's notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(eventId.hashCode(), notificationBuilder.build()); // Use eventId's hash as a unique ID

        // Store the announcement in Firestore (optional, for record-keeping)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("eventId", eventId);
        announcement.put("title", title);
        announcement.put("message", message);
        announcement.put("timestamp", FieldValue.serverTimestamp());

        db.collection("announcements")
                .add(announcement)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Announcement sent and saved to Firestore.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    /**
     * Logic to view attendees of the event.
     */
    private void viewAttendees() {
        if (event != null && event.getWaitingList() != null) {
            ArrayList<User> waitingList = event.getWaitingList();

            if (!waitingList.isEmpty()) {
                // Build a string with the list of waiting attendees
                StringBuilder attendeesList = new StringBuilder("Waiting List:\n");
                for (User user : waitingList) {
                    attendeesList.append(user.getFirstName()).append("\n");
                }

                // Display the waiting list in a dialog
                new AlertDialog.Builder(getContext())
                        .setTitle("Event Waiting List")
                        .setMessage(attendeesList.toString())
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(getContext(), "No attendees in the waiting list.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Event data is missing or invalid.", Toast.LENGTH_SHORT).show();
        }
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
        // Add your logic to show check-in code
        Toast.makeText(getContext(), "Check-in Code clicked", Toast.LENGTH_SHORT).show();
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
