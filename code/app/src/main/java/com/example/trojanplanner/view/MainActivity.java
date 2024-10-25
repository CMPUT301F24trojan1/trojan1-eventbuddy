package com.example.trojanplanner.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trojanplanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static Activity activity; // Important to allow non-activity classes to trigger UI components, i.e. PhotoPicker

    private Button tempButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        tempButton = findViewById(R.id.tempButton);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);






        // temp stuff below
        Database database = new Database();
        database.initPhotoPicker();

        tempButton.setOnClickListener(v -> {
            User user = new Entrant("", "", "", "", "1234567890", "", false, false);
            //database.uploadImage(user);
            database.uploadFromPhotoPicker(user);
        });

        System.out.println("onCreate done");
    }


    /**
     * Gets the application context. This is a static method so any other class is able to call this function
     * in order to get the application context itself
     * @return The application context
     */
//    public static Context getAppContext() {
//        return activity.getApplicationContext();
//    }
//
}