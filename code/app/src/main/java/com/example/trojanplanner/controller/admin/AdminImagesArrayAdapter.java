package com.example.trojanplanner.controller.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.R;

import java.util.List;

public class AdminImagesArrayAdapter extends RecyclerView.Adapter<AdminImagesArrayAdapter.ImageViewHolder> {
    private Context context;
    private List<Bitmap> imagesList;

    // Constructor to initialize the context and the list of images
    public AdminImagesArrayAdapter(Context context, List<Bitmap> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each image item (you will need to create an item layout for this)
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_card, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Bitmap image = imagesList.get(position);

        // Set the image to the ImageView
        holder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return imagesList.size(); // Return the number of items (images)
    }

    // ViewHolder class to hold references to the image views
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view); // Reference to ImageView in your item layout
        }
    }
}
