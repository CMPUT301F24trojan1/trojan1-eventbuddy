package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.notifications.NotificationManager;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.util.List;

public class NotificationSenderFragment extends Fragment {

    private Spinner spinnerNotificationType;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private Button buttonSendNotification;

    private final NotificationManager notificationManager = new NotificationManager();
    private Event event; // Event object passed to this fragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_notification_sender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        spinnerNotificationType = view.findViewById(R.id.spinnerNotificationType);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSendNotification = view.findViewById(R.id.buttonSendNotification);

        // Retrieve the event object from arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }

        // Set up button click listener
        buttonSendNotification.setOnClickListener(v -> sendNotification());
    }

    /**
     * Handle sending the notification.
     */
    private void sendNotification() {
        if (event == null) {
            Toast.makeText(getContext(), "No event data available!", Toast.LENGTH_SHORT).show();
            return;
        }

        String notificationType = spinnerNotificationType.getSelectedItem().toString();
        String title = editTextTitle.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(getContext(), "Title and message cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine the list of users based on the notification type
        List<User> users;
        switch (notificationType) {
            case "Current Waitlist":
                users = event.getWaitingList(); // Get the waitlist users from the event
                Toast.makeText(getContext(), "Notifying Entrants on the waiting list.", Toast.LENGTH_SHORT).show();
                break;
            case "Selected Entrants":
                users = event.getEnrolledList(); // Get the selected users from the event
                Toast.makeText(getContext(), "Notifying Selected Entrants.", Toast.LENGTH_SHORT).show();
                break;
            case "Cancelled Entrants":
                users = event.getCancelledList(); // Get the cancelled users from the event
                Toast.makeText(getContext(), "Notifying Cancelled Entrants.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), "Invalid notification type selected.", Toast.LENGTH_SHORT).show();
                return;
        }

        if (users == null || users.isEmpty()) {
            Toast.makeText(getContext(), "No users found for this notification type.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Notify each user's device ID
        for (User user : users) {
            String deviceID = user.getDeviceId();
            if (deviceID != null && !deviceID.isEmpty()) {
                App.sendAnnouncement("organizer" + deviceID, title, message);
            } else {
                Toast.makeText(getContext(), "User with missing device ID skipped.", Toast.LENGTH_SHORT).show();
            }
        }

        // Show confirmation
        Toast.makeText(getContext(), "Notification sent successfully to " + users.size() + " users!", Toast.LENGTH_SHORT).show();

        // Clear inputs
        editTextTitle.setText("");
        editTextMessage.setText("");
    }
}
