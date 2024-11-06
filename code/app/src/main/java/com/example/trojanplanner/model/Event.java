package com.example.trojanplanner.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

// facilities can have .... multiple events (facility isn't implemented yet)
public class Event implements Serializable {
    private String name;
    private String eventId;
    private String facility;
    private String description;
    private int daysLeftToRegister;
    private String qrCodePath;
    private Bitmap qrCodeBitmap;

    private Bitmap picture; // New attribute to store the event picture
    private String pictureUri;

    //if photo attribute is null we want a default image or smht..
    //to do 1 image for now:
    private ArrayList<User> waitingList;
    private Long TotalSpots;
    private Long availableSpots;
    // Calculated based on maximumAttendees - participants.size()
    // implement this class
    private ArrayList<Notification> notifications;

    private Date registrationDeadline;

    private Date startDateTime;
    private Date endDateTime;

    private boolean isRecurring;
    private Set<String> recurrenceDays; ///create a hash on creaton

    private RecurrenceType recurrenceType;
    private String eventRecurrenceType;

    private Date recurrenceEndDate;
    private int Total_Occurrences;


    // https://www.w3schools.com/java/java_enums.asp
    public enum RecurrenceType {
        UNTIL_DATE, AFTER_OCCURRENCES, NEVER
    }


    public Event(String name, String eventId, String description, String facility, int daysLeftToRegister, String qrCodePath, Bitmap qrCodeBitmap, Bitmap picture, String pictureUri, ArrayList<User> waitingList, Long totalSpots, Long availableSpots, ArrayList<Notification> notifications, Date registrationDeadline, Date startDateTime, Date endDateTime, boolean isRecurring, Set<String> recurrenceDays, RecurrenceType recurrenceType, String eventRecurrenceType, Date recurrenceEndDate, int total_Occurrences) {
        this.name = name;
        this.eventId = eventId;
        this.description = description;
        this.facility = facility;
        this.daysLeftToRegister = daysLeftToRegister;
        this.qrCodePath = qrCodePath;
        this.qrCodeBitmap = qrCodeBitmap;
        this.picture = picture;
        this.pictureUri = pictureUri;
        this.waitingList = waitingList;
        this.TotalSpots = totalSpots;
        this.availableSpots = availableSpots;
        this.notifications = notifications;
        this.registrationDeadline = registrationDeadline;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isRecurring = isRecurring;
        this.recurrenceDays = recurrenceDays;
        this.recurrenceType = recurrenceType;
        this.eventRecurrenceType = eventRecurrenceType;
        this.recurrenceEndDate = recurrenceEndDate;
        Total_Occurrences = total_Occurrences;

    }

    // Constructor with no image args
    public Event(String eventName, String eventDescription) {
        this.name = eventName;
        this.description = eventDescription;
    }

