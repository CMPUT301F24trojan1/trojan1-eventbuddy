package com.example.trojanplanner.QRUtils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.trojanplanner.R;

/**
 * A DialogFragment that provides help or instructions related to QR code usage in the app.
 * This fragment displays a simple help screen with a button to close the dialog.
 */
public class QRHelpFragment extends DialogFragment {

    /**
     * Called when the fragment is created. Sets the dialog's style to the default normal style.
     *
     * @param savedInstanceState The saved instance state (if any).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, 0);
    }

    /**
     * Called to create the view hierarchy for this fragment.
     * Inflates the layout for the QRHelpFragment and sets up the close button.
     *
     * @param inflater The LayoutInflater used to inflate the fragment's layout.
     * @param container The container (if any) for the view hierarchy.
     * @param savedInstanceState The saved instance state (if any).
     * @return The view hierarchy for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.activity_slide_show, container, false);

        // Find the close button and set its click listener
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dismiss()); // Close the dialog on click

        return view;
    }
}
