package com.example.trojanplanner.events;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.events.entrant.EntrantEventOptionsDialogFragment;
import com.example.trojanplanner.events.organizer.EventOptionsDialogFragment;

import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A fragment to display event details to an entrant. It provides functionality to join or leave
 * the event's waitlist.
 * <p>
 * This fragment requires an event and entrant to be passed as arguments when being instantiated.
 * The event's details will be displayed, and based on the entrant's current status, appropriate
 * buttons will be shown for joining or leaving the waitlist.
 */
public class EventDetailsFragment extends Fragment {
    private Event event;
    private Entrant entrant;
    private Database database;
    private Button buttonLeaveWaitlist;
    private Button manageButton;
    private Button optionsButton;
    private Button acceptButton, declineButton;
    private TextView invitationText;

    @NonNull
    public static EventDetailsFragment newInstance(Event event, Entrant entrant) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        args.putSerializable("entrant", entrant);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty constructor
    public EventDetailsFragment() {}

    /**
     * Populates the event details in the respective text views.
     * If event details are missing, default values will be shown.
     *
     * @param eventNameTextView        The TextView to display the event's name.
     * @param eventLocationTextView    The TextView to display the event's location.
     * @param eventDateTextView        The TextView to display the event's start and end date.
     * @param recurringDatesTextView   The TextView to display the event's recurrence days.
     * @param eventDescriptionTextView The TextView to display the event's description.
     */
    public void populateEventDetails(TextView eventNameTextView, TextView eventLocationTextView,
                                     TextView eventDateTextView, TextView recurringDatesTextView,
                                     TextView eventDescriptionTextView) {

        eventNameTextView.setText(event.getName());
        if (event.getFacility() != null) {
            eventLocationTextView.setText(event.getFacility().getFacilityId());
        }

        // Default values for dates in case they are null
        String defaultDate = "Not Available";  // Default date if event date is null
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Assign default value if startDateTime or endDateTime is null
        String startDate = (event.getStartDateTime() != null) ? dateFormat.format(event.getStartDateTime()) : defaultDate;
        String endDate = (event.getEndDateTime() != null) ? dateFormat.format(event.getEndDateTime()) : defaultDate;

        eventDateTextView.setText(startDate + " - " + endDate);

        // Convert abbreviations in recurrenceDays to full day names
        ArrayList<String> recurrenceDays = event.getRecurrenceDays();

        if (recurrenceDays != null) {
            String recurrenceDaysText = recurrenceDays.stream()
                    .map(this::getFullDayName) // Convert each unique abbreviation to full day name
                    .filter(name -> !name.isEmpty()) // Filter out any invalid/missing conversions
                    .reduce((a, b) -> a + ", " + b) // Join with commas
                    .orElse("No recurrence");

            recurringDatesTextView.setText(recurrenceDaysText);
        }
        eventDescriptionTextView.setText(event.getDescription());

    }

