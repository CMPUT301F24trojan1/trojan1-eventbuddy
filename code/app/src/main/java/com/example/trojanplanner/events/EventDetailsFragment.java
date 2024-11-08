package com.example.trojanplanner.events;

import android.app.AlertDialog;
import android.os.Bundle;
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


public class EventDetailsFragment extends Fragment {
    private Event event;
    private Entrant entrant;
    private Database database;

    // Constructor with parameters
    public EventDetailsFragment(ConcreteEvent event, Entrant entrant) {
        this.event = event;
        this.entrant = entrant;
        this.database = new Database(); // Initialize Database instance
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setEntrant(Entrant entrant) {
        this.entrant = entrant;
    }

    // Factory method to create an instance of EventDetailsFragment with arguments
    // This was just for testing but tbh that didn't work out
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
        Button buttonEnterNow = view.findViewById(R.id.button_enter_now);

        // call function to populate event details
        populateEventDetails(eventNameTextView, eventLocationTextView, eventDateTextView, recurringDatesTextView, eventDescriptionTextView);

        // enter now button calls showConfirmDialog() to add the user
        buttonEnterNow.setOnClickListener(v -> showConfirmDialog());

        return view;
    }

    public void populateEventDetails(TextView eventNameTextView, TextView eventLocationTextView, TextView eventDateTextView, TextView recurringDatesTextView, TextView eventDescriptionTextView) {
        eventNameTextView.setText(event.getName());
        eventLocationTextView.setText(event.getFacility().getFacilityId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String startDate = dateFormat.format(event.getStartDateTime());
        String endDate = dateFormat.format(event.getEndDateTime());
        eventDateTextView.setText(startDate + " - " + endDate);
        Set<String> recurrenceDays = event.getRecurrenceDays();
        String recurrenceDaysText = String.join(", ", recurrenceDays);
        recurringDatesTextView.setText(recurrenceDaysText);
        eventDescriptionTextView.setText(event.getDescription());
    }
    // should this be public?
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




