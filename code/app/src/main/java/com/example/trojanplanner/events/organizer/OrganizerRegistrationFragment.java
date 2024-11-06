package com.example.trojanplanner.events.organizer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.trojanplanner.R;

public class OrganizerRegistrationFragment extends Fragment {
    public OrganizerRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_registration, container, false);
    }
}
