package com.example.trojanplanner.events;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trojanplanner.R;
import com.example.trojanplanner.model.ConcreteEvent;

import com.example.trojanplanner.model.Database;
import com.example.trojanplanner.model.Entrant;
import com.example.trojanplanner.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Set;

//NOTE THIS IS FOR THE ENTRANT NOT ORGANIZER, same with event_details_fragment.xml
public class EventDetailsFragment extends Fragment {
    private Event event;
    private Entrant entrant;
    private Database database;
    private Button buttonEnterNow;
    private Button buttonLeaveWaitlist;
    // Constructor with parameters
    public EventDetailsFragment(ConcreteEvent event, Entrant entrant) {
        this.event = event;
        this.entrant = entrant;
        this.database = new Database(); // Initialize Database instance
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Entrant getEntrant() {
        return entrant;
    }

    public void setEntrant(Entrant entrant) {
        this.entrant = entrant;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public static EventDetailsFragment newInstance(Event event, Entrant entrant) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        args.putSerializable("entrant", entrant);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            entrant = (Entrant) getArguments().getSerializable("entrant");
        }
        database = new Database();
    }

    // Required empty constructor
    public EventDetailsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        // initialize views
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventLocationTextView = view.findViewById(R.id.eventLocationTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView recurringDatesTextView = view.findViewById(R.id.recurringDatesTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);

        buttonEnterNow = view.findViewById(R.id.button_enter_now);
        buttonLeaveWaitlist = view.findViewById(R.id.button_leave_waitlist);

//        // Populate event details
//        populateEventDetails(eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, eventDescriptionTextView);
        // Ensure `event` is initialized
        if (event != null) {
            populateEventDetails(eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, eventDescriptionTextView);
        } else {
            // Log or handle the error
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        }
        // Ensure 'event' is initialized before calling 'checkEntrantStatus'
        if (event != null) {
            checkEntrantStatus();
        } else {
            Log.e("EventDetailsFragment", "Event is null in onCreateView");
        };

        // Set button click listeners
        buttonEnterNow.setOnClickListener(v -> joinWaitlist());
        buttonLeaveWaitlist.setOnClickListener(v -> leaveWaitlist());

        return view;
    }
    private void checkEntrantStatus() {
        if (event != null && event.getWaitingList() != null) {
            if (event.getWaitingList().contains(entrant)) {
                buttonEnterNow.setVisibility(View.GONE);
                buttonLeaveWaitlist.setVisibility(View.VISIBLE);
            } else {
                buttonEnterNow.setVisibility(View.VISIBLE);
                buttonLeaveWaitlist.setVisibility(View.GONE);
            }
        } else {
            Log.e("EventDetailsFragment", "Event or waiting list is null in checkEntrantStatus");
        }
    }

    // Helper method to get the full name for the day of the week based on unique abbreviation
    private String getFullDayName(String abbreviation) {
        switch (abbreviation) {
            case "U": return "Sunday";
            case "M": return "Monday";
            case "T": return "Tuesday";
            case "W": return "Wednesday";
            case "R": return "Thursday";
            case "F": return "Friday";
            case "S": return "Saturday";
            default: return ""; // Handle invalid abbreviations
        }
    }

