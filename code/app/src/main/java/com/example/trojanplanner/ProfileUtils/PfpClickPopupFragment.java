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

    private ProfileActivity profileActivity;

    public PfpClickPopupFragment() {
        super();
    }

    public PfpClickPopupFragment(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_pfp_click_popup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view)
                .setTitle("Change Profile Picture")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Remove PFP", (dialogInterface, i) -> {removePFP(); })
                .setPositiveButton("Change PFP", (dialogInterface, i) -> {changePFP(); });

        return builder.create();
    }


    private void removePFP() {
        profileActivity.profileFragment.resetPFP(null); // pass null to reset
    }

    private void changePFP() {
        profileActivity.photoPicker.openPhotoPicker();
    }


}
