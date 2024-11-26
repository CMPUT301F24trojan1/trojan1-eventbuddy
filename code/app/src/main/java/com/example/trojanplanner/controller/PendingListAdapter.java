package com.example.trojanplanner.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.User;

import java.util.ArrayList;

public class PendingListAdapter extends ArrayAdapter<User> {

    private final Context context;
    private final ArrayList<User> pendingUsers;

    public PendingListAdapter(Context context, ArrayList<User> pendingUsers) {
        super(context, R.layout.item_pending_user, pendingUsers);
        this.context = context;
        this.pendingUsers = pendingUsers != null ? pendingUsers : new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_pending_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.pending_user_name);
            viewHolder.detailsTextView = convertView.findViewById(R.id.pending_user_details);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set user data to the views
        User user = pendingUsers.get(position);
        viewHolder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        viewHolder.detailsTextView.setText("Details: " + user.getEmail()); // Replace with relevant user details

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView detailsTextView;
    }
}
