// EventsFragment.java
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
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.EventArrayAdapter;
import com.example.trojanplanner.databinding.FragmentEventsBinding;
import com.example.trojanplanner.model.Event;
import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventArrayAdapter eventsAdapter;
    private List<Event> eventList; // Assuming Event is your model class

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the RecyclerView
        RecyclerView recyclerView = binding.eventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Set LayoutManager

        // Initialize the event list and adapter
        eventList = new ArrayList<>(); // Populate this list with your data
        eventsAdapter = new EventArrayAdapter(eventList); // Create the adapter
        recyclerView.setAdapter(eventsAdapter); // Set the adapter to RecyclerView

        // Example of adding data to the list
        loadEvents(); // A method to populate the eventList

        return root;
    }

    private void loadEvents() {
        // Add dummy events for testing
        for (int i = 0; i < 20; i++) { // Add 20 items to ensure scrolling
            eventList.add(new Event("Event " + (i + 1), "Description for Event " + (i + 1)));
        }

        // Here we will load your events from a database or an API
        eventList.add(new Event("Event 1", "Description for Event 1"));
        eventList.add(new Event("Event 2", "Description for Event 2"));
        eventList.add(new Event("Event 3", "Description for Event 3"));
        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}