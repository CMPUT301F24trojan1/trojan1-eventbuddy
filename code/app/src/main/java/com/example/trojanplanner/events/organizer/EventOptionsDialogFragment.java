package com.example.trojanplanner.events.organizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
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
        args.putSerializable(ARG_EVENT, event);
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
        Database.getDB().getEvent(object -> {
            event = (Event) object;
            Log.d("EventOptionsDialog", "Fetched Event: " + event.getName());
        }, () -> {
            Log.e("EventOptionsDialog", "Failed to fetch event.");
        }, event.getEventId());

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

    private void navigateToPendingList() {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.PendingListFragment, args);
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
    private void sendAnnouncementPage(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.NotificationSenderFragment, args);
    }

    /**
     * Logic to view waitlists of the event.
     */
    private void viewWaitlist(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        String listType = "waiting";
        args.putString("listType", listType);

        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
    }

    /**
     * Logic to view enrolled attendees of the event.
     */
    private void viewEnrolled(Event event) {
        Bundle args = new Bundle();
        // Add the event and list type as arguments
        args.putSerializable("event", event);
        String listType = "enrolled";
        args.putString("listType", listType);

        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
    }

    /**
     * Logic to view attendees of the event.
     */
    private void viewCancelled(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        String listType = "cancelled";
        args.putString("listType", listType);

        Log.d("EventOptionsDialog", "Cancelled List: " + event.getCancelledList());

        // Use NavController from the parent fragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
    }

    private void initiateLottery() {
        ArrayList<User> Lottery_waitlist = new ArrayList<>(event.getWaitingList()); // Copy to avoid modifying the original list

        // Show a popup dialog to specify the number of attendees to select
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Number of Attendees, " + Lottery_waitlist.size() + " available");

        final EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER); // Restricting inputs to numbers
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String inputText = input.getText().toString();
            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            // This is just a invalid input checker
            int numUsersToSelect;
            try {
                numUsersToSelect = Integer.parseInt(inputText);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid number entered.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (numUsersToSelect > Lottery_waitlist.size()) {
                Toast.makeText(requireContext(), "Not enough attendees on the waitlist.", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<User> selectedAttendees = new ArrayList<>();
            for (int i = 0; i < numUsersToSelect; i++) {
                int randomIndex = (int) (Math.random() * Lottery_waitlist.size());
                User selectedUser = Lottery_waitlist.remove(randomIndex); // Remove to prevent duplicates
                selectedAttendees.add(selectedUser);
            }

            for (User winner : selectedAttendees) {
                Lottery_waitlist.add(winner);
                event.setPendingList(Lottery_waitlist); // Add to the pending list
                Log.d("InitiateLottery", "Winner selected: " + winner.getFirstName() + " " + winner.getLastName());
            }

            // Notify the organizer
            String successMessage = numUsersToSelect + " attendee(s) successfully registered.";
            Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();

            new AlertDialog.Builder(requireContext())
                    .setTitle("Lottery Winners")
                    .setMessage("The following attendees have been selected:\n" +
                            selectedAttendees.stream()
                                    .map(user -> user.getFirstName() + " " + user.getLastName())
                                    .reduce("", (a, b) -> a + "\n" + b))
                    .setPositiveButton("OK", (winnerDialog, whichWinner) -> winnerDialog.dismiss())
                    .create()
                    .show();

            event.setPendingList(Lottery_waitlist);
            Database.getDB().insertEvent(event);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public void leaveWaitlist() {
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

    /**
     * Logic to view the event location on a map.
     */
    private void viewMap(Event event) {
        // Show a Toast message
        Toast.makeText(getContext(), "View Map clicked", Toast.LENGTH_SHORT).show();

        // Create a Bundle and put the Event object inside it
        Bundle args = new Bundle();
        args.putSerializable("event", event);  // Put the event object as a Parcelable

        // Use NavController to navigate to the MarkedMapFragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.action_eventDetailsFragment_to_markedMapFragment, args);
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
                sendAnnouncementPage(event);
                break;
            case 3: // View Waitlist
                viewWaitlist(event);
                break;
            case 4:
                viewEnrolled(event);
                break;
            case 5:
                viewCancelled(event);
                break;
            case 6:
                viewMap(event);
                break;
            case 7: // Initiate Lottery
                initiateLottery();
                break;
            case 8: // edit the event information and the poster
                editEvent();
                break;
            case 9:
                navigateToPendingList();
                break;
            default:
                break;
        }
    }

}