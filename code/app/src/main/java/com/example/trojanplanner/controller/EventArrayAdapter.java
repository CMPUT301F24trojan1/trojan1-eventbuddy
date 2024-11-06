package com.example.trojanplanner.controller;

import android.content.Context;
import android.text.NoCopySpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trojanplanner.model.ConcreteEvent;

import java.util.ArrayList;


public class EventArrayAdapter extends ArrayAdapter<ConcreteEvent> {

    private ArrayList<ConcreteEvent> events;
    private Context context;

    public EventArrayAdapter(Context context, ArrayList<ConcreteEvent> events){
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }


    // TODO: Implement getView based on what an event needs (see lab 5 example)
    // bro dis so hard
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(layoutResourceId, parent, false);
        }

        // Get the current event
        ConcreteEvent event = events.get(position);

        // Find the views by their IDs
        TextView eventNameTextView = view.findViewById(eventNameId); //fill out according to XML
        TextView eventLocationTextView = view.findViewById(eventLocationId);
        TextView eventDateTextView = view.findViewById(eventDateId);

        // get event data... according to XML
        eventNameTextView.setText(event.getName());           // Assuming `getName()` is in the Event class
        eventLocationTextView.setText(event.getFacility());   // Assuming `getLocation()` is in the Event class


        return view;

    }
}
