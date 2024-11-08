package com.example.trojanplanner.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.User;

import java.util.List;
import android.app.AlertDialog;
import android.widget.ImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.ImageView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Entrant;

import java.util.List;

public class WaitlistAdapter extends ArrayAdapter<Entrant> {

    private final Context context;
    private final List<Entrant> entrants;

    public WaitlistAdapter(Context context, List<Entrant> entrants) {
        super(context, R.layout.item_waitlist, entrants);
        this.context = context;
        this.entrants = entrants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_waitlist, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.firstNameTextView = convertView.findViewById(R.id.firstNameTextView);
            viewHolder.lastNameTextView = convertView.findViewById(R.id.lastNameTextView);
            viewHolder.deviceIdTextView = convertView.findViewById(R.id.DeviceIDView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Entrant entrant = entrants.get(position);

        viewHolder.firstNameTextView.setText(entrant.getFirstName()); // Assuming getFirstName() method exists
        viewHolder.lastNameTextView.setText(entrant.getLastName());   // Assuming getLastName() method exists
        viewHolder.deviceIdTextView.setText(entrant.getDeviceId());   // Assuming getDeviceId() method exists

        // Set an on-click listener to show the full profile in a dialog
        convertView.setOnClickListener(v -> showProfilePopup(entrant));

        return convertView;
    }

    private void showProfilePopup(Entrant entrant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.item_waitlist_popup, null);

        // Initialize views
        TextView deviceIdView = popupView.findViewById(R.id.DeviceIDView);
        ImageView profilePicture = popupView.findViewById(R.id.profilePicture);
        TextView firstNameTextView = popupView.findViewById(R.id.firstNameTextView);
        TextView lastNameTextView = popupView.findViewById(R.id.lastNameTextView);
        TextView emailTextView = popupView.findViewById(R.id.emailTextView);
        TextView phoneNumberView = popupView.findViewById(R.id.phone_number_View);

        // Populate popup fields with entrant data
        deviceIdView.setText(entrant.getDeviceId());               // Assuming getDeviceId() method exists in Entrant
        firstNameTextView.setText(entrant.getFirstName());         // Assuming getFirstName() method exists in Entrant
        lastNameTextView.setText(entrant.getLastName());           // Assuming getLastName() method exists in Entrant
        emailTextView.setText(entrant.getEmail());                 // Assuming getEmail() method exists in Entrant
        phoneNumberView.setText(entrant.getPhoneNumber());         // Assuming getPhoneNumber() method exists in Entrant
        profilePicture.setImageBitmap(entrant.getPfpBitmap()); // Assuming getProfilePicture() returns a Bitmap

        builder.setView(popupView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static class ViewHolder {
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView deviceIdTextView;
    }
}
