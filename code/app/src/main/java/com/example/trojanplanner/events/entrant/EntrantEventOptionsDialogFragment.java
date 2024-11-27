package com.example.trojanplanner.events.entrant;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A DialogFragment for entrants to manage event options.
 */
public class EntrantEventOptionsDialogFragment extends DialogFragment {

    private static final String ARG_EVENT = "event";
    private Event event;
    private boolean isSubscribed = true; // Assume the user is subscribed by default

    /**
     * Creates a new instance of EntrantEventOptionsDialogFragment with the event passed as an argument.
     *
     * @param event The event object to manage.
     * @return A new instance of EntrantEventOptionsDialogFragment.
     */
    public static EntrantEventOptionsDialogFragment newInstance(Event event) {
        EntrantEventOptionsDialogFragment fragment = new EntrantEventOptionsDialogFragment();
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
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        }
    }

    /**
     * Creates the dialog that displays the entrant's event options.
     *
     * @param savedInstanceState The saved instance state (if any).
     * @return The dialog to be shown to the user.
     */
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Event Options")
                .setItems(R.array.entrant_event_options, (dialog, which) -> {
                    switch (which){
                        case 0:
                            toggleNotificationPreference();
                            break;
                        case 1:
                            leaveWaitlist();
                    }
                });

        return builder.create();
    }

    private void leaveWaitlist(){
        if (event.getWaitingList() == null || event.getWaitingList().isEmpty()
                || ((Entrant)App.currentUser).getCurrentWaitlistedEvents() == null
                || ((Entrant)App.currentUser).getCurrentWaitlistedEvents().isEmpty())
        {
            Toast.makeText(getContext(), "No waitlist found", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<User> waitlist = event.getWaitingList();
        ArrayList<Event> waitlistEvents = ((Entrant)App.currentUser).getCurrentWaitlistedEvents();
        boolean userRemoved = false;
        boolean eventRemoved = false;
        for (User entrant: waitlist) {
            if (entrant.getDeviceId().equals(App.deviceId)){
                waitlist.remove((entrant));
                event.setWaitingList(waitlist);
                Database.getDB().insertEvent(event);
                userRemoved = true;
                break;
            }
        }

        for (Event waitlistedEvent: waitlistEvents){
            if (waitlistedEvent.getEventId().equals(event.getEventId())){
                waitlistEvents.remove(waitlistedEvent);
                Database.getDB().insertUserDocument(App.currentUser);
                eventRemoved = true;
                break;
            }
        }

        if (userRemoved && eventRemoved) {
            Toast.makeText(getContext(), "You have left the waitlist.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to leave the waitlist.", Toast.LENGTH_SHORT).show();
            return;
        }

        App.sendAnnouncement(App.currentUser.getDeviceId(), event.getName(), "You have left the waitlist.");
        // Dismiss the dialog and navigate up
        dismiss();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * Toggles the notification subscription for the event.
     */
    private void toggleNotificationPreference() {
        if (event == null) {
            Toast.makeText(getContext(), "Event data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = event.getEventId();

        if (isSubscribed) {
            // Unsubscribe from the event's topic
            FirebaseMessaging.getInstance().unsubscribeFromTopic(eventId)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            isSubscribed = false;
                            Log.d("Notifications", "Successfully unsubscribed from notifications for event: " + eventId);
                        } else {
                            Log.e("Notifications", "Failed to unsubscribe from notifications: " + task.getException());
                            Toast.makeText(getContext(), "Failed to unsubscribe.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Subscribe to the event's topic
            FirebaseMessaging.getInstance().subscribeToTopic(eventId)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            isSubscribed = true;
                            Log.d("Notifications", "Successfully subscribed for event: " + eventId);
                        } else {
                            Log.e("Notifications", "Failed to subscribe to notifications: " + task.getException());
                            Toast.makeText(getContext(), "Failed to subscribe.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}