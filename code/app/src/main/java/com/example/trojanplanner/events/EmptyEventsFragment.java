package com.example.trojanplanner.events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.R;

public class EmptyEventsFragment extends Fragment {
    private TextView messageTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_myevents, container, false);
        messageTextView = view.findViewById(R.id.messageTextView);

        // Display a message for the user
        showNoEventsMessage();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the NavController
        NavController navController = NavHostFragment.findNavController(this);

        view.findViewById(R.id.becomeOrganizerButton).setOnClickListener(v -> {
            Log.d("EmptyEventsFragment", "Become Organizer Button Clicked");
            navController.navigate(R.id.action_emptyEventsFragment_to_facilitySetupFragment);
        });

        Button createEventButton = view.findViewById(R.id.createEventButton);
        createEventButton.setOnClickListener(v -> {
            Log.d("EmptyEventsFragment", "Create Event Button Clicked");
            navController.navigate(R.id.action_emptyEventsFragment_to_createEventFragment);
        });
    }

    private void showNoEventsMessage() {
        messageTextView.setText("No events found. Come back here to see events you've created or joined!");
    }
}
