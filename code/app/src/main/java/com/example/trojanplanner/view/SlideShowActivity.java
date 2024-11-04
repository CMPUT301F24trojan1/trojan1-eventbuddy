package com.example.trojanplanner.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.trojanplanner.HelperFragments.InstructionSlideFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.SlidePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SlideShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show); // Use the correct layout

        ViewPager viewPager = findViewById(R.id.view_pager);
        Button closeButton = findViewById(R.id.close_button);

        // Prepare the list of fragments (slides)
        List<Fragment> slides = new ArrayList<>();
        slides.add(InstructionSlideFragment.newInstance("1. Point your camera at the QR code."));
        slides.add(InstructionSlideFragment.newInstance("2. Ensure the QR code is well-lit and within the frame."));
        slides.add(InstructionSlideFragment.newInstance("3. Wait for the scanner to automatically recognize the code."));
        slides.add(InstructionSlideFragment.newInstance("4. The scanned information will be displayed on the screen."));
        slides.add(InstructionSlideFragment.newInstance("If you encounter any issues, please try again."));

        // Set the adapter to the ViewPager
        SlidePagerAdapter adapter = new SlidePagerAdapter(getSupportFragmentManager(), slides);
        viewPager.setAdapter(adapter);

        closeButton.setOnClickListener(v -> finish());
    }
}
