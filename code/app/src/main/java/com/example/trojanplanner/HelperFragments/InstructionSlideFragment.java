package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.trojanplanner.R;

public class InstructionSlideFragment extends Fragment {

    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_IMAGE_RES_ID = "arg_image_res_id";

    public static InstructionSlideFragment newInstance(String text, int imageResId) {
        InstructionSlideFragment fragment = new InstructionSlideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instruction_slide, container, false);
        TextView textView = view.findViewById(R.id.instruction_text);
        ImageView imageView = view.findViewById(R.id.custom_image);

        if (getArguments() != null) {
            textView.setText(getArguments().getString(ARG_TEXT));
            int imageResId = getArguments().getInt(ARG_IMAGE_RES_ID);
            imageView.setImageResource(imageResId);
        }

        return view;
    }
}
