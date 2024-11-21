package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.WaitlistAdapter;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Event;

public class WaitlistFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitlist, container, false);

        // Initialize the ListView (you may populate it elsewhere in your code)
        ListView waitlistListView = view.findViewById(R.id.waitlistListView);

        // Set up the back button
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to EventEditFragment
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_waitlistFragment_to_eventEditFragment);
        });

        //// TESTING
// Retrieve the Event object from the arguments Bundle
//        if (getArguments() != null) {
//            testEvent = (Event) getArguments().getSerializable("event");
//        }
//
//        // Check if the event is null and handle it accordingly
//        if (testEvent == null) {
//            Log.e("WaitlistFragment", "Event is null!");
//// Optionally, display an error message or return early
//            return view;
//        }

        return view;
    }
}

