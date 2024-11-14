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
import com.example.trojanplanner.model.Database;
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
    private Database database;

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
        eventsAdapter = new EventArrayAdapter(App.activity, eventList, this);
        recyclerView.setAdapter(eventsAdapter);

        // Initialize database instance
        database = Database.getDB();

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
        };

        Database.QuerySuccessAction onSuccess = null; // TODO: implement if this function need to call db?

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
