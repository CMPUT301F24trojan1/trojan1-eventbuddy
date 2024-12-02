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

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.util.List;

/**
 * EventArrayAdapter is a RecyclerView adapter that binds a list of events to the corresponding views.
 * It is used to display event details such as name, description, and image in a RecyclerView.
 * <p>
 * The adapter also checks if the current user is in the event's pending list and shows a notification icon if true.
 * </p>
 */
public class EventArrayAdapter extends RecyclerView.Adapter<EventArrayAdapter.EventViewHolder> {

    private final EventArrayAdapter.OnEventClickListener OnEventClickListener;
    private List<Event> eventList;
    private Context context;

    /**
     * Constructs an EventArrayAdapter with the specified context, event list, and click listener.
     *
     * @param context The context in which the adapter operates.
     * @param eventList The list of events to be displayed.
     * @param listener The listener for handling event clicks.
     */
    public EventArrayAdapter(Context context, List<Event> eventList, OnEventClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.OnEventClickListener = listener;
    }

    /**
     * Creates a new view holder for each event item in the RecyclerView.
     *
     * @param parent The parent view group in which the item will be inserted.
     * @param viewType The type of view to be created (not used in this implementation).
     * @return A new EventViewHolder instance.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds event data to the specified view holder for a particular event in the list.
     *
     * @param holder The view holder that will display the event data.
     * @param position The position of the event in the event list.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription());
        holder.pendingNotif.setVisibility(View.GONE);

        if (event.getPendingList() != null){
            for (User user : event.getPendingList()) {
                if (user.getDeviceId().equals(App.currentUser.getDeviceId())) {
                    holder.pendingNotif.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }

        // Use context to get the picture, providing default if necessary
        Bitmap picture = event.getPicture();
        holder.eventImage.setImageBitmap(picture);

        holder.itemView.setOnClickListener(v -> {
            // When an event is clicked, call the onEventClick method
            if (OnEventClickListener != null) {
                OnEventClickListener.onEventClick(event); // This will call the onEventClick in the fragment
            }
        });
    }

    // Define the OnEventClickListener interface
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    /**
     * Returns the number of events in the event list.
     *
     * @return The size of the event list.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class that holds references to the views for an event item.
     * Used to improve performance by recycling views in the RecyclerView.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName;
        private final TextView eventDescription;
        private final ImageView eventImage;
        private final ImageView pendingNotif;

        /**
         * Constructs an EventViewHolder and binds the views to their respective UI elements.
         *
         * @param itemView The root view for the event item.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventImage = itemView.findViewById(R.id.event_image);
            pendingNotif = itemView.findViewById(R.id.pending_notif);
        }
    }
}

