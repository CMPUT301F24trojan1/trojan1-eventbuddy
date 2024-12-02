package com.example.trojanplanner.events.entrant;

import android.content.pm.PackageManager;
import android.location.Location;
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

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextView recurringEndDateTextView = view.findViewById(R.id.recurringEndDateTextView); // initalized
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventPriceTextview = view.findViewById(R.id.ticketPriceTextView);
        TextView eventTotalSpotsTextview = view.findViewById(R.id.totalSpotsTextView);


        // Initialize Buttons
        buttonEnterNow = view.findViewById(R.id.button_enter_now);

        // Initialize close button
        ImageButton closeButton = view.findViewById(R.id.button_close);

        // Set close button listener
        closeButton.setOnClickListener(v -> dismiss());  // This will close the dialog

        // Populate event details
        if (event != null) {
            populateEventDetails(eventImageView, eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, recurringEndDateTextView, eventDescriptionTextView, eventPriceTextview, eventTotalSpotsTextview);

            if (event.getWaitlistCapacity() == event.getWaitingList().size()) {
                buttonEnterNow.setVisibility(View.GONE);
            }

            Date date = new Date();
            if (event.getWaitlistClose() != null && event.getWaitlistClose().getTime() == date.getTime()) {
                buttonEnterNow.setVisibility(View.GONE);
            }

            if (event.getWaitlistOpen() != null && date.getTime() >= event.getWaitlistOpen().getTime()) {
                // The current time is equal to or later than the waitlistOpen time
                buttonEnterNow.setVisibility(View.VISIBLE);
            } else {
                // The current time is before the waitlistOpen time
                buttonEnterNow.setVisibility(View.GONE);
            }

            for (User user: event.getWaitingList()){ // If the user is on the waitlist, hide the button
                if (user.getDeviceId().equals(App.currentUser.getDeviceId())){
                    buttonEnterNow.setVisibility(View.GONE);
                }
            }

            checkUserInList(event.getWaitingList(), buttonEnterNow);
            checkUserInList(event.getEnrolledList(), buttonEnterNow);
            checkUserInList(event.getCancelledList(), buttonEnterNow);
            checkUserInList(event.getPendingList(), buttonEnterNow);
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }

        // Set button click listeners
        buttonEnterNow.setOnClickListener(v -> joinWaitlist());

        return view;
    }


    private void checkUserInList(List<User> userList, Button buttonEnterNow) {
        if (userList != null) {
            for (User user : userList) {
                if (user != null && user.getDeviceId() != null &&
                        user.getDeviceId().equals(App.currentUser.getDeviceId())) {
                    buttonEnterNow.setVisibility(View.GONE);
                    break; // Exit loop once match is found
                }
            }
        }
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
                    //FIX we got to make sure the waitlist isn't full already
                    // Add the entrant to the event's waitlist
                    if (!syncedEvent.canAddToWaitlist()) {
                        // TO FIC: toast being like event waitlist is full or smth hmm...
                        return;
                    }
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
                                            addtoNotifications(App.currentUser.getDeviceId());
                                            App.sendAnnouncement(App.currentUser.getDeviceId(), event.getEventId() ,"Added to Waitlist, you'll be notified when your status updates.");
                                            // Save the updated entrant only after the event is successfully updated
                                            database.insertUserDocument(
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(), "Successfully added to the waitlist!", Toast.LENGTH_SHORT).show();
                                                            Log.d("EventDetails", "Entrant successfully updated in the database.");

                                                            // Validate database state
                                                            validateDatabaseState(syncedEvent, syncedEntrant);

                                                            if (event.isRequiresGeolocation()){

                                                                addLocationtoDatabase();
                                                            }
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
        buttonEnterNow.setVisibility(View.GONE);
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

    /**
     * Populates the event details in the respective text views.
     * If event details are missing, default values will be shown.
     *
     * @param eventImageView
     * @param eventNameTextView        The TextView to display the event's name.
     * @param eventLocationTextView    The TextView to display the event's location.
     * @param eventDateTextView        The TextView to display the event's start and end date.
     * @param recurringDaysTextView    The TextView to display the event's recurrence days.
     * @param recurringEndDateTextView    The TextView to display the event's recurrence end date.
     * @param eventDescriptionTextView The TextView to display the event's description.
     * @param eventPriceTextView       The TextView to display the event's price.
     * @param eventTotalSpotsTextView   The TextView to display the event's total capacity.
     */

    public void populateEventDetails(ImageView eventImageView, TextView eventNameTextView, TextView eventLocationTextView,
                                     TextView eventDateTextView, TextView recurringDaysTextView,
                                     TextView recurringEndDateTextView, TextView eventDescriptionTextView,
                                     TextView eventPriceTextView, TextView eventTotalSpotsTextView) {

        eventImageView.setImageBitmap(event.getPicture());

        eventNameTextView.setText("Event: " + event.getName());
        if (event.getFacility() != null) {
            eventLocationTextView.setText("\uD83D\uDCCD Facility: " + event.getFacility().getName());
        }

        // Default values for dates
        String defaultDate = "Not Available";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        String startDate = (event.getStartDateTime() != null) ? dateFormat.format(event.getStartDateTime()) : defaultDate;
        String endDate = (event.getEndDateTime() != null) ? dateFormat.format(event.getEndDateTime()) : defaultDate;

        eventDateTextView.setText("⏰ Time: " + startDate + " - " + endDate);

        // Set the price and total spots
        eventPriceTextView.setText("\uD83D\uDCB5 Cost: $" + event.getPrice());
        eventTotalSpotsTextView.setText("\uD83E\uDE91 Total Spots: " + event.getTotalSpots());

        // Set the recurring days
        if (event.isRecurring()) {
            List<String> recurrenceDays = event.getRecurrenceDays();
            if (recurrenceDays != null && !recurrenceDays.isEmpty()) {
                String recurringDays = recurrenceDays.stream()
                        .map(day -> day.substring(0, 1).toUpperCase()) // Convert to shorthand
                        .reduce((a, b) -> a + " " + b)
                        .orElse("None");
                recurringDaysTextView.setText("🔄 Recurring Days: " + recurringDays);
            } else {
                recurringDaysTextView.setText("🔄 Recurring Days: None");
            }

            // Set the recurring end date
            if (event.getRecurrenceEndDate() != null) {
                String recurrenceEndDate = dateFormat.format(event.getRecurrenceEndDate());
                recurringEndDateTextView.setText("📅 Recurrence End Date: " + recurrenceEndDate);
            } else {
                recurringEndDateTextView.setText("📅 Recurrence End Date: None");
            }
        } else {
            recurringDaysTextView.setText("🔄 Recurring Days: None");
            recurringEndDateTextView.setText("📅 Recurrence End Date: None");
        }

        // Set the event description
        eventDescriptionTextView.setText("Description: " + event.getDescription());
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


    private void addtoNotifications(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notifications", "Successfully subscribed to the topic: " + topic);
                    } else {
                        Log.e("Notifications", "Failed to subscribe to the topic: " + topic + ". Error: " + task.getException());
                    }
                });
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