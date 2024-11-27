package com.example.trojanplanner.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Entrant;

import java.util.ArrayList;

public class WaitlistAdapter extends ArrayAdapter<Entrant> {

    private final Context context;
    private final ArrayList<Entrant> entrants;

    public WaitlistAdapter(Context context, ArrayList<Entrant> entrants) {
        super(context, R.layout.item_waitlist, entrants);  // Add the entrants list here
        this.context = context;
        this.entrants = entrants != null ? entrants : new ArrayList<>();
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

        // Populate popup fields with entrant data
        deviceIdView.setText(entrant.getDeviceId());
        firstNameTextView.setText(entrant.getFirstName());
        lastNameTextView.setText(entrant.getLastName());
        emailTextView.setText(entrant.getEmail());
        phoneNumberView.setText(entrant.getPhoneNumber());
        profilePicture.setImageBitmap(entrant.getPfpBitmap());  // Assuming you have a valid Bitmap for the profile picture

        // Set up the dialog and show it
        builder.setView(popupView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // ViewHolder pattern for performance optimization
    static class ViewHolder {
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView deviceIdTextView;
    }
}