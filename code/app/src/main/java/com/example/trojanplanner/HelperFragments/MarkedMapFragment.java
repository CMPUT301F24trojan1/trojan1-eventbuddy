package com.example.trojanplanner.HelperFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.model.User;
import com.example.trojanplanner.App;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapCapabilities;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PinConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class MarkedMapFragment extends Fragment implements OnMapReadyCallback {
    private static final float ZOOM_LEVEL = 3.5f;
    private static final String TAG = MarkedMapFragment.class.getName();
    private Event event;
    private GoogleMap map;

    public MarkedMapFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable android.view.ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_marked_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the Event object from the arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event"); // Retrieve the Event object passed in the bundle
            if (event != null) {
                Log.d(TAG, "Event received: " + event.getName());
            } else {
                Log.d(TAG, "No event passed in arguments.");
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        // Default zoom level on map
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), ZOOM_LEVEL));

        // Add markers for all users in the waiting list
        if (event != null && event.getWaitingList() != null && !event.getWaitingList().isEmpty()) {
            for (User user : event.getWaitingList()) {
                getUserLocation(user);
            }
        } else {
            Log.d("MapFragment", "No user in waiting list");
        }
    }

    private void getUserLocation(User user) {
        Database.getDB().getLocation(
                event.getEventId(),
                user.getDeviceId(),
                latLng -> {
                    // Success: Handle the returned location
                    Log.d("Location", "Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude);
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(user.getFirstName()));
                },
                e -> {
                    // Failure: Handle the error
                    Log.e("Location", "Failed to fetch location for user: " + user.getFirstName(), e);
                    Toast.makeText(getContext(), "No location found for " + user.getFirstName(), Toast.LENGTH_SHORT).show();
                }
        );

    }
}