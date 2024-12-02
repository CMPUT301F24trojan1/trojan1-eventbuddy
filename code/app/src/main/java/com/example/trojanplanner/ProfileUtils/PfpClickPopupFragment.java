package com.example.trojanplanner.ProfileUtils;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.trojanplanner.R;

/**
 * Dialog fragment that displays options to change or remove a profile picture (PFP).
 * It allows the parent activity or fragment to define the actions triggered by the button presses.
 *
 * <p>This dialog is reusable and can be used in different parts of the application where the
 * user might want to change or remove their profile picture.</p>
 */
public class PfpClickPopupFragment extends DialogFragment {


    /**
     * Allows the class that instantiates this popup to determine what actions the buttons should do.
     * This makes the popup generalizable so it can be reused.
     */
    public interface PfpPopupFunctions {
        /**
         * Triggered when the user selects the "Change PFP" button.
         */
        void changePFP();

        /**
         * Triggered when the user selects the "Remove PFP" button.
         */
        void removePFP();
    }
    private PfpPopupFunctions popupCallback;

    /**
     * Default constructor.
     */
    public PfpClickPopupFragment() {
        super();
    }


    /**
     * Constructor with a callback for handling button actions.
     *
     * @param popupCallback The callback that defines the actions for the buttons.
     */
    public PfpClickPopupFragment(PfpPopupFunctions popupCallback) {
        this.popupCallback = popupCallback;
    }

    /**
     * Creates the dialog with the options to change or remove the profile picture.
     * The dialog displays three buttons: Cancel, Remove PFP, and Change PFP.
     *
     * @param savedInstanceState A saved instance state if the dialog is being recreated.
     * @return The created dialog.
     */
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

    /**
     * Calls the callback to remove the profile picture.
     */
    private void removePFP() {
        popupCallback.removePFP();
    }

    /**
     * Calls the callback to change the profile picture.
     */
    private void changePFP() {
        popupCallback.changePFP();
    }


}
