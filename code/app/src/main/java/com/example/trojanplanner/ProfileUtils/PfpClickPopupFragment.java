package com.example.trojanplanner.ProfileUtils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.User;
import com.example.trojanplanner.view.ProfileActivity;

public class PfpClickPopupFragment extends DialogFragment {


    /**
     * Allows the class that instantiates this popup to determine what actions the buttons should do.
     * This makes the popup generalizable so it can be reused.
     */
    public interface PfpPopupFunctions {
        void changePFP();
        void removePFP();
    }
    private PfpPopupFunctions popupCallback;


    public PfpClickPopupFragment() {
        super();
    }

    public PfpClickPopupFragment(PfpPopupFunctions popupCallback) {
        this.popupCallback = popupCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_pfp_click_popup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view)
                .setTitle("Change Picture")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Remove PFP", (dialogInterface, i) -> {removePFP(); })
                .setPositiveButton("Change PFP", (dialogInterface, i) -> {changePFP(); });

        return builder.create();
    }


    private void removePFP() {
        popupCallback.removePFP();
    }

    private void changePFP() {
        popupCallback.changePFP();
    }


}
