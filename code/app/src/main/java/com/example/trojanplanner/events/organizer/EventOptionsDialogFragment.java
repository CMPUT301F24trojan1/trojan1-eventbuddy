package com.example.trojanplanner.events.organizer;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.HelperFragments.WaitlistFragment;
import com.example.trojanplanner.QRUtils.QRCodeUtil;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.User;

import java.io.Serializable;
import java.util.ArrayList;

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
                viewCancelled();
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
            case 6: // View Waitlist
                viewWaitlist();
                break;
            case 7: // Initiate Lottery
                initiateLottery();
                break;
            case 8:
                viewSelected();
                break;
            default:
                break;
        }
    }

    private void viewSelected() {
        Bundle args = new Bundle();
        event.setWaitingList(event.getEnrolledList());
        args.putSerializable("event", event);

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
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
    }

    /**
     * Logic to view attendees of the event.
     */
    private void viewCancelled() {
        Bundle args = new Bundle();
        event.setWaitingList(event.getCancelledList());
        args.putSerializable("event", event);

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
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

        String eventId = event.getEventId();
        Log.d("EventOptionsDialog", "Fetched Event ID: " + eventId);

        // Hash the event ID
        String hashedEventId = QRCodeUtil.hashText(eventId);
        if (hashedEventId == null) {
            Log.e("EventOptionsDialog", "Failed to hash Event ID.");
            Toast.makeText(requireContext(), "Failed to process Event ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EventOptionsDialog", "Hashed Event ID: " + hashedEventId);

        // Generate QR code bitmap
        Bitmap qrCodeBitmap = QRCodeUtil.generateQRCode(hashedEventId);
        if (qrCodeBitmap == null) {
            Log.e("EventOptionsDialog", "QR Code generation failed.");
            Toast.makeText(requireContext(), "QR Code generation failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display QR Code in a dialog
        showQRCodeInDialog(qrCodeBitmap);
    }

    private void generateEventCode() {
        if (event == null || event.getEventId() == null) {
            Log.e("EventOptionsDialog", "Event or Event ID is null");
            Toast.makeText(requireContext(), "No event data available to generate event code.", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = event.getEventId();
        Log.d("EventOptionsDialog", "Generating event code for Event ID: " + eventId);

        // Hash the event ID
        String hashedEventId = QRCodeUtil.hashText(eventId);
        if (hashedEventId == null) {
            Log.e("EventOptionsDialog", "Failed to hash Event ID.");
            Toast.makeText(requireContext(), "Failed to generate event code.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EventOptionsDialog", "Generated Hashed Event ID: " + hashedEventId);

        // Upload the hashed event ID to the database
        uploadQRHashToDatabase(hashedEventId, event);
    }

    private void uploadQRHashToDatabase(String qrHash, Event event) {
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                Log.d("EventOptionsDialog", "QR hash successfully uploaded to database.");
                Toast.makeText(requireContext(), "Event code saved successfully.", Toast.LENGTH_SHORT).show();
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                Log.e("EventOptionsDialog", "Failed to upload QR hash to database.");
                Toast.makeText(requireContext(), "Failed to save event code to database.", Toast.LENGTH_SHORT).show();
            }
        };

        Database.getDB().insertQRHash(qrHash, event);
    }

    private void showQRCodeInDialog(Bitmap qrCodeBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Create an ImageView for the QR Code
        ImageView qrCodeImageView = new ImageView(requireContext());
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
        qrCodeImageView.setPadding(50, 50, 50, 50); // Optional: add padding for better display

        // Configure the dialog
        builder.setTitle("Check-In QR Code")
                .setView(qrCodeImageView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

        Log.d("EventOptionsDialog", "QR Code displayed in dialog successfully.");
    }


    /**
     * Logic to delete the event, potentially by calling an API or updating the database.
     */
    private void deleteEvent() {
        // Add your logic to delete the event
        Toast.makeText(getContext(), "Delete Event clicked", Toast.LENGTH_SHORT).show();
    }

    private void viewWaitlist() {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
    }


    private void initiateLottery() {
        if (event == null || event.getWaitingList() == null || event.getWaitingList().isEmpty()) {
            Toast.makeText(requireContext(), "Cannot initiate a lottery. Waitlist is empty or missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<User> waitlist = event.getWaitingList();

        // Simple random lottery logic
        int winnerIndex = (int) (Math.random() * waitlist.size());
        User winner = waitlist.get(winnerIndex);

        // Notify the winner and log
        String winnerMessage = "Congratulations! " + winner.getFirstName() + " " + winner.getLastName() + " has won the lottery.";
        Log.d("EventOptionsDialog", "Lottery Winner: " + winnerMessage);

        new AlertDialog.Builder(requireContext())
                .setTitle("Lottery Winner")
                .setMessage(winnerMessage)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

        // Optionally, update the event or database with the winner's details
        // database.updateLotteryWinner(event.getEventId(), winner);
    }
}
