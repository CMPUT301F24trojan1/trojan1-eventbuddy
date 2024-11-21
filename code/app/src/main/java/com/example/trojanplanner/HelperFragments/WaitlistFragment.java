package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.WaitlistAdapter;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WaitlistFragment extends Fragment {

    private ListView waitlistListView;
    private WaitlistAdapter waitlistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitlist, container, false);

        waitlistListView = view.findViewById(R.id.waitlistListView);

        // Retrieve the event object from the arguments
        Event event = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            event = (Event) arguments.getSerializable("event"); // Ensure Event implements Serializable or Parcelable
        }

        // If the Event object is null, navigate back to the previous fragment
        if (event == null) {
            Log.e("WaitlistFragment", "Event object is null");
            // Navigate back up the backstack
            getActivity().onBackPressed();
            return view; // Early return to avoid further execution
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
                ArrayList<User> waitlistedUsers = updatedEvent.getWaitingList();

                Log.d("WaitlistFragment", "Fetched waitlisted users: " + waitlistedUsers);

                // Convert User objects to Entrant objects
                ArrayList<Entrant> entrants = new ArrayList<>();
                for (User user : waitlistedUsers) {
                    Entrant entrant = (Entrant) user;
                    Log.d("WaitlistFragment", "Creating Entrant: " + entrant.getFirstName());
                    entrants.add(entrant);
                }

                // Now set the adapter with the converted entrants
                if (!entrants.isEmpty()) {
                    waitlistAdapter = new WaitlistAdapter(requireContext(), entrants);
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
        Database.getDB().getEvent(successAction, failureAction, event.getEventId(), false, null);
    }
}