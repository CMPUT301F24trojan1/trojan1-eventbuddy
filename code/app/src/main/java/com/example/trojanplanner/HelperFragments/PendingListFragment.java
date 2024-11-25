package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PendingListAdapter;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.User;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

public class PendingListFragment extends Fragment {

    private Event event;
    private ArrayList<User> pendingList;

    public PendingListFragment() {
        super(R.layout.fragment_pending_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enable back button in the ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Pending List");

        // Retrieve event and pending list from arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            pendingList = event.getPendingList();
        } else {
            pendingList = new ArrayList<>();
        }

        // Set up the ListView with PendingListAdapter
        ListView listView = view.findViewById(R.id.pending_list_view);
        PendingListAdapter adapter = new PendingListAdapter(requireContext(), pendingList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
