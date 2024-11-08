package com.example.trojanplanner.HelperFragments;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.WaitlistAdapter;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Button;

import java.util.List;


import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Event;

public class WaitlistFragment extends Fragment {

    private Event testEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitlist, container, false);

        // Retrieve the Event object from the arguments Bundle
        if (getArguments() != null) {
            testEvent = (Event) getArguments().getSerializable("event");
        }

        // Check if the event is null and handle it accordingly
        if (testEvent == null) {
            Log.e("WaitlistFragment", "Event is null!");
            // Optionally, display an error message or return early
            return view;
        }

        // Now you can safely access testEvent.getWaitingList()
        // Example usage:
        // List<Entrant> waitlist = testEvent.getWaitingList();

        // Your UI setup code for displaying the waitlist, etc.

        return view;
    }
}
