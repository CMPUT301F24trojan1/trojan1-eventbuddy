package com.example.trojanplanner.events;

import android.app.AlertDialog;
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
import com.example.trojanplanner.model.ConcreteEvent;

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
import java.util.Set;

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
    private Button buttonEnterNow;
    private Button buttonLeaveWaitlist;
    private Button manageButton;
    private Button optionsButton;

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
     * Checks the current status of the entrant (whether they are in the waitlist).
     * Based on the status, appropriate buttons (Join Waitlist or Leave Waitlist) will be displayed.
     */
    private void checkEntrantStatus() {
        if (event != null && event.getWaitingList() != null) {
            if (event.getWaitingList().contains(entrant)) {
                buttonEnterNow.setVisibility(View.GONE);
                buttonLeaveWaitlist.setVisibility(View.VISIBLE);
            } else {
                buttonEnterNow.setVisibility(View.VISIBLE);
                buttonLeaveWaitlist.setVisibility(View.GONE);
            }
        } else {
            Log.e("EventDetailsFragment", "Event or waiting list is null in checkEntrantStatus");
        }
    }

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
        if (event != null && App.currentUser != null) {
            // Cast the current user to Entrant
            Entrant currentEntrant = (Entrant) App.currentUser;

            // Ensure the event's waiting list is initialized
            if (event.getWaitingList() == null) {
                event.setWaitingList(new ArrayList<>());
            }
            Log.d("EventDetails", "Current Event Waitlist before changes " + event.getWaitingList());

            ArrayList<User> waitingList = event.getWaitingList();

            // Add the entrant to the waiting list if not already present
            if (!waitingList.contains(currentEntrant)) {
                waitingList.add(currentEntrant);
                event.setWaitingList(waitingList);

                ArrayList<Event> eventList = currentEntrant.getCurrentWaitlistedEvents();
                eventList.add(event);
                currentEntrant.setCurrentWaitlistedEvents(eventList);

                // Log the addition
                Log.d("EventDetails", "Entrant " + currentEntrant.getDeviceId() +
                        " successfully added to the waitlist for event " + event.getEventId());
                Log.d("EventDetails", "Current Event Waitlist: " + waitingList);
                Log.d("EventDetails", "Current Entrant Waitlisted Events: " + currentEntrant.getCurrentWaitlistedEvents());

                // Update the database
                database.insertEvent(event); // Save the updated event
                database.insertUserDocument(currentEntrant); // Save the updated entrant

                // Notify the user and update UI
                Toast.makeText(getContext(), "Added to waitlist", Toast.LENGTH_SHORT).show();
                addtoNotifications();
                checkEntrantStatus(); // Refresh the UI
            } else {
                Log.d("EventDetails", "Entrant " + currentEntrant.getDeviceId() +
                        " is already on the waitlist for event " + event.getEventId());
                Toast.makeText(getContext(), "You are already on the waitlist.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Event or User data is missing.", Toast.LENGTH_SHORT).show();
        }
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

            // Subscribe the user to the event's notification topic
            FirebaseMessaging.getInstance().subscribeToTopic(eventId)
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



    /**
     * Shows a confirmation dialog for the entrant to leave the event's waitlist.
     * If confirmed, the entrant will be removed from the event's waitlist.
     */
    public void leaveWaitlist() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_registration, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView textEventInfo = dialogView.findViewById(R.id.text_event_info);
        textEventInfo.setText("Are you sure you want to leave the waitlist for:\n" + event.getName());

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(v -> {

            event.removeParticipant(entrant);
            entrant.removeWaitlistedEvent(event);

            database.insertEvent(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    database.getEntrant(new Database.QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {
                            Entrant existingEntrant = (Entrant) object;
                            existingEntrant.removeWaitlistedEvent(event);

                            database.insertUserDocument(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Removed from waitlist", Toast.LENGTH_SHORT).show();
                                    checkEntrantStatus();
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to update entrant in the database", Toast.LENGTH_SHORT).show();
                                }
                            }, existingEntrant);
                        }
                    }, new Database.QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            Toast.makeText(getContext(), "Entrant not found in the database", Toast.LENGTH_SHORT).show();
                        }
                    }, entrant.getDeviceId());
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to update event in the database", Toast.LENGTH_SHORT).show();
                }
            }, event);

            dialog.dismiss();
        });

        dialog.show();
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
        buttonEnterNow = view.findViewById(R.id.button_enter_now);
        buttonLeaveWaitlist = view.findViewById(R.id.button_leave_waitlist);
        manageButton = view.findViewById(R.id.ManageEvents);
        optionsButton = view.findViewById(R.id.EntrantManageEvents);

        // Populate event details
        if (event != null) {
            populateEventDetails(eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, eventDescriptionTextView);
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }

        // Check entrant status
        if (event != null) {
            checkEntrantStatus();
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }

        // If the user is an organizer, show manage button
        if (App.currentUser != null && App.currentUser.isOrganizer()) {
            checkCreatedEventsFromDatabase(event.getEventId(), exists -> {
                if (exists && manageButton != null) {
                    manageButton.setVisibility(View.VISIBLE);
                    optionsButton.setVisibility(View.GONE);
                    manageButton.setOnClickListener(v -> {
                        if (event != null) {
                            EventOptionsDialogFragment dialogFragment = EventOptionsDialogFragment.newInstance(event);
                            dialogFragment.show(getChildFragmentManager(), "EventOptionsDialog");
                        }
                    });
                    System.out.println("Event created by current Organizer, displaying organizer options!");
                } else {
                    assert manageButton != null;
                    manageButton.setVisibility(View.GONE);
                    optionsButton.setVisibility(View.VISIBLE);
                    System.out.println("Event is not created by current Organizer.");
                }});
        } else if (App.currentUser != null && optionsButton != null) {
            optionsButton.setVisibility(View.VISIBLE);
            System.out.println("User is not an organizer, displaying entrant options!");
        }

        // Set button click listeners
        buttonEnterNow.setOnClickListener(v -> joinWaitlist());
        buttonLeaveWaitlist.setOnClickListener(v -> leaveWaitlist());

        return view;
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
        // Assuming you have a `EventsListFragment` to return to
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack(); // Navigate back in the fragment stack

        // Alternatively, if no back stack is used:
        // Fragment eventsListFragment = new EventsListFragment();
        // fragmentManager.beginTransaction()
        //         .replace(R.id.fragment_container, eventsListFragment)
        //         .commit();
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

