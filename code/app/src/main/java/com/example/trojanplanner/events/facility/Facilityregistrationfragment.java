package com.example.trojanplanner.events.facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.trojanplanner.R;

public class Facilityregistrationfragment extends Fragment {
        public void FacilityRegistrationFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_facility_registration, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Set up the button to navigate to OrganizerRegistrationFragment
            view.findViewById(R.id.openEventDetailsButton).setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.eventsFragment)
            );
        }
    }
