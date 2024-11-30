package com.example.trojanplanner.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.util.ArrayList;

public class WaitlistAdapter extends ArrayAdapter<Entrant> {

    private final Context context;
    private final ArrayList<Entrant> entrants;
    private final Event event;
    private final String listType;

    public WaitlistAdapter(Context context, ArrayList<Entrant> entrants, Event event, String listType) {
        super(context, R.layout.item_waitlist, entrants);  // Add the entrants list here
        this.context = context;
        this.entrants = entrants != null ? entrants : new ArrayList<>();
        this.event = event;
        this.listType = listType;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Log.d("WaitlistAdapter", "Binding view for position: " + position);
        ViewHolder viewHolder;

        // Check if the convertView is null and inflate it if necessary
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_waitlist, parent, false);

            // Initialize ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.firstNameTextView = convertView.findViewById(R.id.firstNameTextView);
            viewHolder.lastNameTextView = convertView.findViewById(R.id.lastNameTextView);
            viewHolder.deviceIdTextView = convertView.findViewById(R.id.DeviceIDView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the Entrant object at the current position
        Entrant entrant = entrants.get(position);

        // Log the entrant data
        Log.d("WaitlistAdapter", "Binding data for Entrant at position " + position + ": " +
                "First Name: " + entrant.getFirstName() + ", " +
                "Last Name: " + entrant.getLastName() + ", " +
                "Device ID: " + entrant.getDeviceId());

        // Set the data to the TextViews
        viewHolder.firstNameTextView.setText(entrant.getFirstName());
        viewHolder.lastNameTextView.setText(entrant.getLastName());
        viewHolder.deviceIdTextView.setText(entrant.getDeviceId());

        // Set an on-click listener to show the full profile in a dialog
        convertView.setOnClickListener(v -> showProfilePopup(entrant));

        return convertView;
    }

    // Method to show the profile popup dialog
    private void showProfilePopup(Entrant entrant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.item_waitlist_popup, null);

        // Initialize views for the popup
        TextView deviceIdView = popupView.findViewById(R.id.DeviceIDView);
        ImageView profilePicture = popupView.findViewById(R.id.profilePicture);
        TextView firstNameTextView = popupView.findViewById(R.id.firstNameTextView);
        TextView lastNameTextView = popupView.findViewById(R.id.lastNameTextView);
        TextView emailTextView = popupView.findViewById(R.id.emailTextView);
        TextView phoneNumberView = popupView.findViewById(R.id.phone_number_View);
        Button deleteButton = popupView.findViewById(R.id.deleteButton);

        // Populate popup fields with entrant data
        deviceIdView.setText("DeviceID: " + entrant.getDeviceId());
        firstNameTextView.setText("First Name: " + entrant.getFirstName());
        lastNameTextView.setText("Last Name: " +entrant.getLastName());
        emailTextView.setText("Email: " +entrant.getEmail());
        phoneNumberView.setText("contact: " + entrant.getPhoneNumber());
        profilePicture.setImageBitmap(entrant.getPfpBitmap());

        if (listType != null) {
            if (listType.equals("cancelled")) {
                deleteButton.setVisibility(View.GONE);
            } else if (!listType.equals("cancelled")){
                deleteButton.setOnClickListener(v -> {
                    showDeleteConfirmationDialog(entrant);
                });
            }
        }

        // Set up the dialog and show it
        builder.setView(popupView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(Entrant entrant) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

        // Set the title and message of the dialog
        builder1.setTitle("Delete Entry")
                .setMessage("Are you sure you want to remove this Entrant from your event?\nThey will not be able to join again\n\nThis action cannot be undone.")
                .setCancelable(false) // Prevents the dialog from being canceled when touched outside

                // Set the "Yes" button and its action
                .setPositiveButton("Yes", (dialog, id) -> {
                    cancelEntrant(entrant); // Perform deletion action
                    dialog.dismiss(); // Dismiss the confirmation dialog
                })

                // Set the "No" button and its action
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss(); // Simply dismiss the dialog if "No" is clicked
                });

        // Show the confirmation dialog
        AlertDialog alert = builder1.create();
        alert.show();
    }

    private void cancelEntrant(Entrant entrant) {
        if (event != null) {
            if (listType != null) {
                switch (listType) {
                    case "waiting": {
                        ArrayList<User> waitingList = event.getWaitingList();
                        waitingList.removeIf(user -> user.getDeviceId().equals(entrant.getDeviceId()));
                        event.setWaitingList(waitingList);
                        ArrayList<User> cancelledList = event.getCancelledList();
                        cancelledList.add(entrant);
                        event.setCancelledList(cancelledList);
                        break;
                    }
                    case "pending": {
                        ArrayList<User> pendingList = event.getPendingList();
                        pendingList.removeIf(user -> user.getDeviceId().equals(entrant.getDeviceId()));
                        event.setWaitingList(pendingList);
                        ArrayList<User> cancelledList = event.getCancelledList();
                        cancelledList.add(entrant);
                        event.setCancelledList(cancelledList);
                        break;
                    }
                    case "enrolled": {
                        ArrayList<User> enrolledList = event.getEnrolledList();
                        enrolledList.removeIf(user -> user.getDeviceId().equals(entrant.getDeviceId()));
                        event.setWaitingList(enrolledList);
                        ArrayList<User> cancelledList = event.getCancelledList();
                        cancelledList.add(entrant);
                        event.setCancelledList(cancelledList);
                        break;
                    }
                }
            }
        }
    }

    // ViewHolder pattern for performance optimization
    static class ViewHolder {
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView deviceIdTextView;
    }
}