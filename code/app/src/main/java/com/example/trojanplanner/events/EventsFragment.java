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
import com.example.trojanplanner.model.ConcreteEvent;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A Fragment that displays a list of events using a RecyclerView.
 * <p>
 * The fragment loads events, either from a database or dummy data if none are available, and presents
 * them in a scrollable list. When an event is clicked, the user is navigated to an editing screen.
 */
public class EventsFragment extends Fragment implements EventArrayAdapter.OnEventClickListener {

    private FragmentEventsListBinding binding;
    private EventArrayAdapter eventsAdapter;
    private List<Event> eventList;

    /**
     * Called to create the view for this fragment. Initializes the RecyclerView, adapter, and loads the events.
     *
     * @param inflater The LayoutInflater used to inflate the fragment's layout.
     * @param container The parent view that contains the fragment's UI.
     * @param savedInstanceState A bundle containing saved state data (if any).
     * @return The view for the fragment.
     */
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

    /**
     * Loads events into the list. If the list is empty, dummy events are generated and added.
     */
    private void loadEvents() {
        if (eventList.isEmpty()) {
            // Example date and time for events
            Calendar calendar = Calendar.getInstance();
            Date startDateTime = calendar.getTime();
            calendar.add(Calendar.HOUR, 1); // End time one hour later
            Date endDateTime = calendar.getTime();

            // Placeholder values for Organizer fields
            Organizer placeholderOrganizer = new Organizer("Doe", "John", "organizer@example.com",
                    "123-456-7890", "device123", "Organizer", true, false, new ArrayList<>(), null);

            // Populate with dummy events
            for (int i = 0; i < 10; i++) {
                eventList.add(new ConcreteEvent(
                        "Sample Event " + (i + 1),
                        "This is a description for sample event " + (i + 1),
                        new Facility("Facility " + (i + 1), "facility" + (i + 1) + "_id",
                                "Sample Address " + (i + 1), placeholderOrganizer, null, null),
                        null,
                        null)
                );
            }

            eventsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Called when an event in the list is clicked. It navigates the user to the EventEditFragment where the
     * event can be edited.
     *
     * @param event The event that was clicked.
     */
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

    /**
     * Called when the fragment's view is being destroyed. This clears the binding to avoid memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
