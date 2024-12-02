package com.example.trojanplanner.events;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.trojanplanner.ProfileUtils.PfpClickPopupFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.events.organizer.EventOptionsDialogFragment;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private PhotoPicker photoPicker;

    private ImageView eventImageView;
    private Button buttonLeaveWaitlist;
    private Button manageButton;
    private Button acceptButton, declineButton, edit_poster;
    private TextView invitationText;
    private boolean isEventInWaitlist = false;
    boolean isEventInPendingList = false;

    // Required empty constructor
    public EventDetailsFragment() {}

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

        // Set the event image
        eventImageView.setImageBitmap(event.getPicture());

        // Set the event name
        eventNameTextView.setText("Event: " + event.getName());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            // Background thread: Database operations
            Database.getDB().getFacilityByEventID(event.getEventId(), facilityID -> {
                Database.getDB().getFacility(object -> {
                    Facility facility = (Facility) object;

                    // Switch to the main thread for UI updates
                    mainHandler.post(() -> {
                        Toast.makeText(getContext(), "Facility: " + facility.getName(), Toast.LENGTH_SHORT).show();
                        event.setFacility(facility);

                        if (event.getFacility() != null && event.getFacility().getName() != null) {
                            eventLocationTextView.setText("\uD83D\uDCCD Facility: " + event.getFacility().getName());
                        } else {
                            eventLocationTextView.setText("\uD83D\uDCCD Facility: Not Available");
                        }
                    });
                }, () -> {
                    // Handle failure case (Facility not found)
                    mainHandler.post(() -> {
                        eventLocationTextView.setText("\uD83D\uDCCD Facility: Not Available");
                    });
                }, (String) facilityID);
            }, () -> {
                // Handle failure case (Event not found)
                mainHandler.post(() -> {
                    eventLocationTextView.setText("\uD83D\uDCCD Facility: Not Available");
                });
            });
        });


        // Default values for dates in case they are null
        String defaultDate = "Not Available";  // Default date if event date is null
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        // Assign default value if startDateTime or endDateTime is null
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

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Perform the database operation asynchronously using the Executor
        executorService.execute(() -> {
            // Perform the database query in the background thread
            database.getEvent(object -> {
                event = (Event) object;
                // Update UI or handle event here
                getActivity().runOnUiThread(() -> {
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

                    for (Event waitlistedEvent : ((Entrant) App.currentUser).getCurrentWaitlistedEvents()) {
                        if (waitlistedEvent.getEventId().equals(event.getEventId())) {
                            isEventInWaitlist = true;
                            break;
                        }
                    }

                    if (isEventInWaitlist) {
                        buttonLeaveWaitlist.setVisibility(View.VISIBLE);
                    } else {
                        buttonLeaveWaitlist.setVisibility(View.GONE);
                    }
                });
            }, () -> {
                // Handle failure or empty response
                getActivity().runOnUiThread(() -> {
                    // Handle failure here (show error message, etc.)
                });
            }, event.getEventId());
        });

        // Make sure to shut down the executor when done
        executorService.shutdown();

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
        eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventLocationTextView = view.findViewById(R.id.eventLocationTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView recurringDaysTextView = view.findViewById(R.id.recurringDaysTextView);
        TextView recurringEndDateTextView = view.findViewById(R.id.recurringEndDateTextView); // initalized
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventPriceTextview = view.findViewById(R.id.ticketPriceTextView);
        TextView eventTotalSpotsTextview = view.findViewById(R.id.totalSpotsTextView);

        // Initialize Buttons
        buttonLeaveWaitlist = view.findViewById(R.id.button_leave_waitlist);
        manageButton = view.findViewById(R.id.ManageEvents);
        edit_poster = view.findViewById(R.id.edit_poster);
        invitationText = view.findViewById(R.id.invitationText);
        acceptButton = view.findViewById(R.id.Accept);
        declineButton = view.findViewById(R.id.Decline);

        // Initialize the photoPicker, borrowing the one from MainActivity
        photoPicker = ((MainActivity) App.activity).mainActivityPhotoPicker;
        PhotoPicker.PhotoPickerCallback photoPickerCallback = new PhotoPicker.PhotoPickerCallback() {
            @Override
            public void OnPhotoPickerFinish(Bitmap bitmap) {
                onPhotoSelected(bitmap);
            }
        };
        photoPicker.setCallback(photoPickerCallback);


        // Populate event details
        if (event != null) {
            populateEventDetails(eventImageView, eventNameTextView, eventLocationTextView, eventDateTextView, recurringDaysTextView, recurringEndDateTextView, eventDescriptionTextView, eventPriceTextview, eventTotalSpotsTextview);
            // Print the current waitlist for debugging purposes
            Log.d("EventDetailsFragment", "updateButton Event Waiting List: " + event.getWaitingList());
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }

        if (App.currentUser != null && App.currentUser.isOrganizer()) {
            checkCreatedEventsFromDatabase(event.getEventId(), exists -> {
                if (exists && manageButton != null) {
                    manageButton.setVisibility(View.VISIBLE);
                    edit_poster.setVisibility(View.VISIBLE);
                    invitationText.setVisibility(View.GONE);

                    // Hide Leave Waitlist buttons
                    buttonLeaveWaitlist.setVisibility(View.GONE);

                    manageButton.setOnClickListener(v -> {
                        if (event != null) {
                            EventOptionsDialogFragment dialogFragment = EventOptionsDialogFragment.newInstance(event);
                            dialogFragment.show(getChildFragmentManager(), "EventOptionsDialog");
                        }
                    });

                    edit_poster.setOnClickListener(v -> {
                        // Handle edit poster button click
                        createPfpPopup();
                    });
                } else {
                    assert manageButton != null;
                    manageButton.setVisibility(View.GONE);
                    edit_poster.setVisibility(View.GONE);
                }
            });
        }

        buttonLeaveWaitlist.setOnClickListener(v -> {
            leaveWaitlist();
        });

        return view;
    }


    /**
     * The action taken when the 'Edit event poster' button is clicked. It creates a popup which
     * allows either changing the event photo or removing it.
     */
    private void createPfpPopup() {
        PfpClickPopupFragment.PfpPopupFunctions popupFunctions = new PfpClickPopupFragment.PfpPopupFunctions() {
            @Override
            public void changePFP() {
                photoPicker.openPhotoPicker();
            }
            @Override
            public void removePFP() {
                clearPFP();
            }
        };

        new PfpClickPopupFragment(popupFunctions).show(((AppCompatActivity) App.activity).getSupportFragmentManager(), "Change Event Poster");
    }


    /**
     * The callback function triggered when the PhotoPicker selects an image. When this happens
     * the event banner will be saved and uploaded with the new image.
     *
     * @param bitmap The new image to save as the event photo (unless it's null)
     */
    private void onPhotoSelected(Bitmap bitmap) {
        System.out.println("EventDetailsFragment photopickercallback triggered!");
        // As long as the bitmap isn't null (photoPicker returned an actual image), set it as the
        // new event poster and upload
        if (bitmap != null) {
            // Upload the new photo, delete the old one and update the event document
            // Assuming event is not null for this
            String oldPfpFilepath = event.getPictureFilePath();
            String newPfpFilepath = event.getFacility().getOwner().getDeviceId() + "/" + System.currentTimeMillis() + ".png";
            event.setPictureFilePath(newPfpFilepath);
            event.setPicture(bitmap);
            eventImageView.setImageBitmap(bitmap);

            if (oldPfpFilepath != null) {
                database.deleteImage(oldPfpFilepath);
            }

            database.uploadImage(bitmap, event.getFacility().getOwner(), newPfpFilepath);
            database.insertEvent(event);

        }
    }


    /**
     * Clears the photo of the event, resetting it to the default value. This change is immediately
     * saved in the database.
     */
    private void clearPFP() {
        eventImageView.setImageBitmap(Event.getDefaultPicture());

        // Delete the image from Firebase Storage if there was one, otherwise just update the event
        if (event.getPictureFilePath() != null) {
            database.deleteImage(event.getPictureFilePath());
        }

        event.setPicture(null); // resets to default
        event.setPictureFilePath(null);
        database.insertEvent(event);

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
        currentWaitlist.removeIf(waitingEvent -> waitingEvent.getEventId().equals(event.getEventId()));
        ((Entrant) App.currentUser).setCurrentWaitlistedEvents(currentWaitlist);

        ArrayList<User> currentEventWaitingList = event.getWaitingList();
        currentEventWaitingList.removeIf(waitingUser -> waitingUser.getDeviceId().equals(App.currentUser.getDeviceId()));
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
        ((Entrant) App.currentUser).setCurrentEnrolledEvents(currentEnrolled);

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
        ((Entrant) App.currentUser).setCurrentDeclinedEvents(currentDeclined);

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