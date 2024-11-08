package com.example.trojanplanner.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.EventArrayAdapter;
import com.example.trojanplanner.databinding.FragmentEventsListBinding;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsFragment extends Fragment implements EventArrayAdapter.OnEventClickListener {

    private FragmentEventsListBinding binding;
    private EventArrayAdapter eventsAdapter;
    private List<Event> eventList;
    private Database database;

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
        eventsAdapter = new EventArrayAdapter(App.activityManager.getActivity(), eventList, this);
        recyclerView.setAdapter(eventsAdapter);

        // Initialize database instance
        database = new Database();

        // Load events from the database
        loadEventsFromDatabase();

        return root;
    }

    private void loadEventsFromDatabase() {
        eventList.clear();

        Database.QuerySuccessAction onSuccess = new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object result) {
                if (result instanceof List) {
                    List<?> genericList = (List<?>) result;

                    if (!genericList.isEmpty() && genericList.get(0) instanceof Event) {
                        @SuppressWarnings("unchecked")
                        List<Event> events = (List<Event>) genericList;
                        eventList.addAll(events);

                        // Log the events retrieved
                        for (Event event : events) {
                            System.out.println("Loaded event: " + event.getName());
                        }

                        eventsAdapter.notifyDataSetChanged();
                    } else {
                        System.out.println("No events found in the database. Adding a dummy event for testing.");

                        // Adding a dummy event if no events were found
                        addDummyEvent();
                    }
                } else {
                    System.out.println("Unexpected data format received from database. Adding a dummy event for testing.");
                    addDummyEvent();
                }
            }
        };

        Database.QueryFailureAction onFailure = new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                System.out.println("Failed to load events from database. Adding a dummy event for testing.");
                addDummyEvent();
            }
        };

        String placeholderId = "all_events"; // Adjust as needed
        database.getEvent(onSuccess, onFailure, placeholderId);
    }

    private void addDummyEvent() {
        Event dummyEvent = new Event(
                "Sample Event",
                "This is a description for the sample event.",
                0.0f, // Price
                null, // Facility (can be null or a placeholder)
                new Date(), // Start date
                new Date(), // End date
                10, // Total spots
                10L, // Available spots
                0L  // Registered participants
        );

        dummyEvent.setEventId("dummy_event_id");
        eventList.add(dummyEvent);
        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEventClick(Event event) {
        System.out.println("Event clicked: " + event.getName());

        // Use NavController to navigate to EventEditFragment
        NavController navController = Navigation.findNavController(requireView());

        // Prepare the arguments
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);  // Pass the event data to the fragment

        // Navigate to EventEditFragment
        navController.navigate(R.id.action_eventsFragment_to_eventEditFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
