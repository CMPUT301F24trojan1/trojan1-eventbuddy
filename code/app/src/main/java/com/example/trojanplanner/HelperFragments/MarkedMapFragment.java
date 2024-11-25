package com.example.trojanplanner.HelperFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapCapabilities;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PinConfig;

public class MarkedMapFragment extends Fragment implements OnMapReadyCallback {

    private static final LatLng SINGAPORE = new LatLng(1.3521, 103.8198);
    private static final LatLng KUALA_LUMPUR = new LatLng(3.1390, 101.6869);
    private static final LatLng JAKARTA = new LatLng(-6.2088, 106.8456);
    private static final LatLng BANGKOK = new LatLng(13.7563, 100.5018);
    private static final LatLng MANILA = new LatLng(14.5995, 120.9842);
    private static final LatLng HO_CHI_MINH_CITY = new LatLng(10.7769, 106.7009);

    private static final float ZOOM_LEVEL = 3.5f;

    private static final String TAG = MarkedMapFragment.class.getName();

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SINGAPORE, ZOOM_LEVEL));

        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Are advanced markers enabled? " + capabilities.isAdvancedMarkersAvailable());

        // Add markers to the map
        addAdvancedMarker(map, SINGAPORE, "Hello!", Color.MAGENTA);
        addAdvancedMarker(map, KUALA_LUMPUR, "KL Marker", Color.BLUE);
        addAdvancedMarker(map, JAKARTA, "Jakarta", Color.RED);
        addAdvancedMarker(map, BANGKOK, "Bangkok", Color.YELLOW);
        addAdvancedMarker(map, MANILA, "Manila", Color.GREEN);
        addAdvancedMarker(map, HO_CHI_MINH_CITY, "HCMC", Color.CYAN);
    }

    private void addAdvancedMarker(GoogleMap map, LatLng position, String text, int color) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(color);

        Marker marker = map.addMarker(new AdvancedMarkerOptions()
                .position(position)
                .iconView(textView)
                .zIndex(1f));

        if (marker != null) {
            Log.d(TAG, "Added marker at position: " + position);
        }
    }
}