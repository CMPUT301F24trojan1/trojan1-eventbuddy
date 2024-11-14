package com.example.trojanplanner.events;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.HelperFragments.WaitlistFragment;
import com.example.trojanplanner.QRUtils.QRCodeUtil;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment that allows the user to edit the details of an existing event.
 * The event details such as name, available spots, description, date, time, and price
 * can be updated. This fragment also allows viewing the event's waitlist.
 */
public class EventEditFragment extends Fragment {

    private Event event;

    private EditText eventNameEditText;
    private EditText eventSpotsAvailableEditText;
    private EditText eventDescriptionEditText;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText eventPriceEditText;
    private Button saveChangesButton;
    private Button viewWaitlistButton;
    private Button returnToEventsButton;
    private Button generateQRCodeButton;
    private Button sendAnnouncementButton;
    private Button viewMapButton;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    /**
     * Called when the fragment's view is created. Initializes UI components
     * and sets up listeners for buttons. Also retrieves the event details
     * from arguments and populates the form fields with those values.
     *
     * @param inflater The LayoutInflater used to inflate the fragment's view.
     * @param container The container where the fragment's view will be attached.
     * @param savedInstanceState Any saved instance state from a previous instance of the fragment.
     * @return The view for this fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_edit_fragment, container, false);

        // Initialize UI components
        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        eventSpotsAvailableEditText = view.findViewById(R.id.eventSpotsAvailableEditText);
        eventDescriptionEditText = view.findViewById(R.id.eventDescriptionEditText);
        eventDateEditText = view.findViewById(R.id.eventDateEditText);
        eventTimeEditText = view.findViewById(R.id.eventTimeEditText);
        eventPriceEditText = view.findViewById(R.id.eventPriceEditText);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        viewWaitlistButton = view.findViewById(R.id.viewWaitlistButton);
        returnToEventsButton = view.findViewById(R.id.returnToEventsButton);
        generateQRCodeButton = view.findViewById(R.id.generateQRCodeButton);
        sendAnnouncementButton = view.findViewById(R.id.sendAnnouncementButton);
        viewMapButton = view.findViewById(R.id.viewMapButton);

        // Retrieve the Event object from arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }

        if (event == null) {
            Log.e("EventSettingsFragment", "Event is null!");
            Toast.makeText(getContext(), "Event data is unavailable", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Populate UI with event data
        populateEventDetails();

        // Set up button click listeners
        saveChangesButton.setOnClickListener(v -> saveChanges());
        viewWaitlistButton.setOnClickListener(v -> openWaitlistFragment());
        returnToEventsButton.setOnClickListener(v -> returnToEvents());
        generateQRCodeButton.setOnClickListener(v -> generateQRCode());
        sendAnnouncementButton.setOnClickListener(v -> sendAnnouncement());
        viewMapButton.setOnClickListener(v -> viewEventMap());
        return view;
    }

    /**
     * Populates the UI with the details of the event.
     * This method is called after retrieving the event object.
     */
    private void populateEventDetails() {
        eventNameEditText.setText(event.getName());
        eventSpotsAvailableEditText.setText(String.valueOf(event.getTotalSpots()));
        eventDescriptionEditText.setText(event.getDescription());

        // Format and set date and time
        eventDateEditText.setText(dateFormat.format(event.getStartDateTime()));
        eventTimeEditText.setText(timeFormat.format(event.getStartDateTime()));
        eventPriceEditText.setText(String.valueOf(event.getPrice()));
    }

    /**
     * Saves the changes made to the event. Validates the input fields and
     * updates the event object with new values.
     */
    private void saveChanges() {
        String name = eventNameEditText.getText().toString().trim();
        String spotsAvailableText = eventSpotsAvailableEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        String dateText = eventDateEditText.getText().toString().trim();
        String timeText = eventTimeEditText.getText().toString().trim();
        String priceText = eventPriceEditText.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || spotsAvailableText.isEmpty() || description.isEmpty()
                || dateText.isEmpty() || timeText.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long spotsAvailable = Long.parseLong(spotsAvailableText); // Convert spots to int
            float price = Float.parseFloat(priceText);                 // Convert price to float
            Date date = dateFormat.parse(dateText);                    // Parse date
            Date time = timeFormat.parse(timeText);                    // Parse time

            // Update event object with new values
            event.setName(name);
            event.setTotalSpots(spotsAvailable);                       // Set spots as int
            event.setDescription(description);
            event.setStartDateTime(combineDateAndTime(date, time));    // Combine date and time
            event.setPrice(price);                                     // Set price as float

            // Here, you could save the event to the database if needed
            // For example:
            // database.saveEvent(event);

            Toast.makeText(getContext(), "Event details updated successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException | ParseException e) {
            Toast.makeText(getContext(), "Invalid input format", Toast.LENGTH_SHORT).show();
            Log.e("EventSettingsFragment", "Error parsing inputs", e);
        }
    }

