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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private void populateEventDetails() {
        eventNameEditText.setText(event.getName());
        eventSpotsAvailableEditText.setText(String.valueOf(event.getTotalSpots()));
        eventDescriptionEditText.setText(event.getDescription());

        // Format and set date and time
        eventDateEditText.setText(dateFormat.format(event.getStartDateTime()));
        eventTimeEditText.setText(timeFormat.format(event.getStartDateTime()));
        eventPriceEditText.setText(String.valueOf(event.getPrice()));
    }

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

    private Date combineDateAndTime(Date date, Date time) {
        // Combine date and time into a single Date object
        long combinedTime = date.getTime() + time.getTime() % (24 * 60 * 60 * 1000);
        return new Date(combinedTime);
    }

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
        // Implement the logic for sending an announcement about the event
        Toast.makeText(getContext(), "Send announcement logic goes here", Toast.LENGTH_SHORT).show();
    }

    private void viewEventMap() {
        // Implement the logic for viewing the event's location on a map
        Toast.makeText(getContext(), "View event map logic goes here", Toast.LENGTH_SHORT).show();
    }
}
