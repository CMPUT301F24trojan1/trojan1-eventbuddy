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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventsFragment extends Fragment {

    private FragmentEventsListBinding binding;
    private EventArrayAdapter eventsAdapter;
    private List<Event> eventList; // Assuming Event is your model class

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the RecyclerView
        RecyclerView recyclerView = binding.eventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Set LayoutManager

        // Initialize the event list and adapter
        eventList = new ArrayList<>(); // Populate this list with your data
        eventsAdapter = new EventArrayAdapter(App.activityManager.getActivity(), eventList); // Create the adapter
        recyclerView.setAdapter(eventsAdapter); // Set the adapter to RecyclerView

        // Example of adding data to the list
        loadEvents(); // A method to populate the eventList

        return root;
    }

    private void loadEvents() {
        // Initialize a date for demonstration (today's date and time)
        Calendar calendar = Calendar.getInstance();
        Date startDateTime = calendar.getTime();
        calendar.add(Calendar.HOUR, 1); // Set end time to one hour later
        Date endDateTime = calendar.getTime();

        // Add dummy events for testing
//        for (int i = 0; i < 20; i++) {
//            eventList.add(new ConcreteEvent(
//                    "Event " + (i + 1),
//                    "Description for Event " + (i + 1),
//                    new Facility("name", "12345", "my house", ),
//                    startDateTime,
//                    endDateTime
//            ));
//        }
//
//        // Optionally add additional events individually
//        eventList.add(new ConcreteEvent("Event 1", "Description for Event 1", "Gym", startDateTime, endDateTime));
//        eventList.add(new ConcreteEvent("Event 2", "Description for Event 2", "Library", startDateTime, endDateTime));
//        eventList.add(new ConcreteEvent("Event 3", "Description for Event 3", "Cafeteria", startDateTime, endDateTime));

        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}