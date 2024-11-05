package com.example.trojanplanner.controller;

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

public class EventArrayAdapter extends RecyclerView.Adapter<EventArrayAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventArrayAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDescription.setText(event.getEventDescription());
        // Assume the event has an image resource ID
        holder.eventImage.setImageResource(event.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName;
        private final TextView eventDescription;
        private final ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventImage = itemView.findViewById(R.id.event_image);
        }

        public void bind(Event event) {
            eventName.setText(event.getEventName());
            eventDescription.setText(event.getEventDescription());
            eventImage.setImageResource(event.getImageResourceId());
        }
    }
}
