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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.App;
import com.example.trojanplanner.events.EventDetailsFragment;
import com.example.trojanplanner.HelperFragments.MarkedMapFragment;
import com.example.trojanplanner.QRUtils.QRCodeUtil;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
            case 10:
                navigateToPendingList();
                break;
            default:
                break;
        }
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
        // Show a Toast message
        Toast.makeText(getContext(), "View Map clicked", Toast.LENGTH_SHORT).show();

        // Use NavController to navigate to the MarkedMapFragment
        NavController navController = Navigation.findNavController(getParentFragment().requireView());
        navController.navigate(R.id.action_eventDetailsFragment_to_markedMapFragment);  // Ensure the ID matches your nav graph
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
        if (event == null) {
            Toast.makeText(requireContext(), "Event data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Database.getDB().getEvent(
                new Database.QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
//                        if (!isAdded()) {
//                            Log.d("EventOptionsDialog", "Fragment not attached to context. Skipping callback.");
//                            return;
//                        }

                        Event syncedEvent = (Event) object;

                        if (syncedEvent == null || syncedEvent.getWaitingList() == null || syncedEvent.getWaitingList().isEmpty()) {
                            Toast.makeText(requireContext(), "Cannot initiate a lottery. Waitlist is empty or missing.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ArrayList<User> waitlist = syncedEvent.getWaitingList();

                        // Simple random lottery logic
                        int winnerIndex = (int) (Math.random() * waitlist.size());
                        User winner = waitlist.get(winnerIndex);

                        // Set the winner in the current instance (if needed for methods)
                        event = syncedEvent;

                        Log.d("EventOptionsDialog", "Winner selected: " + winner.getFirstName() + " " + winner.getLastName());

                        // Call `leaveWaitlist` to remove the winner
                        leaveWaitlist();

                        // Call `joinPendingList` to add the winner to the pending list
                        joinPendingList();

                        // Notify the winner and log
                        String winnerMessage = "Congratulations! " + winner.getFirstName() + " " + winner.getLastName() + " has won the lottery.";
                        Log.d("EventOptionsDialog", "Lottery Winner: " + winnerMessage);

                        new AlertDialog.Builder(requireContext())
                                .setTitle("Lottery Winner")
                                .setMessage(winnerMessage)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    }
                },
                new Database.QueryFailureAction() {
                    @Override
                    public void OnFailure() {
                        Log.e("EventOptionsDialog", "Failed to fetch event.");
                        Toast.makeText(requireContext(), "Failed to fetch event data. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                event.getEventId()
        );
    }

    public void joinPendingList() {
        if (event == null || App.currentUser == null) {
            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Entrant currentEntrant = (Entrant) App.currentUser;

        Database.getDB().getEvent(
                new Database.QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
                        Event syncedEvent = (Event) object;

                        // Ensure the pending list is initialized
                        if (syncedEvent.getPendingList() == null) {
                            syncedEvent.setPendingList(new ArrayList<>());
                        }

                        // Prevent duplicates
                        if (syncedEvent.getPendingList().contains(currentEntrant)) {
                            Toast.makeText(getContext(), "You are already on the pending list.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Add entrant to the event's pending list
                        syncedEvent.getPendingList().add(currentEntrant);

                        Database.getDB().getEntrant(
                                new Database.QuerySuccessAction() {
                                    @Override
                                    public void OnSuccess(Object object) {
                                        Entrant syncedEntrant = (Entrant) object;

                                        // Ensure the entrant's pending events are initialized
                                        if (syncedEntrant.getCurrentPendingEvents() == null) {
                                            syncedEntrant.setCurrentPendingEvents(new ArrayList<>());
                                        }

                                        // Add the event to the entrant's pending events
                                        syncedEntrant.getCurrentPendingEvents().add(syncedEvent);

                                        // Save the updated event and entrant
                                        Database.getDB().insertEvent(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("joinPendingList", "Event successfully updated in the database.");

                                                        Database.getDB().insertUserDocument(
                                                                new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(getContext(), "Successfully added to the pending list!", Toast.LENGTH_SHORT).show();
                                                                        Log.d("joinPendingList", "Entrant successfully updated in the database.");
                                                                    }
                                                                },
                                                                new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getContext(), "Failed to update entrant data.", Toast.LENGTH_SHORT).show();
                                                                        Log.e("joinPendingList", "Error updating entrant: " + e.getMessage());
                                                                    }
                                                                },
                                                                syncedEntrant
                                                        );
                                                    }
                                                },
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failed to update event data.", Toast.LENGTH_SHORT).show();
                                                        Log.e("joinPendingList", "Error updating event: " + e.getMessage());
                                                    }
                                                },
                                                syncedEvent
                                        );
                                    }
                                },
                                new Database.QueryFailureAction() {
                                    @Override
                                    public void OnFailure() {
                                        Toast.makeText(getContext(), "Failed to sync entrant data.", Toast.LENGTH_SHORT).show();
                                        Log.e("joinPendingList", "Error syncing entrant from database.");
                                    }
                                },
                                currentEntrant.getDeviceId()
                        );
                    }
                },
                new Database.QueryFailureAction() {
                    @Override
                    public void OnFailure() {
                        Toast.makeText(getContext(), "Failed to sync event data.", Toast.LENGTH_SHORT).show();
                        Log.e("joinPendingList", "Error syncing event from database.");
                    }
                },
                event.getEventId()
        );
    }
    public void leaveWaitlist() {
        if (event == null || App.currentUser == null) {
            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Entrant currentEntrant = (Entrant) App.currentUser;

        Database.getDB().getEvent(
                new Database.QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
                        Event syncedEvent = (Event) object;

                        // Ensure the waiting list is initialized
                        if (syncedEvent.getWaitingList() == null) {
                            syncedEvent.setWaitingList(new ArrayList<>());
                        }

                        // Check if the entrant is in the waiting list
                        if (!syncedEvent.getWaitingList().contains(currentEntrant)) {
                            Toast.makeText(getContext(), "You are not on the waitlist.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Remove the entrant from the waiting list
                        syncedEvent.getWaitingList().remove(currentEntrant);

                        Database.getDB().getEntrant(
                                new Database.QuerySuccessAction() {
                                    @Override
                                    public void OnSuccess(Object object) {
                                        Entrant syncedEntrant = (Entrant) object;

                                        // Ensure the entrant's waitlisted events are initialized
                                        if (syncedEntrant.getCurrentWaitlistedEvents() == null) {
                                            syncedEntrant.setCurrentWaitlistedEvents(new ArrayList<>());
                                        }

                                        // Remove the event from the entrant's waitlisted events
                                        syncedEntrant.getCurrentWaitlistedEvents().remove(syncedEvent);

                                        // Save the updated event and entrant
                                        Database.getDB().insertEvent(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("leaveWaitlist", "Event successfully updated in the database.");

                                                        Database.getDB().insertUserDocument(
                                                                new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(getContext(), "Successfully removed from the waitlist!", Toast.LENGTH_SHORT).show();
                                                                        Log.d("leaveWaitlist", "Entrant successfully updated in the database.");
                                                                    }
                                                                },
                                                                new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getContext(), "Failed to update entrant data.", Toast.LENGTH_SHORT).show();
                                                                        Log.e("leaveWaitlist", "Error updating entrant: " + e.getMessage());
                                                                    }
                                                                },
                                                                syncedEntrant
                                                        );
                                                    }
                                                },
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failed to update event data.", Toast.LENGTH_SHORT).show();
                                                        Log.e("leaveWaitlist", "Error updating event: " + e.getMessage());
                                                    }
                                                },
                                                syncedEvent
                                        );
                                    }
                                },
                                new Database.QueryFailureAction() {
                                    @Override
                                    public void OnFailure() {
                                        Toast.makeText(getContext(), "Failed to sync entrant data.", Toast.LENGTH_SHORT).show();
                                        Log.e("leaveWaitlist", "Error syncing entrant from database.");
                                    }
                                },
                                currentEntrant.getDeviceId()
                        );
                    }
                },
                new Database.QueryFailureAction() {
                    @Override
                    public void OnFailure() {
                        Toast.makeText(getContext(), "Failed to sync event data.", Toast.LENGTH_SHORT).show();
                        Log.e("leaveWaitlist", "Error syncing event from database.");
                    }
                },
                event.getEventId()
        );
    }


}