package com.example.trojanplanner.events.organizer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trojanplanner.App;
import com.example.trojanplanner.ProfileUtils.PfpClickPopupFragment;
import com.example.trojanplanner.R;
import com.example.trojanplanner.controller.PhotoPicker;
import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Event;
import com.example.trojanplanner.model.Facility;
import com.example.trojanplanner.model.Organizer;
import com.example.trojanplanner.view.MainActivity;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

/**
 * A fragment for creating a new event. It collects event information
 * from the user through input fields and creates an event in the database.
 */
public class CreateEventFragment extends Fragment {
    private ImageView eventImageView;
    private Bitmap eventImageBitmap;
    private boolean changedPfp = false;
    private PhotoPicker photoPicker;
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText eventDateEditText;
    private EditText eventSpotsAvailableEditText;
    private EditText waitlistCapacityEditText;
    private EditText signupOpenDateEditText, signupCloseDateEditText, eventPriceEditText, eventendDateEditText;
    private Button createEventButton;
    private Button cancelEventButton;
    private Database database;
    private Switch eventGeolocationSwitch;

    //for recurrence
    private Switch recurrenceSwitch;
    private LinearLayout recurrenceOptionsLayout;
    private EditText recurrenceEndDateEditText;
    private CheckBox mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox;
    private Date recurrenceEndDate;
    private ArrayList<String> recurrenceDays = new ArrayList<>();




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Inflates the layout for this fragment and returns the root view.
     *
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The container view to attach the fragment to.
     * @param savedInstanceState The saved instance state for the fragment, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_events, container, false);
    }

    /**
     * Initializes the view components and sets up event listeners for user interactions.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved instance state for the fragment, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recurrenceSwitch = view.findViewById(R.id.recurrenceSwitch);
        recurrenceOptionsLayout = view.findViewById(R.id.recurrenceOptionsLayout);
        recurrenceEndDateEditText = view.findViewById(R.id.recurrenceEndDateEditText);
        mondayCheckbox = view.findViewById(R.id.mondayCheckbox);
        tuesdayCheckbox = view.findViewById(R.id.tuesdayCheckbox);
        wednesdayCheckbox = view.findViewById(R.id.wednesdayCheckbox);
        thursdayCheckbox = view.findViewById(R.id.thursdayCheckbox);
        fridayCheckbox = view.findViewById(R.id.fridayCheckbox);
        saturdayCheckbox = view.findViewById(R.id.saturdayCheckbox);
        sundayCheckbox = view.findViewById(R.id.sundayCheckbox);

        LinearLayout recurrenceOptionsLayout = view.findViewById(R.id.recurrenceOptionsLayout);
        EditText recurrenceEndDateEditText = view.findViewById(R.id.recurrenceEndDateEditText);

        // Handle recurrence visibility
        recurrenceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                recurrenceOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                recurrenceOptionsLayout.setVisibility(View.GONE);
                recurrenceEndDate = null;
            }
        });

        // handle recurrence with the open calendar (hope this works)
        recurrenceEndDateEditText.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                recurrenceEndDateEditText.setText(sdf.format(selectedDate));
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });


        // Create and register a callback for the photoPicker
        photoPicker = ((MainActivity) App.activity).mainActivityPhotoPicker;
        PhotoPicker.PhotoPickerCallback photoPickerCallback = new PhotoPicker.PhotoPickerCallback() {
            @Override
            public void OnPhotoPickerFinish(Bitmap bitmap) {
                onSelectedPhoto(bitmap);
            }
        };
        photoPicker.setCallback(photoPickerCallback);

        if (requireActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back button
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);  // Ensure home button is shown
        }

        // Initialize database
        database = Database.getDB();

        // Get views on the screen
        eventImageView = view.findViewById(R.id.eventImageView);
        eventImageView.setImageBitmap(Event.getDefaultPicture());

        eventGeolocationSwitch = view.findViewById(R.id.eventGeolocationSwitch);
        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = view.findViewById(R.id.eventDescriptionEditText);

        eventDateEditText = view.findViewById(R.id.eventDateEditText);
        eventendDateEditText = view.findViewById(R.id.eventendDateEditText);

        eventSpotsAvailableEditText = view.findViewById(R.id.eventSpotsAvailableEditText);
        waitlistCapacityEditText = view.findViewById(R.id.waitlistCapacityEditText);
        signupOpenDateEditText = view.findViewById(R.id.signupOpenDateEditText);
        signupCloseDateEditText = view.findViewById(R.id.signupCloseDateEditText);
        eventPriceEditText = view.findViewById(R.id.eventPriceEditText);

        createEventButton = view.findViewById(R.id.createEventButton);
        cancelEventButton = view.findViewById(R.id.cancelEventButton);

        eventDateEditText.setOnClickListener(v -> openDateTimePicker(eventDateEditText));
        eventendDateEditText.setOnClickListener(v -> openDateTimePicker(eventendDateEditText));
        signupOpenDateEditText.setOnClickListener(v -> openDateTimePicker(signupOpenDateEditText));
        signupCloseDateEditText.setOnClickListener(v -> openDateTimePicker(signupCloseDateEditText));

        eventImageView.setOnClickListener(v -> {
            createPfpPopup();
        });
        createEventButton.setOnClickListener(v -> {
            boolean ret = createEvent(view);
            if (ret) {
                navigateToEventsListFragment();
            }
        });// Handle "Create Event" button click
        cancelEventButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Event Creation")
                    .setMessage("Are you sure you want to discard this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        NavController navController = NavHostFragment.findNavController(this);
                        navController.navigateUp();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });// Handle "Cancel" button click
    }

    /**
     * Creates a new event by collecting data from the user inputs and uploading it to the database.
     * It ensures that all required fields are filled out and that the current user is an organizer.
     * Attempts to create the event and returns true if creation succeeds.
     *
     * @return True if the event was successfully created and uploaded, false otherwise.
     */
    private boolean createEvent(View view) {
        String spotsAvailableStr = eventSpotsAvailableEditText.getText().toString().trim();
        String waitlistCapacityStr = waitlistCapacityEditText.getText().toString().trim();
        String priceStr = eventPriceEditText.getText().toString().trim();

        String name = eventNameEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        String eventDateStr = eventDateEditText.getText().toString().trim();
        String eventendDateStr = eventendDateEditText.getText().toString().trim();
        String waitlistOpenDateStr = signupOpenDateEditText.getText().toString().trim();
        String waitlistCloseDateStr = signupCloseDateEditText.getText().toString().trim();
        long eventCapacity = spotsAvailableStr.isEmpty() ? 0L : Long.parseLong(spotsAvailableStr);
        long waitlistCapacity = waitlistCapacityStr.isEmpty() ? 100000L : Long.parseLong(waitlistCapacityStr);
        float price = priceStr.isEmpty() ? 0f : Float.parseFloat(priceStr);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date eventDate, eventendDate, waitlistOpenDate, waitlistCloseDate;

        // Initialize views for recurrence functionality
        recurrenceSwitch = view.findViewById(R.id.recurrenceSwitch);
        recurrenceOptionsLayout = view.findViewById(R.id.recurrenceOptionsLayout);
        recurrenceEndDateEditText = view.findViewById(R.id.recurrenceEndDateEditText);
        mondayCheckbox = view.findViewById(R.id.mondayCheckbox);
        tuesdayCheckbox = view.findViewById(R.id.tuesdayCheckbox);
        wednesdayCheckbox = view.findViewById(R.id.wednesdayCheckbox);
        thursdayCheckbox = view.findViewById(R.id.thursdayCheckbox);
        fridayCheckbox = view.findViewById(R.id.fridayCheckbox);
        saturdayCheckbox = view.findViewById(R.id.saturdayCheckbox);
        sundayCheckbox = view.findViewById(R.id.sundayCheckbox);

        // Handle recurrence details
        boolean isRecurring = recurrenceSwitch.isChecked();
        recurrenceEndDate = null;
        recurrenceDays = new ArrayList<>();

        if (isRecurring) {
            String recurrenceEndDateStr = recurrenceEndDateEditText.getText().toString().trim();
            if (!recurrenceEndDateStr.isEmpty()) {
                try {
                    recurrenceEndDate = dateFormat.parse(recurrenceEndDateStr);
                } catch (ParseException e) {
                    Toast.makeText(getContext(), "Invalid recurrence end date", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (mondayCheckbox.isChecked()) recurrenceDays.add("Monday");
            if (tuesdayCheckbox.isChecked()) recurrenceDays.add("Tuesday");
            if (wednesdayCheckbox.isChecked()) recurrenceDays.add("Wednesday");
            if (thursdayCheckbox.isChecked()) recurrenceDays.add("Thursday");
            if (fridayCheckbox.isChecked()) recurrenceDays.add("Friday");
            if (saturdayCheckbox.isChecked()) recurrenceDays.add("Saturday");
            if (sundayCheckbox.isChecked()) recurrenceDays.add("Sunday");

            if (recurrenceDays.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one day for recurrence", Toast.LENGTH_SHORT).show();
                return false;
            }
        }



        try {
            eventDate = dateFormat.parse(eventDateStr);
            eventendDate = dateFormat.parse(eventendDateStr);
            waitlistOpenDate = dateFormat.parse(waitlistOpenDateStr);
            waitlistCloseDate = dateFormat.parse(waitlistCloseDateStr);
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Dates cannot be left blank", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check for missing fields
        if (TextUtils.isEmpty(spotsAvailableStr) || TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        database.getOrganizer(object -> {
                Organizer currentOrganizer = (Organizer) object;
                String newEventId = currentOrganizer.getDeviceId() + "-" + System.currentTimeMillis();
                Facility facility = currentOrganizer.getFacility();
                Date currentDate = new Date();
                int daysLeftToRegister = (int) ((eventDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));

                Event newEvent = new Event(newEventId, name, description, price, facility, eventDate, eventendDate,
                        daysLeftToRegister, eventCapacity, eventCapacity);
                newEvent.setWaitlistCapacity(waitlistCapacity);
                newEvent.setDaysLeftToRegister(daysLeftToRegister);
                newEvent.setAvailableSpots(eventCapacity);
                newEvent.setWaitlistOpen(waitlistOpenDate);
                newEvent.setWaitlistClose(waitlistCloseDate);
                newEvent.setRequiresGeolocation(eventGeolocationSwitch.isChecked()); // Enable geolocation

                //added for recurrence
                // Add recurrence data
//                newEvent.setRecurring(true);
            // this by default is never ^ so we don't want to display that probably
            // we also aren't using the enum types anymore
                newEvent.setRecurrenceEndDate(recurrenceEndDate);
                newEvent.setRecurrenceDays(recurrenceDays); // this needs to be an arraylist of string

                if (changedPfp) {
                    if (eventImageBitmap != null) {
                        String newPfpFilepath = currentOrganizer.getDeviceId() + "/" + System.currentTimeMillis() + ".png";
                        newEvent.setPictureFilePath(newPfpFilepath);
                        newEvent.setPicture(eventImageBitmap);
                        database.uploadImage(eventImageBitmap, currentOrganizer, newPfpFilepath);
                    }
                    else {
                        newEvent.setPictureFilePath(null);
                        newEvent.setPicture(null);
                    }
                }

                database.insertEvent(newEvent);
                currentOrganizer.addEvent(newEvent);
                database.createEmptyMap(newEventId);
                database.insertUserDocument(currentOrganizer);
//                App.currentUser = currentOrganizer; // I have a bad feeling about this

                App.sendAnnouncement(App.currentUser.getDeviceId(), "TrojanPlanner", "New Event Created!");
                Toast.makeText(App.activity, "Event created successfully!", Toast.LENGTH_SHORT).show();
            }, ()-> {Log.e("CreateEvent", "Something went wrong, please try again"); Toast.makeText(App.activity, "Failed to fetch organizer. Please try again.", Toast.LENGTH_SHORT).show();
        }, App.currentUser.getDeviceId());
        return true;
    }

    private void openDateTimePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();

        // Open DatePickerDialog
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            // Set the date in the calendar
            calendar.set(year, month, dayOfMonth);

            // Format and set the selected date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(calendar.getTime());
            targetEditText.setText(formattedDate);

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void navigateToEventsListFragment() {
        // Create an Intent to navigate to MainActivity
        Intent intent = new Intent(getContext(), MainActivity.class);

        // Optionally, you can use this to clear the current activity stack and start fresh
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Start the MainActivity
        startActivity(intent);

        // Optional: You can add a transition animation if needed
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Show the same confirmation dialog as the Cancel button
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Event Creation")
                    .setMessage("Are you sure you want to discard this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        NavController navController = NavHostFragment.findNavController(CreateEventFragment.this);
                        navController.navigateUp();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Action to be taken when the user successfully selects a photo for the event banner.
     * The PhotoPicker is set up to call this function through a callback on selection.
     *
     * @param bitmap
     */
    public void onSelectedPhoto(Bitmap bitmap) {
        System.out.println("CreateEventFragment photopickercallback triggered!");
        changedPfp = true;
        eventImageBitmap = bitmap;
        eventImageView.setImageBitmap(bitmap);
    }

    public void resetEventPhoto() {
        changedPfp = false;
        eventImageBitmap = null;
        eventImageView.setImageBitmap(Event.getDefaultPicture());
    }

    private void createPfpPopup() {
        PfpClickPopupFragment.PfpPopupFunctions popupFunctions = new PfpClickPopupFragment.PfpPopupFunctions() {
            @Override
            public void changePFP() {
                photoPicker.openPhotoPicker();
            }
            @Override
            public void removePFP() {
                resetEventPhoto();
            }
        };

        new PfpClickPopupFragment(popupFunctions).show(((AppCompatActivity) App.activity).getSupportFragmentManager(), "Change Event Picture");
    }

    private Date parseRecurrenceEndDate(String recurrenceEndDateStr) {
        if (recurrenceEndDateStr == null || recurrenceEndDateStr.trim().isEmpty()) {
            return null; // No recurrence end date provided
        }
        try {
            return new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(recurrenceEndDateStr);
        } catch (ParseException e) {
            Log.e("CreateEventFragment", "Invalid recurrence end date format", e);
            Toast.makeText(getContext(), "Invalid recurrence end date format. Please use MM/DD/YYYY.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}