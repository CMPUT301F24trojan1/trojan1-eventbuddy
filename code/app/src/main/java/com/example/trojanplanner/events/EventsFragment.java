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
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;
import com.example.trojanplanner.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private User currentUser; // Assuming you have a user object (Entrant or Organizer)

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

        // Fetch the current user
        currentUser = App.currentUser; // This method needs to return the logged-in user


        // Load events from the database
        loadEventsFromDatabase();

        return root;
    }

    private void loadEventsFromDatabase() {
        eventList.clear();

        // Get current user ID (assuming App.currentUser holds this information)
        String userId = App.currentUser.getDeviceId();

        if (userId == null || userId.isEmpty()) {
            System.out.println("No user is currently logged in.");
            return;
        }

        // Reference to the current user document in the "users" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Fetch the "createdEvents" field from the user document
        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> createdEvents =
                                (List<DocumentReference>) documentSnapshot.get("createdEvents");

                        if (createdEvents != null && !createdEvents.isEmpty()) {
                            // Fetch each event from the "createdEvents" references
                            for (DocumentReference eventRef : createdEvents) {
                                eventRef.get()
                                        .addOnSuccessListener(eventSnapshot -> {
                                            if (eventSnapshot.exists()) {
                                                Event event = eventSnapshot.toObject(Event.class);
                                                if (event != null) {
                                                    eventList.add(event);
                                                    System.out.println("Loaded event: " + event.getName());
                                                }
                                            } else {
                                                System.out.println("No event found for reference: " + eventRef.getId());
                                            }
                                            eventsAdapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Failed to fetch event: " + e.getMessage());
                                        });
                            }
                        } else {
                            System.out.println("No created events found for this user.");
                            eventsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        System.out.println("No user document found for ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Failed to load user document: " + e.getMessage());
                });
    }

    private void addDummyEvent() {
        Event dummyEvent = new Event(
                "fakeId",
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
        navController.navigate(R.id.action_eventsListFragment_to_eventDetailsFragment, bundle);
    }

}