    /**
     * Shows a confirmation dialog for the entrant to join the event's waitlist.
     * If confirmed, the entrant will be added to the event's waitlist.
     */
    public void joinWaitlist() {
        if (event == null || App.currentUser == null) {
            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
            return; // Unchanged
        }

        Entrant currentEntrant = (Entrant) App.currentUser; // Unchanged

        database.getEvent(
                new Database.QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
                        Event syncedEvent = (Event) object;

                        // Ensure the event's waiting list is initialized
                        if (syncedEvent.getWaitingList() == null) {
                            syncedEvent.setWaitingList(new ArrayList<>());
                        }

                        // Prevent duplicates in the waitlist
                        if (syncedEvent.getWaitingList().contains(currentEntrant)) {
                            Toast.makeText(getContext(), "You are already on the waitlist.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Add the entrant to the event's waitlist
                        syncedEvent.getWaitingList().add(currentEntrant);

                        // Debug log to confirm local addition
                        Log.d("add entrant", "Updated Local Event Waitlist: " + syncedEvent.getWaitingList());


                        database.getEntrant(
                                new Database.QuerySuccessAction() {
                                    @Override
                                    public void OnSuccess(Object object) {
                                        Entrant syncedEntrant = (Entrant) object;

                                        // Ensure the entrant's waitlisted events are initialized
                                        if (syncedEntrant.getCurrentWaitlistedEvents() == null) {
                                            syncedEntrant.setCurrentWaitlistedEvents(new ArrayList<>());
                                        }

                                        // Add the event to the entrant's waitlisted events
                                        syncedEntrant.getCurrentWaitlistedEvents().add(syncedEvent);

                                        // Debug logs before saving
                                        Log.d("EventDetails", "Synced Event to Save: " + syncedEvent.toString());
                                        Log.d("EventDetails", "Synced Entrant to Save: " + syncedEntrant.toString());

                                        // Save the updated event
                                        // Save the updated event
                                        database.insertEvent(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("EventDetails", "Event successfully updated in the database.");
                                                        addtoNotifications();
                                                        // Save the updated entrant only after the event is successfully updated
                                                        database.insertUserDocument(
                                                                new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(getContext(), "Successfully added to the waitlist!", Toast.LENGTH_SHORT).show();
                                                                        Log.d("EventDetails", "Entrant successfully updated in the database.");

                                                                        // Validate database state
                                                                        validateDatabaseState(syncedEvent, syncedEntrant);
                                                                    }
                                                                },
                                                                new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getContext(), "Failed to update user data.", Toast.LENGTH_SHORT).show();
                                                                        Log.e("EventDetails", "Error updating entrant: " + e.getMessage());
                                                                    }
                                                                },
                                                                syncedEntrant // Pass the correct User object
                                                        );
                                                    }
                                                },
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failed to update event data.", Toast.LENGTH_SHORT).show();
                                                        Log.e("EventDetails", "Error updating event: " + e.getMessage());
                                                    }
                                                },
                                                syncedEvent // Pass the correct Event object
                                        );
                                    }
                                },
                                new Database.QueryFailureAction() {
                                    @Override
                                    public void OnFailure() {
                                        Toast.makeText(getContext(), "Failed to sync user data.", Toast.LENGTH_SHORT).show();
                                        Log.e("EventDetails", "Error syncing entrant from database.");
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
                        Log.e("EventDetails", "Error syncing event from database.");
                    }
                },
                event.getEventId()
        );
    }


    /**
     * Validates that the event and entrant were correctly saved to the database.
     */
    private void validateDatabaseState(Event syncedEvent, Entrant syncedEntrant) {
        // Validate the event in the database
        database.getEvent(new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                Log.d("EventDetails", "Verified Event in Database: " + object.toString());
            }
        }, new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                Log.e("EventDetails", "Failed to verify event in database.");
            }
        }, syncedEvent.getEventId());

        // Validate the entrant in the database
        database.getEntrant(new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                Log.d("EventDetails", "Verified Entrant in Database: " + object.toString());
            }
        }, new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                Log.e("EventDetails", "Failed to verify entrant in database.");
            }
        }, syncedEntrant.getDeviceId());
    }

    private void addtoNotifications() {
        if (event != null) {
            String eventId = event.getEventId(); // Use eventId for channel and topic
            String channelId = "EventChannel_" + eventId; // Dynamic channel ID
            String channelName = "Event Updates for " + eventId;

            // Create a Notification Manager
            NotificationManager notificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            // For Android 8.0+ (Oreo and above), create a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("Notifications for updates on event " + eventId);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            // Log the topic
            String topic = "Event_" + eventId;  // Match the backend topic
            Log.d("Notifications", "Subscribing to topic: " + topic);

            // Subscribe the user to the event's notification topic
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Notifications", "Successfully subscribed to notifications for event: " + eventId);
                            Toast.makeText(getContext(), "Subscribed to event notifications.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Notifications", "Failed to subscribe to event notifications: " + task.getException());
                            Toast.makeText(getContext(), "Failed to subscribe to notifications.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("Notifications", "Event data is missing. Cannot create notification channel.");
            Toast.makeText(getContext(), "Event data is missing.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCreatedEventsFromDatabase(String eventIdToCheck, final EventCheckCallback callback) {
        // Get current user ID (assuming App.currentUser holds this information)
        String userId = App.currentUser.getDeviceId();

        if (userId == null || userId.isEmpty()) {
            System.out.println("No user is currently logged in.");
            callback.onComplete(false);  // Return false if no user is logged in
            return;
        }

        // Reference to the current user document in the "users" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Fetch the "createdEvents" field from the user document
        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> createdEvents =
                                (List<DocumentReference>) documentSnapshot.get("createdEvents");

                        if (createdEvents != null && !createdEvents.isEmpty()) {
                            // Check if the event ID matches any created event
                            for (DocumentReference eventRef : createdEvents) {
                                eventRef.get()
                                        .addOnSuccessListener(eventSnapshot -> {
                                            if (eventSnapshot.exists()) {
                                                String retrievedEventId = eventSnapshot.getId();
                                                if (retrievedEventId.equals(eventIdToCheck)) {
                                                    // Return true if the event ID matches
                                                    callback.onComplete(true);
                                                    return; // Exit the loop once a match is found
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Failed to fetch event: " + e.getMessage());
                                            callback.onComplete(false);  // Return false on failure
                                        });
                            }
                        } else {
                            System.out.println("No created events found for this user.");
                            callback.onComplete(false);  // Return false if no events are found
                        }
                    } else {
                        System.out.println("No user document found for ID: " + userId);
                        callback.onComplete(false);  // Return false if no user is found
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Failed to load user document: " + e.getMessage());
                    callback.onComplete(false);  // Return false on failure
                });
    }

    public interface EventCheckCallback {
        void onComplete(boolean exists);
    }

    /**
     * Initializes the fragment with the event and entrant data passed in arguments.
     *
     * @param savedInstanceState The saved instance state (if any).
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            entrant = (Entrant) getArguments().getSerializable("entrant");
        }
        database = Database.getDB();

        // Ensure the activity is AppCompatActivity
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        setHasOptionsMenu(true); // Inform FragmentManager to handle options menu}
    }

    /**
     * Called to create the view for the fragment.
     * Initializes UI elements and populates the event details.
     *
     * @param inflater           The LayoutInflater to inflate the view.
     * @param container          The parent view group.
     * @param savedInstanceState The saved instance state (if any).
     * @return The view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        // Initialize views
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventLocationTextView = view.findViewById(R.id.eventLocationTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView recurringDatesTextView = view.findViewById(R.id.recurringDatesTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);

        // Initialize Buttons
        buttonLeaveWaitlist = view.findViewById(R.id.button_leave_waitlist);
        manageButton = view.findViewById(R.id.ManageEvents);
        optionsButton = view.findViewById(R.id.EntrantManageEvents);

        invitationText = view.findViewById(R.id.invitationText);
        acceptButton = view.findViewById(R.id.Accept);
        declineButton = view.findViewById(R.id.Decline);

        // Populate event details
        if (event != null) {
            populateEventDetails(eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, eventDescriptionTextView);
            // Print the current waitlist for debugging purposes
            Log.d("EventDetailsFragment", "updateButton Event Waiting List: " + event.getWaitingList());
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }

        if (App.currentUser != null && App.currentUser.isOrganizer()) {
            checkCreatedEventsFromDatabase(event.getEventId(), exists -> {
                if (exists && manageButton != null) {
                    manageButton.setVisibility(View.VISIBLE);
                    optionsButton.setVisibility(View.GONE);

                    // Hide Leave Waitlist buttons
                    buttonLeaveWaitlist.setVisibility(View.GONE);

                    manageButton.setOnClickListener(v -> {
                        if (event != null) {
                            EventOptionsDialogFragment dialogFragment = EventOptionsDialogFragment.newInstance(event);
                            dialogFragment.show(getChildFragmentManager(), "EventOptionsDialog");
                        }
                    });
                } else {
                    assert manageButton != null;
                    manageButton.setVisibility(View.GONE);
                    optionsButton.setVisibility(View.VISIBLE);
                    optionsButton.setOnClickListener(v -> {
                        if (event != null) {
                            EntrantEventOptionsDialogFragment dialogFragment = EntrantEventOptionsDialogFragment.newInstance(event);
                            dialogFragment.show(getChildFragmentManager(), "EntrantEventOptionsDialog");
                        }
                    });
                }
            });
        } else if (App.currentUser != null && optionsButton != null) {
            optionsButton.setVisibility(View.VISIBLE);
            optionsButton.setOnClickListener(v -> {
                if (event != null) {
                    EntrantEventOptionsDialogFragment dialogFragment = EntrantEventOptionsDialogFragment.newInstance(event);
                    dialogFragment.show(getChildFragmentManager(), "EntrantEventOptionsDialog");
                }
            });
        }

        boolean isEventInPendingList = false;
        for (Event pendingEvent : ((Entrant) App.currentUser).getCurrentPendingEvents()) {
            if (pendingEvent.getEventId().equals(event.getEventId())) {
                // If event IDs match, set isEventInPendingList to true
                isEventInPendingList = true;
                break;
            }
        }
        if (isEventInPendingList) {
            acceptButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.VISIBLE);
            acceptButton.setOnClickListener(v -> {
                acceptEvent();
            });
            declineButton.setOnClickListener(v -> {
                declineEvent();
            });

            invitationText.setText("You've been selected from the wishlist!");
        } else {
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
        }

        boolean isEventInWaitlist = false;
        for (Event waitlistedEvent : ((Entrant) App.currentUser).getCurrentWaitlistedEvents()) {
            if (waitlistedEvent.getEventId().equals(event.getEventId())) {
                // If event IDs match, set isEventInWaitlist to true
                isEventInWaitlist = true;
                break;
            }
        }

        if (isEventInWaitlist) {
            buttonLeaveWaitlist.setVisibility(View.VISIBLE);
        } else {
            buttonLeaveWaitlist.setVisibility(View.GONE);
        }
        buttonLeaveWaitlist.setOnClickListener(v -> {
            leaveWaitlist();
        });

        return view;
    }

    /**
     * Shows a confirmation dialog for the entrant to leave the event's waitlist.
     * If confirmed, the entrant will be removed from the event's waitlist.
     */
    public void leaveWaitlist() {
        if (event == null || App.currentUser == null) {
            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Event> currentWaitlist = ((Entrant) App.currentUser).getCurrentWaitlistedEvents();
        currentWaitlist.removeIf(pendingEvent -> pendingEvent.getEventId().equals(event.getEventId()));
        ((Entrant) App.currentUser).setCurrentPendingEvents(currentWaitlist);

        ArrayList<User> currentEventWaitingList = event.getPendingList();
        currentEventWaitingList.removeIf(pendingUser -> pendingUser.getDeviceId().equals(App.currentUser.getDeviceId()));
        event.setPendingList(currentEventWaitingList);

        // Now move only the database operations to the background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Database operations in the background thread
            Database.getDB().insertUserDocument(App.currentUser);
            Database.getDB().insertEvent(event);
        });
        buttonLeaveWaitlist.setVisibility(View.GONE);
    }


    private void acceptEvent() {
        ArrayList<Event> currentPending = ((Entrant) App.currentUser).getCurrentPendingEvents();
        currentPending.removeIf(pendingEvent -> pendingEvent.getEventId().equals(event.getEventId()));
        ((Entrant) App.currentUser).setCurrentPendingEvents(currentPending);

        ArrayList<Event> currentEnrolled = ((Entrant) App.currentUser).getCurrentEnrolledEvents();
        currentEnrolled.add(event);
        ((Entrant) App.currentUser).setCurrentWaitlistedEvents(currentEnrolled);

        ArrayList<User> currentPendingList = event.getPendingList();
        currentPendingList.removeIf(pendingUser -> pendingUser.getDeviceId().equals(App.currentUser.getDeviceId()));
        event.setPendingList(currentPendingList);

        ArrayList<User> currentEnrolledList = event.getEnrolledList();
        currentEnrolledList.add(App.currentUser);
        event.setEnrolledList(currentEnrolledList);

        // Hide the buttons here
        acceptButton.setVisibility(View.GONE);
        declineButton.setVisibility(View.GONE);
        invitationText.setText("You've been accepted!!");

        // Now move only the database operations to the background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Database operations in the background thread
            Database.getDB().insertUserDocument(App.currentUser);
            Database.getDB().insertEvent(event);
        });
    }

    private void declineEvent() {
        ArrayList<Event> currentPending = ((Entrant) App.currentUser).getCurrentPendingEvents();
        currentPending.removeIf(pendingEvent -> pendingEvent.getEventId().equals(event.getEventId()));
        ((Entrant) App.currentUser).setCurrentPendingEvents(currentPending);

        ArrayList<Event> currentDeclined = ((Entrant) App.currentUser).getCurrentDeclinedEvents();
        currentDeclined.add(event);
        ((Entrant) App.currentUser).setCurrentWaitlistedEvents(currentDeclined);

        Database.getDB().insertUserDocument(App.currentUser);

        ArrayList<User> currentPendingList = event.getPendingList();
        currentPendingList.removeIf(pendingUser -> pendingUser.getDeviceId().equals(App.currentUser.getDeviceId()));
        event.setPendingList(currentPendingList);

        ArrayList<User> currentCancelledList = event.getCancelledList();
        currentCancelledList.add(App.currentUser);
        event.setEnrolledList(currentCancelledList);

        Database.getDB().insertEvent(event);


        // Hide the buttons here
        acceptButton.setVisibility(View.GONE);
        declineButton.setVisibility(View.GONE);
        invitationText.setText("You've been declined! Feel free to leave this page");

        // Now move only the database operations to the background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Database operations in the background thread
            Database.getDB().insertUserDocument(App.currentUser);
            Database.getDB().insertEvent(event);
        });
    }

    /**
     * Converts a short abbreviation (e.g., "M" for Monday) to a full day name.
     *
     * @param abbreviation The abbreviation for the day of the week.
     * @return The full day name (e.g., "Monday").
     */
    // Helper method to get the full name for the day of the week based on unique abbreviation
    private String getFullDayName(String abbreviation) {
        switch (abbreviation) {
            case "U": return "Sunday";
            case "M": return "Monday";
            case "T": return "Tuesday";
            case "W": return "Wednesday";
            case "R": return "Thursday";
            case "F": return "Friday";
            case "S": return "Saturday";
            default: return ""; // Handle invalid abbreviations
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Disable the back button in the action bar when exiting the fragment
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button press
            navigateBackToEventList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateBackToEventList() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack(); // Navigate back in the fragment stack
    }


    // ONLY FOR TESTING PURPOSES
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Entrant getEntrant() {
        return entrant;
    }

    public void setEntrant(Entrant entrant) {
        this.entrant = entrant;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

}
//    private void checkEntrantStatus() {
//        if (App.currentUser instanceof Entrant && event != null) {
//            Entrant currentEntrant = (Entrant) App.currentUser;
//            ArrayList<User> waitingList = event.getWaitingList();
//
//            // Compare using unique identifiers
//            boolean isOnWaitlist = false;
//            for (DocumentReference doc: waitingList) {
//                if (user.getDeviceId().equals(currentEntrant.getDeviceId())) {
//                    isOnWaitlist = true;
//                    break;
//                }
//            }
//
//            // Update button visibility
//            if (isOnWaitlist) {
//                buttonEnterNow.setVisibility(View.GONE);
//                buttonLeaveWaitlist.setVisibility(View.VISIBLE);
//                Log.d("EventDetailsFragment", "User is on the waitlist. Showing 'Leave Waitlist' button.");
//            } else {
//                buttonEnterNow.setVisibility(View.VISIBLE);
//                buttonLeaveWaitlist.setVisibility(View.GONE);
//                Log.d("EventDetailsFragment", "User is not on the waitlist. Showing 'Enter Now' button.");
//            }
//        }
//    }


//    public void joinWaitlist() {
//        if (event != null && App.currentUser != null) {
//            // Cast the current user to Entrant
//            Entrant currentEntrant = (Entrant) App.currentUser;
//
//            // Ensure the event's waiting list is initialized
//            if (event.getWaitingList() == null) {
//                event.setWaitingList(new ArrayList<>());
//            }
//            Log.d("EventDetails", "Current Event Waitlist before changes " + event.getWaitingList());
//
//            ArrayList<User> waitingList = event.getWaitingList();
//
//            // Add the entrant to the waiting list if not already present
//            if (!waitingList.contains(currentEntrant)) {
//                waitingList.add(currentEntrant);
//                event.setWaitingList(waitingList);
//
//
//                // TO FIX
//                /*
//                that "event" argument, needs to have a synced waitlist with the db and then .add("melonapopus")
//                then db.insertEvent("event")
//                it's not updating the CURRENTWaitlist
//                its rewriting it with your local waitlist
//                but the local waitlist isn't synced up
//
//                the enter now upload working from eventdetailsdialog fragment AND eventdetailsfragment->
//                 meaning on db both user will have a ref to waitlisted events of theirs and events have one to
//                 the users and that should fix the button displaying
//                 right now is that the waitlist is being added to user on the database correctly
//                 but user isn't being added to the database correctly
//                 this causes the button toggle to fail
//                 */
//                ArrayList<Event> eventList = currentEntrant.getCurrentWaitlistedEvents();
//                eventList.add(event);
//                currentEntrant.setCurrentWaitlistedEvents(eventList);
//
//                // Log the addition
//                Log.d("EventDetails", "Entrant " + currentEntrant.getDeviceId() +
//                        " successfully added to the waitlist for event " + event.getEventId());
//                Log.d("EventDetails", "Current Event Waitlist: " + waitingList);
//                Log.d("EventDetails", "Current Entrant Waitlisted Events: " + currentEntrant.getCurrentWaitlistedEvents());
//
//
//                //TO FIX: currently it displays,
////                Current Event Waitlist before changes [com.google.firebase.firestore.DocumentReference@310f0fb4]
////                2024-11-20 19:04:15.841  5176-5176  EventDetails            com.example.trojanplanner            D  Entrant d0c6ba9291492596 successfully added to the waitlist for event 19cd6a862b96402f-1732064463678
////                2024-11-20 19:04:15.841  5176-5176  EventDetails            com.example.trojanplanner            D  Current Event Waitlist: [com.google.firebase.firestore.DocumentReference@310f0fb4, Jennifer Wang (d0c6ba9291492596)]
//// we want to do the code that extracts just the useres, ths is the case when the waitlist already has someone and it displays weirdly
//                // Update the database
//                database.insertEvent(event); // Save the updated event
//                database.insertUserDocument(currentEntrant); // Save the updated entrant
//
//                // Notify the user and update UI
//                Toast.makeText(getContext(), "Added to waitlist", Toast.LENGTH_SHORT).show();
//                addtoNotifications();
//                checkEntrantStatus(); // Refresh the UI
//            } else {
//                Log.d("EventDetails", "Entrant " + currentEntrant.getDeviceId() +
//                        " is already on the waitlist for event " + event.getEventId());
//                Toast.makeText(getContext(), "You are already on the waitlist.", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
//        }
//    }