    /**
     * Combines a date and time into a single Date object.
     * The time is added to the date, ensuring that the event's start date and time are correctly represented.
     *
     * @param date The date of the event.
     * @param time The time of the event.
     * @return A new Date object representing the combined date and time.
     */
    private Date combineDateAndTime(Date date, Date time) {
        // Combine date and time into a single Date object
        long combinedTime = date.getTime() + time.getTime() % (24 * 60 * 60 * 1000);
        return new Date(combinedTime);
    }


    /**
     * Navigates to the WaitlistFragment, passing the event details to it.
     * This allows the user to view the event's waitlist.
     */
    private void openWaitlistFragment() {
        // Navigate to WaitlistFragment with the event
        WaitlistFragment waitlistFragment = new WaitlistFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        waitlistFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, waitlistFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Returns the user to the events list by popping the current fragment from the back stack.
     */
    private void returnToEvents() {
        // Go back to the previous fragment
        getParentFragmentManager().popBackStack();
    }

    private void generateQRCode() {
        if (event == null || event.getEventId() == null) {
            Toast.makeText(getContext(), "Event ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash the eventID using SHA-256
        String hashedEventID = QRCodeUtil.hashText(event.getEventId());

        if (hashedEventID == null) {
            Toast.makeText(getContext(), "Failed to hash Event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the hashed event ID to generate the QR code
        Bitmap qrCodeBitmap = QRCodeUtil.generateQRCode(hashedEventID);

        if (qrCodeBitmap != null) {
            // Show the QR code in a popup
            showQRCodeDialog(qrCodeBitmap);
        } else {
            Toast.makeText(getContext(), "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }


    private void showQRCodeDialog(Bitmap qrCodeBitmap) {
        // Create an ImageView to display the QR code
        ImageView qrCodeImageView = new ImageView(getContext());
        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        // Create an AlertDialog to display the QR code
        new AlertDialog.Builder(getContext())
                .setTitle("Event QR Code")
                .setMessage("Scan the QR code below:")
                .setView(qrCodeImageView) // Set the QR code image as the content of the dialog
                .setPositiveButton("Close", null) // Close the dialog when the button is pressed
                .create()
                .show();
    }


    private void sendAnnouncement() {
        if (event == null || event.getEventId() == null) {
            Toast.makeText(getContext(), "Event ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create campaign data based on the event
        Map<String, Object> campaignData = new HashMap<>();
        campaignData.put("eventId", event.getEventId());
        campaignData.put("eventName", event.getName());
        campaignData.put("announcementTime", System.currentTimeMillis());  // Current timestamp
        campaignData.put("message", "Check out this exciting event!"); // Custom message

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Store the campaign in the 'campaigns' collection with the event ID as document ID
        DocumentReference campaignRef = db.collection("campaigns").document(event.getEventId());

        // Set the campaign data in Firestore
        campaignRef.set(campaignData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Announcement sent successfully!", Toast.LENGTH_SHORT).show();
                    // Optional: Send push notification if required
                    sendPushNotification(event.getEventId(), "New Event Announcement!", "Don't miss out on " + event.getName());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to send announcement", Toast.LENGTH_SHORT).show();
                });
    }


    private void sendPushNotification(String eventId, String title, String body) {
        // Create the RemoteMessage to send to the topic (or device token)
        RemoteMessage message = new RemoteMessage.Builder("/topics/" + eventId)  // Using topic as the recipient
                .setMessageId(Integer.toString((int) System.currentTimeMillis()))  // Unique message ID
                .addData("title", title)  // Add custom data (title)
                .addData("body", body)    // Add custom data (body)
                .build();

        try {
            // Send the message to the topic or device token
            FirebaseMessaging.getInstance().send(message);

            // Log success
            Log.d("FCM", "Push notification sent successfully!");

        } catch (Exception e) {
            // Handle any errors during message sending
            Log.e("FCM", "Failed to send push notification", e);
        }
    }

    private void viewEventMap() {
        // Implement the logic for viewing the event's location on a map
        Toast.makeText(getContext(), "View event map logic goes here", Toast.LENGTH_SHORT).show();
    }
}
