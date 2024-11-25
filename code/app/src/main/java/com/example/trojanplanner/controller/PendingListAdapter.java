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
        super(context, R.layout.item_pending_list, pendingUsers);
        this.context = context;
        this.pendingUsers = pendingUsers != null ? pendingUsers : new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_pending_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.nameTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User user = pendingUsers.get(position);
        viewHolder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
    }
}