    public void populateEventDetails(TextView eventNameTextView, TextView eventLocationTextView, TextView eventDateTextView, TextView recurringDatesTextView, TextView eventDescriptionTextView) {
        eventNameTextView.setText(event.getName());
        eventLocationTextView.setText(event.getFacility().getFacilityId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String startDate = dateFormat.format(event.getStartDateTime());
        String endDate = dateFormat.format(event.getEndDateTime());
        eventDateTextView.setText(startDate + " - " + endDate);

        // Convert abbreviations in recurrenceDays to full day names
        Set<String> recurrenceDays = event.getRecurrenceDays();
        String recurrenceDaysText = recurrenceDays.stream()
                .map(this::getFullDayName) // Convert each unique abbreviation to full day name
                .filter(name -> !name.isEmpty()) // Filter out any invalid/missing conversions
                .reduce((a, b) -> a + ", " + b) // Join with commas
                .orElse("");

        recurringDatesTextView.setText(recurrenceDaysText);
        eventDescriptionTextView.setText(event.getDescription());
    }


    public void joinWaitlist() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_registration, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView textEventInfo = dialogView.findViewById(R.id.text_event_info);
        textEventInfo.setText("Are you sure you want to join the waitlist for:\n" + event.getName());

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
        Log.d("EventDetailsFragment", "Attempting to hide buttonEnterNow");
        buttonEnterNow.setVisibility(View.GONE);  // Ensure this line executes as expected
        Log.d("EventDetailsFragment", "buttonEnterNow visibility set to GONE");

        dialogView.findViewById(R.id.button_confirm).setOnClickListener(v -> {

            event.addParticipant(entrant);
            entrant.addWaitlistedEvent(event);

            database.insertEvent(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    database.getEntrant(new Database.QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {

                            database.insertUserDocument(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Added to waitlist", Toast.LENGTH_SHORT).show();
                                    checkEntrantStatus();
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to update entrant in the database", Toast.LENGTH_SHORT).show();
                                }
                            }, entrant);
                        }
                    }, new Database.QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            Toast.makeText(getContext(), "Entrant not found in the database", Toast.LENGTH_SHORT).show();
                        }
                    }, entrant.getDeviceId());
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to add event to the database", Toast.LENGTH_SHORT).show();
                }
            }, event);

            dialog.dismiss();
        });

        dialog.show();
    }

    public void leaveWaitlist() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_registration, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView textEventInfo = dialogView.findViewById(R.id.text_event_info);
        textEventInfo.setText("Are you sure you want to leave the waitlist for:\n" + event.getName());

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(v -> {

            event.removeParticipant(entrant);
            entrant.removeWaitlistedEvent(event);

            database.insertEvent(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    database.getEntrant(new Database.QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {
                            Entrant existingEntrant = (Entrant) object;
                            existingEntrant.removeWaitlistedEvent(event);

                            database.insertUserDocument(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Removed from waitlist", Toast.LENGTH_SHORT).show();
                                    checkEntrantStatus();
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to update entrant in the database", Toast.LENGTH_SHORT).show();
                                }
                            }, existingEntrant);
                        }
                    }, new Database.QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            Toast.makeText(getContext(), "Entrant not found in the database", Toast.LENGTH_SHORT).show();
                        }
                    }, entrant.getDeviceId());
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to update event in the database", Toast.LENGTH_SHORT).show();
                }
            }, event);

            dialog.dismiss();
        });

        dialog.show();
    }

    // no longer used since we have joinWaitlist and leaveWaitlist
    public void showConfirmDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_registration, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView textEventInfo = dialogView.findViewById(R.id.text_event_info);
        textEventInfo.setText(event.getName() + "\n" + event.getFacility().getFacilityId() + "\n" +
                event.getStartDateTime() + " - " + event.getEndDateTime() + "\n" +
                event.getRecurrenceDays() + "\n" + event.getDescription());

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(v -> {

            // IS THIS NEEDED
            event.addParticipant(entrant);
            entrant.addWaitlistedEvent(event);

            database.insertEvent(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Check if the entrant exists in the database
                    database.getEntrant(new Database.QuerySuccessAction() {
                        @Override
                        public void OnSuccess(Object object) {
                            Entrant existingEntrant = (Entrant) object;
                            existingEntrant.addWaitlistedEvent(event);

                            // Update the entrant in the database
                            database.insertUserDocument(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Added to waitlist", Toast.LENGTH_SHORT).show();
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to update entrant in the database", Toast.LENGTH_SHORT).show();
                                }
                            }, existingEntrant);
                        }
                    }, new Database.QueryFailureAction() {
                        @Override
                        public void OnFailure() {
                            Toast.makeText(getContext(), "Entrant not found in the database", Toast.LENGTH_SHORT).show();
                        }
                    }, entrant.getDeviceId());
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to add event to the database", Toast.LENGTH_SHORT).show();
                }
            }, event);

            dialog.dismiss();
        });

        dialog.show();
    }
}




