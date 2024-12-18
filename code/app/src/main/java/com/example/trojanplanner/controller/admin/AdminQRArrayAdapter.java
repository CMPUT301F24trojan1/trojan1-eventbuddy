package com.example.trojanplanner.controller.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trojanplanner.R;
import com.example.trojanplanner.view.admin.AdminQRActivity;

import java.util.Collections;
import java.util.List;

public class AdminQRArrayAdapter extends RecyclerView.Adapter<AdminQRArrayAdapter.QRViewHolder> {
    private final Context context;
    private static List<AdminQRActivity.QRModel> qrList = Collections.emptyList();
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AdminQRActivity.QRModel qrModel);
    }

    public AdminQRArrayAdapter(Context context, List<AdminQRActivity.QRModel> qrList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.qrList = qrList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public QRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qr_card, parent, false);
        return new QRViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QRViewHolder holder, int position) {
        AdminQRActivity.QRModel qrModel = qrList.get(position);
        holder.qrTextView.setText("QR Code: " + qrModel.getQrHash());
        holder.eventTextView.setText("Event Name: " + qrModel.getEventName());
    }

    @Override
    public int getItemCount() {
        return qrList.size();
    }

    public static class QRViewHolder extends RecyclerView.ViewHolder {
        TextView qrTextView, eventTextView;
        ImageView qrImageView;

        public QRViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            qrTextView = itemView.findViewById(R.id.qrTextView);
            eventTextView = itemView.findViewById(R.id.eventTextView);
            qrImageView = itemView.findViewById(R.id.qrImageview);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(qrList.get(getAdapterPosition()));
                }
            });
        }
    }
}
