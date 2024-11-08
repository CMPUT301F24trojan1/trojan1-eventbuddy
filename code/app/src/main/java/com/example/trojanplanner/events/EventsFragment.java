package com.example.trojanplanner.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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

public class EventsFragment extends Fragment implements EventArrayAdapter.OnEventClickListener  {

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
        eventsAdapter = new EventArrayAdapter(App.activityManager.getActivity(), eventList, this);
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

            // Placeholder values for Organizer fields
            String lastName = "Doe";
            String firstName = "John";
            String email = "organizer@example.com";
            String phoneNumber = "123-456-7890";
            String deviceId = "device123";
            String role = "Organizer";
            boolean isOrganizer = true;
            boolean isAdmin = false;
            ArrayList<Event> createdEvents = new ArrayList<>(); // Empty list for demonstration
            Facility organizerFacility = null; // Assuming no facility is associated with the organizer initially

            // Create the Organizer object
            Organizer placeholderOrganizer = new Organizer(
                    lastName,
                    firstName,
                    email,
                    phoneNumber,
                    deviceId,
                    role,
                    isOrganizer,
                    isAdmin,
                    createdEvents,
                    organizerFacility
            );

            // Populate with dummy events
            for (int i = 0; i < 10; i++) {
                eventList.add(new ConcreteEvent(
                        "Sample Event " + (i + 1),
                        "This is a description for sample event " + (i + 1),
                        new Facility(
                                "Facility " + (i + 1),
                                "facility" + (i + 1) + "_id",
                                "Sample Address " + (i + 1),
                                placeholderOrganizer,   // Owner, no image args used
                                null,
                                null),
                        null,
                        null)
                );
            }

            eventsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEventClick(Event event) {
        System.out.println("Event clicked: " + event.getName());

        // Use NavController to navigate
        NavController navController = Navigation.findNavController(requireView());

        // Prepare the arguments
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);  // Pass the event data to the fragment

        // Navigate to EventDetailsFragment
        navController.navigate(R.id.action_eventsFragment_to_eventDetailsFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
