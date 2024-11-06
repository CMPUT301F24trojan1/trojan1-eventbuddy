package com.example.trojanplanner.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class ConcreteEvent extends Event {

    public ConcreteEvent(String name, String description, String facility, Date startDateTime, Date endDateTime) {
        super(
                name,
                null,           // eventId
                description,
                facility,
                0,                      // daysLeftToRegister
                null,                   // qrCodePath
                null,                   // qrCodeBitmap
                null,                   // picture
                null,                   // pictureUri
                new ArrayList<>(),      // waitingList
                100L,                   // TotalSpots
                100L,                   // availableSpots
                new ArrayList<>(),      // notifications
                null,                   // registrationDeadline
                startDateTime,
                endDateTime,
                false,                  // isRecurring
                new HashSet<>(),        // recurrenceDays
                RecurrenceType.NEVER,   // recurrenceType
                null,                   // eventRecurrenceType
                null,                   // recurrenceEndDate
                0                       // Total_Occurrences
        );
    }

}
