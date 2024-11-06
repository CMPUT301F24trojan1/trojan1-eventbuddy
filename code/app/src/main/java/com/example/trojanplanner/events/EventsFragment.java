package com.example.trojanplanner.events;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.EventArrayAdapter;
import com.example.trojanplanner.databinding.FragmentEventsListBinding;
import com.example.trojanplanner.model.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment implements EventArrayAdapter.OnItemClickListener {

    private FragmentEventsListBinding binding;
    private EventArrayAdapter eventsAdapter;
    private List<Event> eventList; // Assuming Event is your model class

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the RecyclerView
        RecyclerView recyclerView = binding.eventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Set LayoutManager

        // Initialize the event list and adapter
        eventList = new ArrayList<>(); // Populate this list with your data
        eventsAdapter = new EventArrayAdapter(eventList, event -> showEventDetailsFragment(event)); // Set the adapter with click listener
        recyclerView.setAdapter(eventsAdapter); // Set the adapter to RecyclerView

        // Load events
        loadEvents();

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadEvents() {
        // Add dummy events for testing
        for (int i = 0; i < 20; i++) { // Add 20 items to ensure scrolling
            eventList.add(new Event("Event " + (i + 1), "Description for Event " + (i + 1)));
        }

        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Event event) {
        // Show event details in a fragment
        showEventDetailsFragment(event);
    }

    private void showEventDetailsFragment(Event event) {
        // Create a Bundle to pass the event data
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", (Serializable) event); // Make sure the event class implements Serializable

        // Navigate to EventDetailsFragment using NavController
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.eventDetailsFragment, bundle); // Navigate to the EventDetailsFragment and pass data
    }
}
