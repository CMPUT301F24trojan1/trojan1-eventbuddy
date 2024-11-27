package com.example.trojanplanner.controller.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.User;

import java.io.File;
import java.util.List;

public class AdminUsersArrayAdapter extends RecyclerView.Adapter<AdminUsersArrayAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;

    public AdminUsersArrayAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.firstNameTextView.setText(user.getFirstName());
        holder.lastNameTextView.setText(user.getLastName());
        holder.emailTextView.setText(user.getEmail());
        holder.phoneTextView.setText(user.getPhoneNumber());

        // Load profile picture if available
        if (user.getPfpFilePath() != null && !user.getPfpFilePath().isEmpty()) {
            // Assuming pfpFilePath is a valid file path or URI
            File imgFile = new  File(user.getPfpFilePath());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.profileImageView.setImageBitmap(bitmap);
            }
        } else {
            holder.profileImageView.setImageResource(R.drawable.placeholder_avatar);  // Use a default image if none is provided
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView, lastNameTextView, emailTextView, phoneTextView;
        ImageView profileImageView;

        public UserViewHolder(View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
