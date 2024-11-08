package com.example.trojanplanner.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.EventArrayAdapter;
import com.example.trojanplanner.databinding.FragmentEventsListBinding;
import com.example.trojanplanner.model.ConcreteEvent;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventsFragment extends Fragment {

    private FragmentEventsListBinding binding;
    private EventArrayAdapter eventsAdapter;
    private List<Event> eventList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the RecyclerView
        RecyclerView recyclerView = binding.eventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the event list and adapter
        eventList = new ArrayList<>();
        eventsAdapter = new EventArrayAdapter(App.activityManager.getActivity(), eventList);
        recyclerView.setAdapter(eventsAdapter);

        // Load events (real or dummy if empty)
        loadEvents();

        return root;
    }

    private void loadEvents() {
        // Check if eventList is empty before populating with dummy events
        if (eventList.isEmpty()) {
            // Example date and time for events
            Calendar calendar = Calendar.getInstance();
            Date startDateTime = calendar.getTime();
            calendar.add(Calendar.HOUR, 1); // End time one hour later
            Date endDateTime = calendar.getTime();

            // Placeholder organizer for demonstration
            Organizer placeholderOrganizer = new Organizer(
                    "Organizer Name", "organizerId", "organizer@example.com"
                    // Add other fields here if needed by Organizer constructor
            );

            // Populate with dummy events
            for (int i = 0; i < 10; i++) {
                eventList.add(new ConcreteEvent(
                        "Sample Event " + (i + 1),
                        "This is a description for sample event " + (i + 1),
                        0.0f,
                        new Facility(
                                "Facility " + (i + 1),
                                "facility" + (i + 1) + "_id",
                                "Sample Address " + (i + 1),
                                placeholderOrganizer,        // Owner
                                null,                        // Profile picture file path
                                null                         // Profile picture bitmap
                        ),
                        startDateTime,
                        endDateTime,
                        50,         // Capacity
                        20L,        // Spots filled
                        30L         // Available spots
                ));
            }

            eventsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
