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
import com.example.trojanplanner.view.admin.AdminQRActivity;
import com.example.trojanplanner.view.admin.AdminUsersActivity;

import java.io.File;
import java.util.List;

public class AdminUsersArrayAdapter extends RecyclerView.Adapter<AdminUsersArrayAdapter.UserViewHolder> {
    private final Context context;
    private final List<User> userList;
    private final AdminUsersArrayAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public AdminUsersArrayAdapter(Context context, List<User> userList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Handle null values for each field
        if (user != null) {
            // Handle first name with null check
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                holder.firstNameTextView.setText(user.getFirstName());
            } else {
                holder.firstNameTextView.setText("Unknown First Name"); // Or a default placeholder
            }

            // Handle last name with null check
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                holder.lastNameTextView.setText(user.getLastName());
            } else {
                holder.lastNameTextView.setText("Unknown Last Name"); // Or a default placeholder
            }

            // Handle email with null check
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                holder.emailTextView.setText(user.getEmail());
            } else {
                holder.emailTextView.setText("No email provided"); // Or a default placeholder
            }

            // Handle phone number with null check
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                holder.phoneTextView.setText(user.getPhoneNumber());
            } else {
                holder.phoneTextView.setText("No phone number"); // Or a default placeholder
            }

            // Handle profile picture with null and validity checks
            if (user.getPfpFilePath() != null && !user.getPfpFilePath().isEmpty()) {
                // Assuming pfpFilePath is a valid file path or URI
                File imgFile = new File(user.getPfpFilePath());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    holder.profileImageView.setImageBitmap(bitmap);
                } else {
                    holder.profileImageView.setImageResource(R.drawable.placeholder_avatar);  // Use default image if file does not exist
                }
            } else {
                holder.profileImageView.setImageResource(R.drawable.placeholder_avatar);  // Use default image if pfpFilePath is null or empty
            }
        } else {
            // Handle the case where the user object is null (though this should be rare if data is consistent)
            holder.firstNameTextView.setText("Unknown First Name");
            holder.lastNameTextView.setText("Unknown Last Name");
            holder.emailTextView.setText("No email provided");
            holder.phoneTextView.setText("No phone number");
            holder.profileImageView.setImageResource(R.drawable.placeholder_avatar);  // Default image if user is null
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
