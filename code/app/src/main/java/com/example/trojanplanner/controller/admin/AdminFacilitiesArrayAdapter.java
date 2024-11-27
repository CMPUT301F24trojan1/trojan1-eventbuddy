package com.example.trojanplanner.controller.admin;

import android.content.Context;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Facility;
import java.util.List;

public class AdminFacilitiesArrayAdapter extends RecyclerView.Adapter<AdminFacilitiesArrayAdapter.FacilityViewHolder> {

    private final List<Facility> facilityList;
    private final Context context;

    // Constructor
    public AdminFacilitiesArrayAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_facility_card, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);

        // Bind the name, location, and owner
        holder.facilityName.setText(facility.getName());
        holder.facilityLocation.setText(facility.getLocation());
        holder.facilityOwner.setText(facility.getOwner().getFirstName()); // Assuming `owner` is an `Organizer` object

        // Load the image using Glide
        if (facility.getPfpFacilityBitmap() != null) {
            // If we have a bitmap, load it into the ImageView
            holder.facilityImage.setImageBitmap(facility.getPfpFacilityBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    // ViewHolder class for facilities
    static class FacilityViewHolder extends RecyclerView.ViewHolder {
        private final ImageView facilityImage;
        private final TextView facilityName, facilityLocation, facilityOwner;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityImage = itemView.findViewById(R.id.facility_image);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityLocation = itemView.findViewById(R.id.facility_location);
            facilityOwner = itemView.findViewById(R.id.facility_owner);
        }
    }
}
