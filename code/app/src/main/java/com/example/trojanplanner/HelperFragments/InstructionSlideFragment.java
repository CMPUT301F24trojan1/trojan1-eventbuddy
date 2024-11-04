package com.example.trojanplanner.HelperFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;

public class InstructionSlideFragment extends Fragment {

    private static final String ARG_INSTRUCTION = "instruction";

    public static InstructionSlideFragment newInstance(String instruction) {
        InstructionSlideFragment fragment = new InstructionSlideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INSTRUCTION, instruction);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instruction_slide, container, false);
        TextView instructionTextView = view.findViewById(R.id.instruction_text);
        if (getArguments() != null) {
            instructionTextView.setText(getArguments().getString(ARG_INSTRUCTION));
        }
        return view;
    }
}
