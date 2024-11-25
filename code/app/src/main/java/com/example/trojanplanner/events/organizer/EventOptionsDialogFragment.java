package com.example.trojanplanner.events.organizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.QRUtils.QRCodeUtil;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.R;
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
    @NonNull
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
                showCheckinCode();
                break;
            case 1:
                generateEventCode();
                break;
            case 2:
                sendAnnouncement(event);
                break;
            case 3: // View Waitlist
                viewWaitlist();
                break;
            case 4:
                viewSelected();
                break;
            case 5:
                viewCancelled();
                break;
            case 6:
                viewMap();
                break;
            case 7: // Initiate Lottery
                initiateLottery();
                break;
            case 8: // edit the event information and the poster
                editEvent();
                break;
            case 9:
                deleteEvent();
                break;
            default:
                break;
        }
    }

    //TO DO
    private void editEvent() {
        Toast.makeText(getContext(), "Edit Event clicked", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles sending Announcements to different user lists
     *
     * @param event The event to send announcements for
     */
    private void sendAnnouncement(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.NotificationSenderFragment, args);
    }

    /**
     * Logic to view waitlists of the event.
     */
    private void viewWaitlist() {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
    }

    /**
     * Logic to view enrolled attendees of the event.
     */
    private void viewSelected() {
        Bundle args = new Bundle();
        event.setWaitingList(event.getEnrolledList());
        args.putSerializable("event", event);

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
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
     * Logic for showing the event's current QR code
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

    /**
     * Logic for generating a QR code).
     */
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