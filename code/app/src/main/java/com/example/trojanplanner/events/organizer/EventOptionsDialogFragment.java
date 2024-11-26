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

        // Fetch the event to select a winner
        Database.getDB().getEvent(
                object -> {
                    Event syncedEvent = (Event) object;

                    if (syncedEvent == null || syncedEvent.getWaitingList() == null || syncedEvent.getWaitingList().isEmpty()) {
                        Toast.makeText(getContext(), "Cannot initiate a lottery. Waitlist is empty or missing.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<User> waitlist = syncedEvent.getWaitingList();

                    // Simple random lottery logic
                    int winnerIndex = (int) (Math.random() * waitlist.size());
                    User winner = waitlist.get(winnerIndex);

                    // Log the winner
                    Log.d("EventOptionsDialog", "Winner selected: " + winner.getFirstName() + " " + winner.getLastName());

                    // Call `joinPendingList` to add the winner to the pending list
                    joinPendingList(winner);

                    // Call `leaveWaitlist` to remove the winner from the waitlist
                    leaveWaitlist(winner);
                },
                () -> Log.e("EventOptionsDialog", "Failed to fetch event data."),
                event.getEventId()
        );
    }

//    public void joinPendingList(User winner, Runnable onSuccess, Runnable onFailure) {
//        if (event == null || winner == null) {
//            Log.e("joinPendingList", "Event or winner data is missing.");
//            onFailure.run();
//            return;
//        }
//
//        Database.getDB().getEvent(
//                object -> {
//                    Event syncedEvent = (Event) object;
//
//                    if (syncedEvent.getPendingList() == null) {
//                        syncedEvent.setPendingList(new ArrayList<>());
//                    }
//
//                    // Add the winner to the pending list
//                    syncedEvent.getPendingList().add(winner);
//
//                    Database.getDB().getEntrant(
//                            object1 -> {
//                                Entrant syncedEntrant = (Entrant) object1;
//
//                                if (syncedEntrant.getCurrentPendingEvents() == null) {
//                                    syncedEntrant.setCurrentPendingEvents(new ArrayList<>());
//                                }
//
//                                syncedEntrant.getCurrentPendingEvents().add(syncedEvent);
//
//                                // Save the updated event
//                                Database.getDB().insertEvent(
//                                        unused -> {
//                                            Log.d("joinPendingList", "Event successfully updated in the database.");
//
//                                            // Save the entrant after the event update
//                                            Database.getDB().insertUserDocument(
//                                                    unused2 -> {
//                                                        Log.d("joinPendingList", "Entrant successfully updated in the database.");
//                                                        onSuccess.run(); // Signal success
//                                                    },
//                                                    e -> {
//                                                        Log.e("joinPendingList", "Error updating entrant: " + e.getMessage());
//                                                        onFailure.run(); // Signal failure
//                                                    },
//                                                    syncedEntrant
//                                            );
//                                        },
//                                        e -> {
//                                            Log.e("joinPendingList", "Error updating event: " + e.getMessage());
//                                            onFailure.run(); // Signal failure
//                                        },
//                                        syncedEvent
//                                );
//                            },
//                            () -> {
//                                Log.e("joinPendingList", "Error syncing entrant from database.");
//                                onFailure.run(); // Signal failure
//                            },
//                            winner.getDeviceId()
//                    );
//                },
//                () -> {
//                    Log.e("joinPendingList", "Error syncing event from database.");
//                    onFailure.run(); // Signal failure
//                },
//                event.getEventId()
//        );
//    }

    /**
     * Adds the winner to the event's pending list.
     * Also updates the entrant's current pending events in the database.
     */
    public void joinPendingList(User winner) {
        if (event == null || winner == null) {
//            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Database.getDB().getEvent(
                object -> {
                    Event syncedEvent = (Event) object;

                    // Ensure the event's pending list is initialized
                    if (syncedEvent.getPendingList() == null) {
                        syncedEvent.setPendingList(new ArrayList<>());
                    }


                    // Add the winner to the pending list
                    syncedEvent.getPendingList().add(winner);

                    // Log the current pending list
                    Log.d("EventDetails", "Current Pending List:" + syncedEvent.getPendingList());

                    Database.getDB().getEntrant(
                            object1 -> {
                                Entrant syncedEntrant = (Entrant) object1;
                                if (syncedEntrant.getCurrentPendingEvents() == null) {
                                    syncedEntrant.setCurrentPendingEvents(new ArrayList<>());
                                }

                                syncedEntrant.getCurrentPendingEvents().add(syncedEvent);

                                // Debug logs before saving
                                Log.d("EventDetails", "Synced Event to Save: " + syncedEvent.toString());
                                Log.d("EventDetails", "Synced Entrant to Save: " + syncedEntrant.toString());

                                // Save the updated event
                                Database.getDB().insertEvent(
                                        unused -> {
                                            Log.d("EventDetails", "Event successfully updated in the database.");

                                            // Save the updated entrant only after the event is successfully updated
                                            Database.getDB().insertUserDocument(
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
//                                                            Toast.makeText(getContext(), "Successfully added to the pending list!", Toast.LENGTH_SHORT).show();
                                                            Log.d("EventDetails", "Entrant successfully updated in the database.");
                                                        }
                                                    },
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
//                                                            Toast.makeText(getContext(), "Failed to update entrant data.", Toast.LENGTH_SHORT).show();
                                                            Log.e("EventDetails", "Error updating entrant: " + e.getMessage());
                                                        }
                                                    },
                                                    syncedEntrant // Pass the synchronized entrant
                                            );
                                        },
                                        e -> {
//                                            Toast.makeText(getContext(), "Failed to update event data.", Toast.LENGTH_SHORT).show();
                                            Log.e("EventDetails", "Error updating event: " + e.getMessage());
                                        },
                                        syncedEvent // Pass the synchronized event
                                );
                            },
                            () -> {
//                                Toast.makeText(getContext(), "Failed to sync entrant data.", Toast.LENGTH_SHORT).show();
                                Log.e("EventDetails", "Error syncing entrant from database.");
                            },
                            winner.getDeviceId() // Fetch the entrant using winner's deviceId
                    );
                },
                () -> {
//                    Toast.makeText(getContext(), "Failed to sync event data.", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetails", "Error syncing event from database.");
                },
                event.getEventId()
        );
    }


    public void leaveWaitlist(User winner) {
        Database.getDB().getEvent(
                new Database.QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
                        Event syncedEvent = (Event) object;

                        // Ensure the waitlist is initialized
                        if (syncedEvent.getWaitingList() == null || syncedEvent.getWaitingList().isEmpty()) {
                            Log.d("leaveWaitlist", "The waitlist is empty.");
                            return;
                        }

                        //this doesn't work
                        Log.d("leaveWaitlist", "Current Waiting List: " + syncedEvent.getWaitingList().toString());
                        Log.d("leaveWaitlist", "Current Entrant: " + winner.toString());

//                        if (!syncedEvent.isUserRegistered(winner)){
//                            Log.d("leaveWaitlist", "Entrant is not on the waitlist.");
//                            return;
//                        }

                        // Fetch the entrant to remove the event from their currentWaitlistedEvents
                        Database.getDB().getEntrant(
                                new Database.QuerySuccessAction() {
                                    @Override
                                    public void OnSuccess(Object object) {
                                        Entrant syncedEntrant = (Entrant) object;

                                        // Ensure the waitlisted events are initialized
                                        if (syncedEntrant.getCurrentWaitlistedEvents() == null || syncedEntrant.getCurrentWaitlistedEvents().isEmpty()) {
                                            Log.d("leaveWaitlist", "No waitlisted events found for the entrant.");
                                            return;
                                        }
                                        for (Event event : syncedEntrant.getCurrentWaitlistedEvents()) {
                                            Log.d("leaveWaitlist", "Comparing with Event: " + event);
                                            Log.d("leaveWaitlist", "Comparison Result: " + syncedEvent.equals(event));
                                        }
                                        // Attempt to remove the event from the entrant's waitlisted events
                                        if (!syncedEntrant.getCurrentWaitlistedEvents().contains(syncedEvent)) {
                                            Log.d("leaveWaitlist", "Event is not in the entrant's waitlisted events.");
                                            return;
                                        }

                                        // Save the updated event
                                        Database.getDB().insertEvent(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("leaveWaitlist", "Event successfully updated in the database.");

                                                        // Save the updated entrant
                                                        Database.getDB().insertUserDocument(
                                                                new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Log.d("leaveWaitlist", "Entrant successfully updated in the database.");
                                                                    }
                                                                },
                                                                new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e("leaveWaitlist", "Failed to update entrant data: " + e.getMessage());
                                                                    }
                                                                },
                                                                syncedEntrant
                                                        );
                                                    }
                                                },
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("leaveWaitlist", "Failed to update event data: " + e.getMessage());
                                                    }
                                                },
                                                syncedEvent
                                        );
                                    }
                                },
                                new Database.QueryFailureAction() {
                                    @Override
                                    public void OnFailure() {
                                        Log.e("leaveWaitlist", "Failed to sync entrant data from the database.");
                                    }
                                },
                                winner.getDeviceId()
                        );
                    }
                },
                new Database.QueryFailureAction() {
                    @Override
                    public void OnFailure() {
                        Log.e("leaveWaitlist", "Failed to sync event data from the database.");
                    }
                },
                event.getEventId()
        );
    }




}