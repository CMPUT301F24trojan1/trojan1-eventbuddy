package com.example.trojanplanner.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.example.trojanplanner.App;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.events.EventsFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trojanplanner.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private Database database;

    public PhotoPicker eventPhotoPicker; // Initialized in MainActivity, callback set in CreateEventFragment
    public PhotoPicker facilityPhotoPicker; // Have to initialize this here in case we get directed to FacilitySetupFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = Database.getDB();
        eventPhotoPicker = new PhotoPicker();
        eventPhotoPicker.initPhotoPicker();
        facilityPhotoPicker = new PhotoPicker();
        facilityPhotoPicker.initPhotoPicker();


//        // If this device ID doesn't match a user on the db then force them to make a profile (switch to that activity)
//        if (App.currentUser == null) {
//            // Get/check entrant from db based on device ID (note: this is async)
//            getEntrantFromDeviceId(App.deviceId); // Redirects if no entrant exists!
//        }

        setupNavigation();

    }

//    /**
//     *
//     * @param deviceId
//     * @author Jared Gourley
//     */
//    private void getEntrantFromDeviceId(String deviceId) {
//        // On success, set the entrant object and populate the events list
//        // On failure, redirect to the make profile page (for now?)
//        Database.QuerySuccessAction successAction = new Database.QuerySuccessAction(){
//            @Override
//             public void OnSuccess(Object object) {
//                 App.currentUser = (Entrant) object;
//                 Entrant currentEntrant = (Entrant) App.currentUser; // Just to make this function have less typecasting
//                 System.out.println("getEntrantFromDeviceId success! current user: " + currentEntrant.getFirstName() + " " + currentEntrant.getLastName());
//                 Toast myToast = Toast.makeText(App.activity, "Hello " + currentEntrant.getFirstName() + "!", Toast.LENGTH_LONG);
//                 myToast.show();
//                 System.out.println("currentUser pfp file path: " + currentEntrant.getPfpFilePath());
//
//                 // TODO: populate events array
//                 // Check if the user has any events
//                 if ((currentEntrant.getCurrentWaitlistedEvents() == null || currentEntrant.getCurrentWaitlistedEvents().isEmpty()) &&
//                         (currentEntrant.getCurrentPendingEvents() == null || currentEntrant.getCurrentPendingEvents().isEmpty())) {
//                     // Show the EmptyEventsFragment if no events are found
//                     // will show by default
//                 } else {
//                     // Otherwise, show the EventsFragment
////                     getSupportFragmentManager().beginTransaction()
////                             .replace(R.id.nav_host_fragment_activity_main, new EventsFragment())
////                             .commit();
//                     NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
//                     navController.navigate(R.id.eventsListFragment);
//                 }
//             }
//        };
//        Database.QueryFailureAction failureAction = new Database.QueryFailureAction(){
//            @Override
//            public void OnFailure() {
//                System.out.println("getEntrantFromDeviceId failed: new user?");
//                Toast myToast = Toast.makeText(App.activity, "Hello new user! Make a profile to join events!", Toast.LENGTH_LONG);
//                myToast.show();
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                // Bundle attributes to be passed here i.e. intent.putExtra(...)
//                startActivity(intent);
//            }
//        };
//
//        database.getEntrant(successAction, failureAction, deviceId);
//    }

//    public void getUserPfp() {
//        System.out.println("Getting user's PFP bitmap...");
//        OnSuccessListener successListener = new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap decodedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                App.currentUser.setPfpBitmap(decodedImage);
//                System.out.println("success!! User pfp bitmap received!");
//            }
//        };
//        OnFailureListener failureListener = new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println("NOOOOOOOOOOOOOOOOO user pfp bitmap query failed");
//            }
//        };
//
//        database.downloadImage(App.currentUser.getPfpFilePath(), successListener, failureListener);
//    }

    /**
     * Sets up the navigation for the BottomNavigationView and the ActionBar.
     * <p>
     * This method initializes the BottomNavigationView and NavController,
     * configuring them to manage navigation between different destinations in
     * the app. It sets up an AppBarConfiguration that defines the top-level
     * navigation destinations. Additionally, it establishes a listener for
     * item selection events within the BottomNavigationView, allowing the
     * user to navigate to the following screens:
     * <ul>
     * <li>{@link EventsFragment} when the events navigation item is selected.
     * <li>{@link QRActivity} when the QR activity item is selected.
     * <li>{@link ProfileActivity} when the profile navigation item is selected.
     * </ul>
     * This method should be called in the onCreate method of the MainActivity
     * to properly set up the navigation when the activity is created.
     *
     * @author Dricmoy Bhattacharjee
     */
    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.emptyEventsFragment)
                .build();

        // Initialize NavController with the nav host fragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // Set up ActionBar and BottomNavigationView with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Disable the back button for EmptyEventsFragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Enable the up button for other fragments
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(destination.getId() != R.id.emptyEventsFragment); // Disable the up button
        });

        // Set up the listener to handle Bottom Navigation item selections
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.eventsListFragment) {
                navController.navigate(R.id.eventsListFragment);
                return true;
            } else if (item.getItemId() == R.id.qrActivity) {
                Intent intent = new Intent(MainActivity.this, QRActivity.class);
                // Clear any savedInstanceState SO to not cause a crash due to Bundle size being too big
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.profileActivity) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                // Clear any savedInstanceState SO to not cause a crash due to Bundle size being too big
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}