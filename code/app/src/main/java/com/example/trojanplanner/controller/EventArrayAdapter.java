package com.example.trojanplanner.controller;

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

public class EventArrayAdapter extends RecyclerView.Adapter<EventArrayAdapter.EventViewHolder> {

public class EventArrayAdapter extends RecyclerView.Adapter<EventArrayAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;

    public EventArrayAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
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
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription());

        // Use context to get the picture, providing default if necessary
        Bitmap picture = event.getPicture(context);
        if (picture != null) {
            holder.eventImage.setImageBitmap(picture);
        } else {
            holder.eventImage.setImageResource(R.drawable.default_event_pic); // Default image
        }
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

//        public void bind(Event event) {
//            eventName.setText(event.getName());
//            eventDescription.setText(event.getDescription());
//            eventImage.setImageBitmap(event.getPicture(context));
//        }
    }
    }
}
