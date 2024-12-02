package com.example.trojanplanner.events.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.App;
import com.example.trojanplanner.HelperFragments.MarkedMapFragment;
import com.example.trojanplanner.QRUtils.QRCodeUtil;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

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
            Log.d("EventOptionsDialog", "Event received: " + event.getName());
            Database.getDB().getEvent(object -> {
                event = (Event) object;
                Log.d("EventOptionsDialog", "Waitlist" + event.getWaitingList());
                Log.d("EventOptionsDialog", "CancelledList" + event.getCancelledList());
                Log.d("EventOptionsDialog", "PendingList" + event.getPendingList());
                Log.d("EventOptionsDialog", "EnrolledList" + event.getEnrolledList());
            }, ()-> {}, event.getEventId());
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
     * Navigates to the waitlist fragment to display the list of people who have been invited
     * to the event but have not yet accepted or declined the invitation.
     *
     * @param event The event for which the pending invitation list will be viewed.
     *              It is passed as a Serializable object to the next fragment.
     */
    private void viewPendingList(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        String listType = "pending";
        args.putString("listType", listType);

        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.waitlistFragment, args);
    }

    /**
     * Navigates to the waitlist fragment to display the waiting list of an event.
     *
     * @param event The event for which the waiting list will be viewed.
     *              It is passed as a Serializable object to the next fragment.
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
     * Navigates to the waitlist fragment to display the list of people who have enrolled in the event.
     *
     * @param event The event for which the enrolled list will be viewed.
     *              It is passed as a Serializable object to the next fragment.
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
     * Navigates to the waitlist fragment to display the list of attendees who have cancelled their participation in the event.
     *
     * @param event The event for which the cancelled attendees list will be viewed.
     *              It is passed as a Serializable object to the next fragment.
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

    /**
     * Displays an error message in a dialog to inform the user.
     *
     * @param errorMessage The error message to be displayed in the dialog.
     *                     It provides details about the issue that occurred.
     */
    private void displayError(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Notice")
                .setMessage(errorMessage)
                .setPositiveButton("Okay I understand", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Note: Should allow organizer to initiate lottery,
    // AS LONG AS THESE CONDITIONS AREN'T VIOLATED
    // Event's starting date isn't <= current Date
    // Waitlist Close date isn't >= current Date
    // Event's enrolled list isn't finalized: meaning event.getStatus doesn't return 'finalized'
    // Enrolled list + pending list != event capacity
    // Event capacity - (Enrolled + Pending List) size is the ammount of Attendees possible to select
    /**
     * Initiates a lottery for selecting attendees for the event from the waiting list.
     *
     * The method ensures the lottery can only be initiated under specific conditions:
     * 1. The event must not have started yet.
     * 2. The event's waitlist must be closed.
     * 3. The event's enrolled list must not be finalized.
     * 4. The event must not be finished.
     *
     * If the conditions are met, the user is prompted to choose the number of attendees to select,
     * and the lottery is performed by randomly picking users from the waiting list.
     * The selected users are then added to the event's pending list, and the non-selected users
     * are notified of their status. The results are shown in a dialog.
     *
     * The process involves several checks, such as ensuring valid inputs and removing null values from the lists.
     * Finally, notifications are sent to both selected and non-selected users regarding the lottery result.
     *
     * @throws NullPointerException if event is null or context is not available.
     * @throws NumberFormatException if the input for the number of attendees is not a valid number.
     */
    private void initiateLottery() {
        // Event's starting date is current Date or before
        if (event.getStartDateTime().getDate() <= new Date().getDate()) {
            String errorMessage = "The event has started already, you can't initiate a lottery.";
            displayError(errorMessage);
            return;
        }

        // Waitlist Close date isn't <= current Date
        if (event.getWaitlistClose() != null && event.getWaitlistClose().getDate() > new Date().getDate()) {
            String errorMessage = "The event sign up has not closed yet, you can't initiate a lottery.";
            displayError(errorMessage);
            return;
        }

        // Event's enrolled list isn't finalized: meaning event.getStatus returns ''
        if (event.getStatus() != null && event.getStatus().equals("finalized")) {
            String errorMessage = "You have finalized this event's enrolled list, you can't initiate a lottery.";
            displayError(errorMessage);
            return;
        }

        if (event.getStatus() != null && event.getStatus().equals("finished")) {
            String errorMessage = "You can't initiate a lottery for a finished event.";
            displayError(errorMessage);
            return;
        }

        Context context = getContext(); // Capture context at the start
        if (context == null) {
            return; // Exit if fragment is not attached
        }
        
        // Copy the waiting list and remove null entries
        ArrayList<User> Lottery_waitlist = new ArrayList<>(event.getWaitingList());
        Lottery_waitlist.removeIf(Objects::isNull); // Remove null values

        // Debugging: Show the Lottery_waitlist size and contents after removing nulls
        StringBuilder waitlistDebug = new StringBuilder("Lottery_waitlist after removeIf:\n");
        if (Lottery_waitlist.isEmpty()) {
            Log.d("EventOptionsDialogFragment", "Lottery_waitlist is empty after removing null values.");
        } else {
            for (User user : Lottery_waitlist) {
                if (user != null) {
                    waitlistDebug.append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
                }
            }
            Log.d("EventOptionsDialogFragment", waitlistDebug.toString());
        }

        if (event.getTotalSpots() == null || event.getTotalSpots() == 0
                || event.getPendingList() == null || event.getEnrolledList() == null) {
            Toast.makeText(context, "Event data is missing, we apologize for the inconvenience", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Number of Attendees, " + Lottery_waitlist.size() + " available\n\n");
        builder.setMessage("Invites Available: " + (event.getTotalSpots() - (event.getPendingList().size() + event.getEnrolledList().size())) + "/" + event.getTotalSpots());
        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 60, 50, 60); // Left, top, right, bottom margins (adjust values as needed)

        final EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER); // Restrict input to numbers only
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String inputText = input.getText().toString();
            if (inputText.isEmpty()) {
                Log.d("EventOptionsDialogFragment", "Please enter a valid number.");
                return;
            }

            int numUsersToSelect;
            try {
                numUsersToSelect = Integer.parseInt(inputText); // Assign the user input directly to numUsersToSelect
            } catch (NumberFormatException e) {
                Log.d("EventOptionsDialogFragment", "Invalid number entered.");
                return;
            }

            if (numUsersToSelect <= 0 || numUsersToSelect > Lottery_waitlist.size()
                    || (event.getTotalSpots() - (event.getPendingList().size() + event.getEnrolledList().size())) < numUsersToSelect) {

                return;
            }

            ArrayList<User> selectedAttendees = new ArrayList<>();
            for (int i = 0; i < numUsersToSelect; i++) {
                if (Lottery_waitlist.isEmpty()) {
                    Log.d("LotterySelection", "Lottery_waitlist is empty. No more users to select.");
                    break;
                }
                int randomIndex = (int) (Math.random() * Lottery_waitlist.size());
                User selectedUser = Lottery_waitlist.remove(randomIndex); // Remove to avoid duplicates

                // Debugging: Log the user removed and current waitlist state
                Log.d("LotterySelection", "Removing user: " + (selectedUser != null ? selectedUser.getFirstName() : "null") +
                        " at index: " + randomIndex);
                Log.d("LotterySelection", "Current Lottery_waitlist size after removal: " + Lottery_waitlist.size());

                // Ensure the selected user is not null
                if (selectedUser != null) {
                    selectedAttendees.add(selectedUser);
                } else {
                    Log.d("EventOptionsDialogFragment", "Encountered a null user during selection.");
                }
            }

            for (User user : Lottery_waitlist){
                App.sendAnnouncement(user.getDeviceId(), event.getName(), "You lost the lottery for the event, however you're still on the waitlist");
            }

            // Debugging: Show the selected attendees after lottery
            StringBuilder selectedAttendeesDebug = new StringBuilder("Selected Attendees:\n");
            for (User selectedUser : selectedAttendees) {
                if (selectedUser != null) {
                    selectedAttendeesDebug.append(selectedUser.getFirstName()).append(" ").append(selectedUser.getLastName()).append("\n");
                }
            }
            Log.d("LotterySelection", "Selected Attendees:\n" + selectedAttendeesDebug.toString());

            // Update the pending list with winners
            event.setPendingList(selectedAttendees);
            Log.d("EventOptionsDialogFragment", numUsersToSelect + " attendee(s) successfully registered.");

            // Display winners in a new dialog
            StringBuilder winnersList = new StringBuilder();
            CountDownLatch latch = new CountDownLatch(event.getPendingList().size()); // Count for each query

            for (User winner : event.getPendingList()) {
                if (winner != null) {
                    Database.getDB().getEntrant(object -> {
                        Entrant user = (Entrant) object;

                        ArrayList<Event> currentPending = user.getCurrentPendingEvents();
                        if (currentPending == null) {
                            currentPending = new ArrayList<>();
                        }
                        currentPending.add(event);
                        user.setCurrentPendingEvents(currentPending);

                        ArrayList<Event> currentWaitlist = user.getCurrentWaitlistedEvents();
                        if (currentWaitlist == null) {
                            currentWaitlist = new ArrayList<>();
                        }

                        ArrayList<Event> finalCurrentPending = currentPending;
                        currentWaitlist.removeIf(pendingEvent -> finalCurrentPending.contains(pendingEvent));
                        user.setCurrentWaitlistedEvents(currentWaitlist);
                        // Custom async insert with callback
                        asyncInsertUserDocument(user, () -> {
                            winnersList.append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
                            latch.countDown(); // Decrement latch after async operation completes
                        });
                    }, () -> {
                        latch.countDown(); // Ensure latch decrements even on failure
                    }, winner.getDeviceId());
                } else {
                    latch.countDown(); // Account for null winners
                }
            }

            new Thread(() -> {  // Background thread to wait for all queries
                try {
                    latch.await(); // Wait for all queries to complete
                    ((Activity) context).runOnUiThread(() -> {  // Update UI on main thread
                        new AlertDialog.Builder(context)
                                .setTitle("Lottery Winners")
                                .setMessage("The following attendees have been selected:\n" + winnersList.toString().trim())
                                .setPositiveButton("OK", (winnerDialog, whichWinner) -> winnerDialog.dismiss())
                                .create()
                                .show();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle interruption
                }
            }).start();

            ArrayList<User> resultList = event.getWaitingList().stream()
                    .filter(user -> !event.getPendingList().contains(user))
                    .collect(Collectors.toCollection(ArrayList::new)); // Ensures resultList is an ArrayList
            event.setWaitingList(resultList);
            Database.getDB().insertEvent(event);

            new Thread(() -> {
                // Loop through the pending list and send the announcement to each user
                for (User user : event.getPendingList()) {
                    App.sendAnnouncement(user.getDeviceId(), event.getName(),
                            "CONGRATS!! You've won the lottery, accept/decline your invitation in the app");
                }
            }).start();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * Asynchronously inserts the user document into the database.
     * <p>
     * This method runs the database insertion on a background thread to avoid blocking the main UI thread.
     * Once the insertion is complete, the provided {@link Runnable} callback will be executed on the main thread.
     * </p>
     *
     * @param user The {@link Entrant} object representing the user to be inserted into the database.
     * @param onComplete A {@link Runnable} callback that will be executed once the insertion is complete.
     *                   This will run on the main thread.
     */
    private void asyncInsertUserDocument(Entrant user, Runnable onComplete) {
        Database.getDB().insertUserDocument(user);
        new Handler(Looper.getMainLooper()).post(onComplete);
    }

    /**
     * Uploads the QR hash to the database for the specified event.
     * <p>
     * This method attempts to insert the QR hash associated with the event into the database. It uses
     * {@link Database.QuerySuccessAction} and {@link Database.QueryFailureAction} to handle the success
     * and failure scenarios. On success, a success message is logged, and a toast is displayed to the user.
     * On failure, an error message is logged, and an error toast is displayed.
     * </p>
     *
     * @param qrHash The QR hash to be uploaded to the database.
     * @param event The {@link Event} object associated with the QR hash that is being uploaded.
     */
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

    /**
     * Displays the QR code in a dialog box.
     * <p>
     * This method creates an {@link AlertDialog} that shows the provided QR code in an {@link ImageView}.
     * It allows the user to close the dialog by pressing the "Close" button.
     * </p>
     *
     * @param qrCodeBitmap The {@link Bitmap} object representing the QR code to be displayed in the dialog.
     */
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
     * Generates a unique event code by hashing the event's ID and uploading it to the database.
     * <p>
     * This method first checks if the event object and its ID are not null. If valid, it generates a hashed version
     * of the event ID and uploads it to the database for further use. If any errors occur during the process,
     * appropriate error messages are logged and displayed to the user.
     * </p>
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
     * Navigates to the map view for the event location.
     * <p>
     * This method creates a Bundle containing the event object and uses the {@link NavController} to navigate
     * to the {@link MarkedMapFragment}. A Toast message is also displayed when the option is selected.
     * </p>
     *
     * @param event The {@link Event} object containing the details of the event to be shown on the map.
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
     * Displays the current event's QR code for check-in.
     * <p>
     * This method checks if the event and event ID are valid. It then hashes the event ID, generates a QR code
     * for it, and displays the QR code in a dialog. If any of the steps fail, appropriate error messages are
     * logged and displayed to the user.
     * </p>
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
                viewPendingList(event);
                break;
            case 7: // Initiate Lottery
                initiateLottery();
                break;
            case 8:
                viewMap(event);
                break;
            default:
                break;
        }
    }
}