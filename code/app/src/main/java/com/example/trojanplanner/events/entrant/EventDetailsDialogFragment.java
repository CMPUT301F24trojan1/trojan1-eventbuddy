package com.example.trojanplanner.events.entrant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventDetailsDialogFragment extends DialogFragment {
    private Event event;
    private Entrant entrant;
    private Database database;
    private Button buttonEnterNow;

    @NonNull
    public static EventDetailsDialogFragment newInstance(Event event, Entrant entrant) {
        EventDetailsDialogFragment fragment = new EventDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        args.putSerializable("entrant", entrant);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.dialog_eventdetails, container, false);

        // Initialize views
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventLocationTextView = view.findViewById(R.id.eventLocationTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView recurringDatesTextView = view.findViewById(R.id.recurringDatesTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);

        // Initialize Buttons
        buttonEnterNow = view.findViewById(R.id.button_enter_now);

        // Initialize close button
        ImageButton closeButton = view.findViewById(R.id.button_close);

        // Set close button listener
        closeButton.setOnClickListener(v -> dismiss());  // This will close the dialog

        // Populate event details
        if (event != null) {
            populateEventDetails(eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, eventDescriptionTextView);
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }

        // Set button click listeners
        buttonEnterNow.setOnClickListener(v -> joinWaitlist());

        return view;
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
                object -> {
                    Event syncedEvent = (Event) object;

                    // Ensure the event's waiting list is initialized
                    if (syncedEvent.getWaitingList() == null) {
                        syncedEvent.setWaitingList(new ArrayList<>());
                    }

                    // Log the current waitlist
                    Log.d("EventDetails", "Current Waitlist:" + syncedEvent.getWaitingList());

                    // Prevent duplicates in the waitlist
                    if (syncedEvent.getWaitingList().contains(currentEntrant)) {
                        Toast.makeText(getContext(), "You are already on the waitlist.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add the entrant to the event's waitlist
                    syncedEvent.getWaitingList().add(currentEntrant);

                    database.getEntrant(
                            object1 -> {
                                Entrant syncedEntrant = (Entrant) object1;

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
                                database.insertEvent(
                                        (OnSuccessListener<Void>) unused -> {
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
                                                            addLocationtoDatabase();
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
                                        },
                                        e -> {
                                            Toast.makeText(getContext(), "Failed to update event data.", Toast.LENGTH_SHORT).show();
                                            Log.e("EventDetails", "Error updating event: " + e.getMessage());
                                        },
                                        syncedEvent // Pass the correct Event object
                                );
                            },
                            () -> {
                                Toast.makeText(getContext(), "Failed to sync user data.", Toast.LENGTH_SHORT).show();
                                Log.e("EventDetails", "Error syncing entrant from database.");
                            },
                            currentEntrant.getDeviceId()
                    );
                },
                () -> {
                    Toast.makeText(getContext(), "Failed to sync event data.", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetails", "Error syncing event from database.");
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

    private void checkEntrantStatus() {
        if (event != null && event.getWaitingList() != null) {
            if (event.getWaitingList().contains(entrant)) {
                buttonEnterNow.setVisibility(View.GONE);
            } else {
                buttonEnterNow.setVisibility(View.VISIBLE);
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
     * Converts a short abbreviation (e.g., "M" for Monday) to a full day name.
     *
     * @param abbreviation The abbreviation for the day of the week.
     * @return The full day name (e.g., "Monday").
     */
    // Helper method to get the full name for the day of the week based on unique abbreviation
    private String getFullDayName(String abbreviation) {
        switch (abbreviation) {
            case "U":
                return "Sunday";
            case "M":
                return "Monday";
            case "T":
                return "Tuesday";
            case "W":
                return "Wednesday";
            case "R":
                return "Thursday";
            case "F":
                return "Friday";
            case "S":
                return "Saturday";
            default:
                return ""; // Handle invalid abbreviations
        }
    }

    public void addLocationtoDatabase() {
        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        // Initialize FusedLocationProviderClient to get current location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Get the last known location
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Get the current location
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Get user and event IDs
                            String userId = App.currentUser.getDeviceId();
                            String eventId = event.getEventId();

                            // Insert the location into the database
                            database.insertLocation(eventId, userId, latitude, longitude, null, null);
                        } else {
                            // Handle failure to get location
                            Log.e("Location", "Failed to get location.");
                        }
                    }
                });
    }
}