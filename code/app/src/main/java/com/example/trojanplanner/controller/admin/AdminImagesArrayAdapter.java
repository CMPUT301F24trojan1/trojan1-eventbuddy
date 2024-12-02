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
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public AdminImagesArrayAdapter(Context context, List<Bitmap> imagesList, OnImageClickListener onImageClickListener) {
        this.context = context;
        this.imagesList = imagesList;
        this.onImageClickListener = onImageClickListener;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_card, parent, false);
        return new ImageViewHolder(view, onImageClickListener);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Bitmap image = imagesList.get(position);
        holder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView, OnImageClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onImageClick(getAdapterPosition());
                }
            });
        }
    }
}
