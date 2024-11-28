package com.example.trojanplanner.controller.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Event;

import java.util.List;

public class AdminEventArrayAdapter extends RecyclerView.Adapter<AdminEventArrayAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final Context context;
    private final OnEventClickListener onEventClickListener;

    // Constructor
    public AdminEventArrayAdapter(Context context, List<Event> eventList, OnEventClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.onEventClickListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set event name, description, and price with null checks
        holder.eventName.setText(event != null && event.getName() != null ? event.getName() : "No Name");
        holder.eventDescription.setText(event != null && event.getDescription() != null ? event.getDescription() : "No Description");
        holder.eventPrice.setText(event != null ? String.format("$%.2f", event.getPrice()) : "$0.00");

        // Set facility name with null checks
        if (event != null && event.getFacility() != null) {
            holder.eventFacility.setText(event.getFacility().getName() != null ? event.getFacility().getName() : "No Facility");
        } else {
            holder.eventFacility.setText("No Facility");
        }

        // Date and other information with null checks
        holder.eventStartDate.setText(event != null && event.getStartDateTime() != null ? event.getStartDateTime().toString() : "No Start Date");
        holder.eventEndDate.setText(event != null && event.getEndDateTime() != null ? event.getEndDateTime().toString() : "No End Date");
        holder.daysLeft.setText(event != null ? String.format("%d days left", event.getDaysLeftToRegister()) : "0 days left");
        holder.totalSpots.setText(event != null ? String.format("Total Spots: %d", event.getTotalSpots()) : "Total Spots: 0");
        holder.availableSpots.setText(event != null ? String.format("Available: %d", event.getAvailableSpots()) : "Available: 0");

        // Set event image (use a default image if no picture is available)
        Bitmap picture = event != null ? event.getPicture() : null;
        if (picture != null) {
            holder.eventImage.setImageBitmap(picture);
        } else {
            holder.eventImage.setImageResource(R.drawable.default_event_pic);
        }

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (onEventClickListener != null && event != null) {
                onEventClickListener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;  // Protect against null list
    }

    // ViewHolder class
    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName, eventDescription, eventPrice, eventFacility, eventStartDate, eventEndDate, daysLeft, totalSpots, availableSpots;
        private final ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventPrice = itemView.findViewById(R.id.event_price);
            eventFacility = itemView.findViewById(R.id.event_facility);
            eventStartDate = itemView.findViewById(R.id.event_start_date);
            eventEndDate = itemView.findViewById(R.id.event_end_date);
            daysLeft = itemView.findViewById(R.id.days_left);
            totalSpots = itemView.findViewById(R.id.total_spots);
            availableSpots = itemView.findViewById(R.id.available_spots);
            eventImage = itemView.findViewById(R.id.event_image);
        }
    }

    // OnEventClickListener interface
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}