    // Constructor with image args
    public Event(String eventName, String eventDescription, Bitmap imageResourceId) {
        this.name = eventName;
        this.description = eventDescription;
        this.picture = imageResourceId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDaysLeftToRegister() {
        return daysLeftToRegister;
    }

    public void setDaysLeftToRegister(int daysLeftToRegister) {
        this.daysLeftToRegister = daysLeftToRegister;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public Bitmap getQrCodeBitmap() {
        return qrCodeBitmap;
    }

    public void setQrCodeBitmap(Bitmap qrCodeBitmap) {
        this.qrCodeBitmap = qrCodeBitmap;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public ArrayList<User> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<User> waitingList) {
        this.waitingList = waitingList;
    }

    public Long getTotalSpots() {
        return TotalSpots;
    }

    public void setTotalSpots(Long totalSpots) {
        TotalSpots = totalSpots;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public Long getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Long availableSpots) {
        this.availableSpots = availableSpots;
    }

    public Date getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Date registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Set<String> getRecurrenceDays() {
        return recurrenceDays;
    }

    public void setRecurrenceDays(Set<String> recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public String getEventRecurrenceType() {
        return eventRecurrenceType;
    }

    public void setEventRecurrenceType(String eventRecurrenceType) {
        this.eventRecurrenceType = eventRecurrenceType;
    }

    public Date getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(Date recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public int getTotal_Occurrences() {
        return Total_Occurrences;
    }

    ///------made functions ----////

    // manages random raffle to pick users who registered
    public User selectRandomEntrant() {
        if (waitingList == null || waitingList.isEmpty()) {
            return null; // null if waiting list is null or empty
        }
        Random random = new Random();
        int index = random.nextInt(waitingList.size());
        return waitingList.get(index);
    }



    // adding day of the week
    public void addRecurrenceDay(String day) {
        recurrenceDays.add(day.toUpperCase());
        validateRecurrenceSettings();
    }

    //removing the day of the week
    public void removeRecurrenceDay(String day) {
        recurrenceDays.remove(day.toUpperCase());
        validateRecurrenceSettings();
    }

    //validate, but if its NEVER should work as well
    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
        validateRecurrenceSettings();
    }

    public void setTotal_Occurrences(int total_Occurrences) {
        if (recurrenceType == RecurrenceType.AFTER_OCCURRENCES) {
            this.Total_Occurrences = total_Occurrences;
            validateRecurrenceSettings();
        }
    }

    // validate and adjust recurrence end date or occurrences
    private void validateRecurrenceSettings() {
        if (!isRecurring || recurrenceType == RecurrenceType.NEVER) {
            recurrenceEndDate = null;
            Total_Occurrences = 0;
            return;
        }
        if (recurrenceType == RecurrenceType.AFTER_OCCURRENCES) {
            calculateRecurrenceEndDateFromOccurrences();
        } else if (recurrenceType == RecurrenceType.UNTIL_DATE) {
            calculateRecurrenceOccurrencesFromEndDate();
        }
    }

    private void calculateRecurrenceEndDateFromOccurrences() {
        if (Total_Occurrences <= 0 || recurrenceDays.isEmpty()) {
            recurrenceEndDate = startDateTime;
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDateTime);
        int occurrencesCount = 0;

        while (occurrencesCount < Total_Occurrences) {
            String dayOfWeek = getDayOfWeek(calendar);

            if (recurrenceDays.contains(dayOfWeek)) {
                occurrencesCount++;
                recurrenceEndDate = calendar.getTime();
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // set the time of recurrenceEndDate to match the event's end time
        Calendar endTimeCalendar = Calendar.getInstance();
        endTimeCalendar.setTime(recurrenceEndDate);
        endTimeCalendar.set(Calendar.HOUR_OF_DAY, endDateTime.getHours());
        endTimeCalendar.set(Calendar.MINUTE, endDateTime.getMinutes());
        endTimeCalendar.set(Calendar.SECOND, 0);
        endTimeCalendar.set(Calendar.MILLISECOND, 0);
        recurrenceEndDate = endTimeCalendar.getTime();
    }


    private void calculateRecurrenceOccurrencesFromEndDate() {
        if (recurrenceEndDate == null || recurrenceDays.isEmpty()) {
            Total_Occurrences = 0;
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDateTime);
        Total_Occurrences = 0;

        while (calendar.getTime().before(recurrenceEndDate) || calendar.getTime().equals(recurrenceEndDate)) {
            String dayOfWeek = getDayOfWeek(calendar);
            if (recurrenceDays.contains(dayOfWeek)) {
                Total_Occurrences++;
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    // helper method to get the day of the week abbreviation (e.g., "M" for Monday)
    private String getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "S";
            case Calendar.MONDAY: return "M";
            case Calendar.TUESDAY: return "T";
            case Calendar.WEDNESDAY: return "W";
            case Calendar.THURSDAY: return "R";
            case Calendar.FRIDAY: return "F";
            case Calendar.SATURDAY: return "S";
            default: return "";
        }
    }
    //FIREBASE, uses helper function above to change Recurrence TYPE
    public void convertToEndDateType() {
        if (recurrenceType == RecurrenceType.AFTER_OCCURRENCES && Total_Occurrences > 0) {
            this.calculateRecurrenceEndDateFromOccurrences(); //calculates and sets recurrence end date attribute
            this.recurrenceType = RecurrenceType.UNTIL_DATE; //changes type here
            this.Total_Occurrences = 0;
        }
    }

    public boolean addParticipant(User user) {
        if (availableSpots > 0 && !waitingList.contains(user)) {
            waitingList.add(user);
            availableSpots--;
            return true;
        }
        return false; // if no spots are available or user already exists
    }

    public boolean removeParticipant(User user) {
        if (waitingList.contains(user)) {
            waitingList.remove(user);
            availableSpots++;
            return true;
        }
        return false; // if user is not in the waiting list
    }

    public boolean isUserRegistered(User user) {
        return waitingList.contains(user);
    }

    public void updateAvailableSpots() {
        if (waitingList != null) {
            availableSpots = Math.max(0, TotalSpots - waitingList.size());
        }
    }
    public void addNotification(Notification notification) {
        if (notifications == null) {
            notifications = new ArrayList<>();
        }
        notifications.add(notification);
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    //prob wont need this since qr code is a standalone thng
    public String generateQRCodePath() {
        if (qrCodePath == null || qrCodePath.isEmpty()) {
            // Example of generating a path based on event ID
            qrCodePath = "events/qrcodes/" + name.replaceAll(" ", "_") + "_qr.png";
        }
        return qrCodePath;
    }

    // new method to set picture with a default option
    public void setPicture(Bitmap picture) {
        if (picture == null) {
            // Assign a default picture if the provided one is null
            this.picture = getDefaultPicture();
        } else {
            this.picture = picture;
        }
    }

    public Bitmap getPicture() {
        if (picture == null) {
            return getDefaultPicture();
        }
        return picture;
    }

    // helper method to load a default picture
    private Bitmap getDefaultPicture() {
        // load a default image resource as a Bitmap
        // replace R.drawable.default_image with  actual default image resource ID TO DOOOOOO
        return BitmapFactory.decodeResource(Resources.getSystem(), android.R.drawable.ic_menu_gallery);
    }

    public List<Date> getOccurrenceDates() {
        List<Date> occurrenceDates = new ArrayList<>();
        if (!isRecurring || recurrenceDays.isEmpty()) {
            occurrenceDates.add(startDateTime);
            return occurrenceDates;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDateTime);

        Date endDate = recurrenceEndDate != null ? recurrenceEndDate : startDateTime;
        int occurrences = Total_Occurrences > 0 ? Total_Occurrences : Integer.MAX_VALUE;

        int occurrenceCount = 0;
        while ((calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) && occurrenceCount < occurrences) {
            String dayOfWeek = getDayOfWeek(calendar);
            if (recurrenceDays.contains(dayOfWeek)) {
                occurrenceDates.add(calendar.getTime());
                occurrenceCount++;
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return occurrenceDates;
    }


}
