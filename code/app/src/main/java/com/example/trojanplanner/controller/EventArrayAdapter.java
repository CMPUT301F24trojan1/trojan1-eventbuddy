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

    private final List<Event> eventList;
    private final OnItemClickListener listener;

    // Constructor takes both the event list and the click listener
    public EventArrayAdapter(List<Event> eventList, OnItemClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
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

        public void bind(Event event, OnItemClickListener listener) {
            eventName.setText(event.getName());
            eventDescription.setText(event.getDescription());
            eventImage.setImageBitmap(event.getPicture());

            // Set an OnClickListener for the entire itemView to handle clicks
            itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }
}
