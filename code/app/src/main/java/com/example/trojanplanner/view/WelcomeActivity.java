package com.example.trojanplanner.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trojanplanner.R;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        View funnyTextView = findViewById(R.id.funnyTextView);
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar during loading

        // Adjust padding of the main view to accommodate system bars (like status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay for 3 seconds before transitioning to the MainActivity
        new Handler().postDelayed(()->{
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent); // Start the MainActivity
            finish();  // Finish the WelcomeActivity to remove it from the back stack
        },3000);

        // Animate the funny text after a short delay
        new Handler().postDelayed(() -> {
            Animation popOutAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_out);
            funnyTextView.startAnimation(popOutAnimation);
            funnyTextView.setVisibility(View.VISIBLE); // Ensure it's visible before animation

        }, 500); // Delay for half a second before animating the text
    }

}
