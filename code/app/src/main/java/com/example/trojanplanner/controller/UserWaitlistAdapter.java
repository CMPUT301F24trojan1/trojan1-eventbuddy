package com.example.trojanplanner.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.model.User;

import java.util.List;


//// JUST AN EXAMPLE CUZ I DONT THINK YALL IMPLEMENTED IT IN FRONT END jsut yet? ion know  SO IM NOT GONNA WORRY ABT THIS
public class UserWaitlistAdapter extends RecyclerView.Adapter<UserWaitlistAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;

    public UserWaitlistAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // // FILL IN THE LAYOUT HERE
        View view = LayoutInflater.from(context).inflate(context.getResources().getIdentifier("user_item_layout", "layout", context.getPackageName()), parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        holder.emailTextView.setText(user.getEmail());

        // Optional set profile pic can ddelete
        if (user.getPfpBitmap() != null) {
            holder.profileImageView.setImageBitmap(user.getPfpBitmap());
        } else {
            // default image
            int defaultImageId = context.getResources().getIdentifier("default_profile_picture", "drawable", context.getPackageName());
            holder.profileImageView.setImageResource(defaultImageId);
        }

        holder.roleTextView.setText(user.getRole());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        ImageView profileImageView;
        TextView roleTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(itemView.getResources().getIdentifier("user_name", "id", itemView.getContext().getPackageName()));
            emailTextView = itemView.findViewById(itemView.getResources().getIdentifier("user_email", "id", itemView.getContext().getPackageName()));
            profileImageView = itemView.findViewById(itemView.getResources().getIdentifier("user_profile_image", "id", itemView.getContext().getPackageName()));
            roleTextView = itemView.findViewById(itemView.getResources().getIdentifier("user_role", "id", itemView.getContext().getPackageName()));
        }
    }
}
