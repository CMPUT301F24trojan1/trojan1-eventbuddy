package com.example.trojanplanner.events.entrant;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Event;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;

/**
 * A DialogFragment for entrants to manage event options, specifically notification preferences.
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
                    if (which == 0) {
                        toggleNotificationPreference();
                    }
                });

        return builder.create();
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
                            Toast.makeText(getContext(), "Unsubscribed from event notifications.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "Subscribed to event notifications.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Notifications", "Failed to subscribe to notifications: " + task.getException());
                            Toast.makeText(getContext(), "Failed to subscribe.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