/*

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


// no longer used since we have joinWaitlist and leaveWaitlist
public void showConfirmDialog() {
    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_registration, null);
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setView(dialogView);
    AlertDialog dialog = builder.create();

    TextView textEventInfo = dialogView.findViewById(R.id.text_event_info);
    textEventInfo.setText(event.getName() + "\n" + event.getFacility().getFacilityId() + "\n" +
            event.getStartDateTime() + " - " + event.getEndDateTime() + "\n" +
            event.getRecurrenceDays() + "\n" + event.getDescription());

    dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
    dialogView.findViewById(R.id.button_confirm).setOnClickListener(v -> {

        // IS THIS NEEDED
        event.addParticipant(entrant);
        entrant.addWaitlistedEvent(event);

        database.insertEvent(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Check if the entrant exists in the database
                database.getEntrant(new Database.QuerySuccessAction() {
                    @Override
                    public void OnSuccess(Object object) {
                        Entrant existingEntrant = (Entrant) object;
                        existingEntrant.addWaitlistedEvent(event);

                        // Update the entrant in the database
                        database.insertUserDocument(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Added to waitlist", Toast.LENGTH_SHORT).show();
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to update entrant in the database", Toast.LENGTH_SHORT).show();
                            }
                        }, existingEntrant);
                    }
                }, new Database.QueryFailureAction() {
                    @Override
                    public void OnFailure() {
                        Toast.makeText(getContext(), "Entrant not found in the database", Toast.LENGTH_SHORT).show();
                    }
                }, entrant.getDeviceId());
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to add event to the database", Toast.LENGTH_SHORT).show();
            }
        }, event);

        dialog.dismiss();
    });

    dialog.show();
}
 */