package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.WaitlistAdapter;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.util.ArrayList;
import java.util.Date;

public class WaitlistFragment extends Fragment {

    private ListView waitlistListView;
    private WaitlistAdapter waitlistAdapter;
    private String listType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable options menu for this fragment
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitlist, container, false);

        waitlistListView = view.findViewById(R.id.waitlistListView);
        Button goBackButton = view.findViewById(R.id.goBackButton);  // Initialize the goBackButton

        // Set up the goBackButton click listener
        goBackButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();  // Handle back press
        });
        
        // Retrieve the event object from the arguments
        Event event = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            event = (Event) arguments.getSerializable("event");
            listType = arguments.getString("listType");

            Log.d("WaitlistFragment", "Received Event object: " + event.getName() + "List Type: " + listType);
        }

        // If the Event object is null, navigate back to the previous fragment
        if (event == null) {
            Log.e("WaitlistFragment", "Event object is null");
            getActivity().onBackPressed();
            return view;
        }

        // Fetch event from the database asynchronously
        fetchEventFromDatabase(event);

        return view;
    }

    private void fetchEventFromDatabase(Event event) {
        // Define success and failure actions
        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction() {
            @Override
            public void OnSuccess(Object object) {
                Event updatedEvent = (Event) object;
                ArrayList<User> users = new ArrayList<>();

                if ("enrolled".equals(listType)) {
                    Button finalizeButton = requireActivity().findViewById(R.id.finalizeButton);
                    finalizeButton.setVisibility(View.VISIBLE);

                    Date currentTime = new Date();
                    if (event.getWaitlistClose() == null || event.getWaitlistClose().getDate() > currentTime.getDate()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Finalize Event");
                        builder.setMessage("You cannot finalize this event until the waitlist is closed.");
                        builder.setPositiveButton("Yes I understand", (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.show();
                    }

                    finalizeButton.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Finalize Event");
                        builder.setMessage("Are you sure you want to finalize this event?");

                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            ArrayList<User> listevent = event.getWaitingList();
                            ArrayList<User> listcancelled = event.getCancelledList();
                            listcancelled.addAll(listevent);
                            event.setCancelledList(listcancelled);
                            event.setWaitingList(new ArrayList<>()); // Clear the waiting List
                            Database.getDB().insertEvent(event);
                            requireActivity().onBackPressed();
                        });
                        builder.setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.show();
                    });
                    users = updatedEvent.getEnrolledList(); // Assuming the event has a method to get enrolled users
                } else if ("cancelled".equals(listType)) {
                    users = updatedEvent.getCancelledList(); // Assuming the event has a method to get cancelled users
                } else if ("waiting".equals(listType)) {
                    users = updatedEvent.getWaitingList(); // Assuming the event has a method to get waitlisted users
                } else if ("pending".equals(listType)) {
                    users = updatedEvent.getPendingList(); // Assuming the event has a method to get waitlisted users
                }
                Log.d("WaitlistFragment", "Fetched waitlisted users: " + users);

                // If no list type matches or the list is empty, return
                if (users.isEmpty()) {
                    Log.d("WaitlistFragment", listType + "is empty.");
                    showEmptyListMessage();
                    return;
                }

                // Convert User objects to Entrant objects
                ArrayList<Entrant> entrants = new ArrayList<>();
                for (User user : users) {
                    Entrant entrant = (Entrant) user;
                    Log.d("WaitlistFragment", "Creating Entrant: " + entrant.getFirstName());
                    entrants.add(entrant);
                }

                // Now set the adapter with the converted entrants
                if (!entrants.isEmpty()) {
                    waitlistAdapter = new WaitlistAdapter(requireContext(), entrants, event, listType);
                    waitlistListView.setAdapter(waitlistAdapter);
                    waitlistAdapter.notifyDataSetChanged();  // Ensure the adapter is notified
                    Log.d("WaitlistFragment", "Adapter has been set with " + entrants.size() + " entrants.");
                } else {
                    Log.d("WaitlistFragment", "Waitlist is empty or null");
                    requireActivity().onBackPressed();
                }
            }
        };

        Database.QueryFailureAction failureAction = new Database.QueryFailureAction() {
            @Override
            public void OnFailure() {
                Log.e("WaitlistFragment", "Failed to fetch event from the database");
            }
        };

        // Fetch event data from the database
        Database.getDB().getEvent(successAction, failureAction, event.getEventId());
    }

    private void showEmptyListMessage() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Notice")
                .setMessage("No Users found in the list.")
                .setPositiveButton("I Understand", (dialog, which) -> {
                    dialog.dismiss(); // Dismiss the dialog when the button is clicked
                })
                .setCancelable(false) // Prevent dialog from being dismissed by tapping outside
                .show();
    }
}