package com.example.trojanplanner.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.trojanplanner.App;
import com.example.trojanplanner.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// facilities can have .... multiple events (facility isn't implemented yet)
public class Event implements Serializable {
    private String name;
    private String eventId;
    private Facility facility;
    private String description;
    private float price;
    private int daysLeftToRegister;
    private String qrCodePath;
    private SerialBitmap qrCodeBitmap;
    private SerialBitmap picture; // Event picture bitmap
    private String pictureFilePath; // Path to where the picture bitmap is stored in Firebase Storage

    //participant lists
    private ArrayList<User> waitingList;
    private ArrayList<User> pendingList;
    private ArrayList<User> cancelledList;
    private ArrayList<User> enrolledList;

    private Date waitlistOpen;
    private Date waitlistClose;
    private int waitlistCapacity;
    private boolean requiresGeolocation;
    //is there a better way of representing this?
    private String status; // "upcoming", "ongoing", "cancelled", "finished"

    private Long TotalSpots;
    private Long availableSpots;
    // Calculated based on maximumAttendees - participants.size()
    // implement this class
    private ArrayList<Notification> notifications;

    private Date registrationDeadline;
    private Date startDateTime;
    private Date endDateTime;

    //for recurrence settings
    private boolean isRecurring;
    private ArrayList<String> recurrenceDays; ///create a hash on creation
    private RecurrenceType recurrenceType;
    private String eventRecurrenceType;
    private Date recurrenceEndDate;
    private int Total_Occurrences;

    public Event(){
        this.availableSpots = 0L;
        this.waitingList = new ArrayList<User>();
    }

//    public Event(String name, String description, float price, String facility, Date startDateTime, Date endDateTime, int daysLeftToRegister, long totalSpots, long availableSpots) {
//    }

    // https://www.w3schools.com/java/java_enums.asp
    public enum RecurrenceType {
        UNTIL_DATE, AFTER_OCCURRENCES, NEVER
    }

    public enum Status {
        UPCOMING, ONGOING, CANCELLED, FINISHED
    }




//    public Event(String name, String eventId, String facility, String description, int daysLeftToRegister, String qrCodePath, Bitmap qrCodeBitmap, Bitmap picture, String pictureFilePath, ArrayList<User> pendingList, ArrayList<User> waitingList, ArrayList<User> cancelledList, ArrayList<User> enrolledList, Date waitlistOpen, Date waitlistClose, boolean requiresGeolocation, String status, Long totalSpots, Long availableSpots, ArrayList<Notification> notifications, Date registrationDeadline, Date startDateTime, Date endDateTime, boolean isRecurring, Set<String> recurrenceDays, String eventRecurrenceType, Date recurrenceEndDate, int total_Occurrences, RecurrenceType recurrenceType) {
//        this.name = name;
//        this.eventId = eventId;
//        this.facility = facility;
//        this.description = description;
//        this.daysLeftToRegister = daysLeftToRegister;
//        this.qrCodePath = qrCodePath;
//        this.qrCodeBitmap = qrCodeBitmap;
//        this.picture = picture;
//        this.pictureFilePath = pictureFilePath;
//
//        //make these new ArrayList<>();?
//        this.pendingList = pendingList;
//        this.waitingList = waitingList;
//        this.cancelledList = cancelledList;
//        this.enrolledList = enrolledList;
//
//        this.waitlistOpen = waitlistOpen;
//        this.waitlistClose = waitlistClose;
//        this.requiresGeolocation = false; //default
//        this.status = "upcoming";
//        this.TotalSpots = totalSpots;
//        this.availableSpots = availableSpots;
//        this.notifications = notifications;
//        this.registrationDeadline = registrationDeadline;
//        this.startDateTime = startDateTime;
//        this.endDateTime = endDateTime;
//        this.isRecurring = isRecurring;
//        this.recurrenceDays = recurrenceDays;
//        this.eventRecurrenceType = eventRecurrenceType;
//        this.recurrenceEndDate = recurrenceEndDate;
//        Total_Occurrences = total_Occurrences;
//        this.recurrenceType = RecurrenceType.NEVER; // I set this by default
//    }

    public Event(String eventId, String name, String description, float price, Facility facility, Date startDateTime, Date endDateTime,
                 int daysLeftToRegister, Long totalSpots, Long availableSpots) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.facility = facility;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.daysLeftToRegister = daysLeftToRegister;
        this.TotalSpots = totalSpots;
        this.availableSpots = availableSpots;

        // Initialize remaining fields with default values if necessary
        this.qrCodePath = null;
        this.qrCodeBitmap = null;
        this.picture = null;
        this.pictureFilePath = null;
        this.waitingList = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.registrationDeadline = null;
        this.isRecurring = false;
        this.recurrenceDays = new ArrayList<>();
        this.recurrenceType = RecurrenceType.NEVER;
        this.eventRecurrenceType = null;
        this.recurrenceEndDate = null;
        this.Total_Occurrences = 0;
        this.status = "upcoming";
    }

    // Constructor with no image args
    public Event(String eventId, String eventName, String eventDescription, float price) {
        this.eventId = eventId;
        this.name = eventName;
        this.description = eventDescription;
        this.price = price;
    }

    // Constructor with image args
    public Event(String eventId, String eventName, String eventDescription, float price, Bitmap imageBitmap) {
        this.eventId = eventId;
        this.name = eventName;
        this.description = eventDescription;
        this.price = price;
        this.setPicture(imageBitmap); // Converts the bitmap to SerialBitmap
    }

    /**
     * Alternate constructor to create an INCOMPLETE Event object to allow
     * setting attributes after object creation.
     *
     * @param eventId The eventId of the event
     * @author Jared Gourley
     */
    public Event(String eventId) {
        this(eventId, "UNKNOWN", "UNKNOWN", 0);
    }



    /**
     * A method to return one of the four arrays using an int, making looping over them easier.
     * @param index 0 = enrolledList, 1 = pendingList, 2 = waitingList, 3 = cancelledList
     * @return One of the above arrays
     * @author Jared Gourley
     */
    public ArrayList<User> returnEntrantsArrayByIndex(int index) {
        if (index == 0) {
            return enrolledList;
        }
        if (index == 1) {
            return pendingList;
        }
        if (index == 2) {
            return waitingList;
        }
        if (index == 3) {
            return cancelledList;
        }
        else {
            throw new IndexOutOfBoundsException("Index must be between 0-3");
        }
    }


    /**
     * Method which returns the index in which the entrant matches the given event ID.
     * @param array The array to search through
     * @param deviceId The device ID to search for in the array.
     * @return The index if found or -1 otherwise.
     * @author Jared Gourley
     */
    public int findIndexWithId(ArrayList<User> array, String deviceId) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getDeviceId().equals(deviceId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Replaces an entrant object from any of the entrant arrays with the new entrant object
     * if the device ids match. Only replaces one instance under the assumption that no
     * duplicates should ever appear.
     * @param entrant The new entrant object to replace if ids match
     * @return true if a replace happened, false if not
     * @author Jared Gourley
     */
    public boolean replaceEntrantMatchingId(Entrant entrant) {
        String entrantId = entrant.getDeviceId();
        int foundIndex = -1;
        ArrayList<User> array;
        for (int i = 0; i < 4; i++) {
            array = returnEntrantsArrayByIndex(i);
            foundIndex = findIndexWithId(array, entrantId);
            if (foundIndex != -1) {
                array.set(foundIndex, entrant);
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "Event: " + name + " (" + eventId + ")";
    }


    public ArrayList<User> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<User> waitingList) {
        this.waitingList = waitingList;
    }

    public ArrayList<User> getPendingList() {
        return pendingList;
    }

    public void setPendingList(ArrayList<User> pendingList) {
        this.pendingList = pendingList;
    }

    public String getPictureFilePath() {
        return pictureFilePath;
    }

    public void setPictureFilePath(String pictureFilePath) {
        this.pictureFilePath = pictureFilePath;
    }

    public Bitmap getQrCodeBitmap() {
        if (this.qrCodeBitmap == null) {
            return null;
        }
        else {
            return qrCodeBitmap.getBitmap();
        }
    }

    public void setQrCodeBitmap(Bitmap qrCodeBitmap) {
        if (qrCodeBitmap == null) {
            this.qrCodeBitmap = null;
        }
        else {
            this.qrCodeBitmap = new SerialBitmap(qrCodeBitmap);
        }
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public int getDaysLeftToRegister() {
        return daysLeftToRegister;
    }

    public void setDaysLeftToRegister(int daysLeftToRegister) {
        this.daysLeftToRegister = daysLeftToRegister;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    // fuck you java
    public void setPriceDouble(Double price) {
        this.price = price.floatValue();
    }

    public int getWaitlistCapacity() {
        return waitlistCapacity;
    }

    public void setWaitlistCapacityint(int waitlistCapacity) {
        this.waitlistCapacity = waitlistCapacity;
    }

    // fuck you again java
    public void setWaitlistCapacity(Long waitlistCapacity) {
        this.waitlistCapacity = waitlistCapacity.intValue();
    }

    public ArrayList<User> getCancelledList() {
        return cancelledList;
    }

    public void setCancelledList(ArrayList<User> cancelledList) {
        this.cancelledList = cancelledList;
    }

    public ArrayList<User> getEnrolledList() {
        return enrolledList;
    }

    public void setEnrolledList(ArrayList<User> enrolledList) {
        this.enrolledList = enrolledList;
    }

    public Date getWaitlistOpen() {
        return waitlistOpen;
    }

    public void setWaitlistOpen(Date waitlistOpen) {
        this.waitlistOpen = waitlistOpen;
    }

    public Date getWaitlistClose() {
        return waitlistClose;
    }

    public void setWaitlistClose(Date waitlistClose) {
        this.waitlistClose = waitlistClose;
    }

    public boolean isRequiresGeolocation() {
        return requiresGeolocation;
    }

    public void setRequiresGeolocation(boolean requiresGeolocation) {
        this.requiresGeolocation = requiresGeolocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public ArrayList<String> getRecurrenceDays() {
        return recurrenceDays;
    }

    public void setRecurrenceDays(ArrayList<String> recurrenceDays) {
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

    private boolean isValidRecurrenceDay(String day) {
        return day.equals("M") || day.equals("T") || day.equals("W") || day.equals("R") ||
                day.equals("F") || day.equals("S");
    }


    // adding day of the week
    public void addRecurrenceDay(String day) {
        if (isValidRecurrenceDay(day.toUpperCase())) {
            recurrenceDays.add(day.toUpperCase());
            validateRecurrenceSettings();
        }
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

        if (startDateTime == null || recurrenceDays == null || recurrenceDays.isEmpty()) return;

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
            case Calendar.SUNDAY: return "U";
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
        if (availableSpots != null && availableSpots > 0 && waitingList != null && !waitingList.contains(user)) {
            waitingList.add(user);
            availableSpots--;  // Assuming availableSpots is an integer or Long initialized to a value
            return true;
        }
        return false;  // No spots available, or user is already in the list
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
            this.picture = new SerialBitmap(getDefaultPicture());
        } else {
            this.picture = new SerialBitmap(picture);
        }
    }

    /**
     * Returns the bitmap picture for the event. If null, assigns the default picture and
     * returns it to avoid null errors
     * @return The current bitmap for the event or the default bitmap
     */
    public Bitmap getPicture() {
        // If the picture attribute is null, assign it the default value since it should have a value
        if (picture == null) {
            picture = new SerialBitmap(getDefaultPicture());
        }
        return picture.getBitmap();
    }

    // helper method to load a default picture
    private Bitmap getDefaultPicture() {
        // load a default image resource as a Bitmap
        return BitmapFactory.decodeResource(App.activity.getResources(), R.drawable.default_event_pic);
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

    // Checks if registration is open based on registrationDeadline
    public boolean isRegistrationOpen() {
        if (registrationDeadline == null) return true; // No deadline set
        Date currentDate = new Date();
        return currentDate.before(registrationDeadline);
    }

    // Checks if a user can be added to the waitlist
    public boolean canAddToWaitlist() {
        return waitingList.size() < TotalSpots && isRegistrationOpen();
    }

    // Checks if the waitlist has reached a maximum capacity (if applicable)
    public boolean isWaitlistFull(int maxCapacity) {
        return waitingList.size() >= maxCapacity;
    }

    // Adds a user to the waitlist if possible
    public boolean addToWaitlist(User user) {
        if (canAddToWaitlist() && !waitingList.contains(user)) {
            waitingList.add(user);
            return true;
        }
        return false;
    }

    // Removes a user from the waitlist
    public boolean removeFromWaitlist(User user) {
        return waitingList.remove(user);
    }

    public void updateStatus() {
        updateStatus(new Date()); // Calls the overloaded method with the current date
    }

    // Overloaded method for testing with a custom date
    public void updateStatus(Date customDate) {
        System.out.println("Custom Date: " + customDate);
        System.out.println("Start Date: " + startDateTime);
        System.out.println("End Date: " + endDateTime);

        if (customDate.before(startDateTime)) {
            status = "upcoming";
        } else if (customDate.after(endDateTime)) {
            status = "finished";
        } else {
            status = "ongoing";
        }
        System.out.println("Updated Status: " + status);
    }




    // Retrieve upcoming occurrences based on recurrence settings within a specified timeframe
    public List<Date> getUpcomingOccurrences(Date endDate) {
        List<Date> occurrences = new ArrayList<>();
        if (!isRecurring) {
            occurrences.add(startDateTime);
            return occurrences;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDateTime);

        while (calendar.getTime().before(endDate) && (recurrenceType != RecurrenceType.NEVER)) {
            String dayOfWeek = getDayOfWeek(calendar);
            if (recurrenceDays.contains(dayOfWeek)) {
                occurrences.add(calendar.getTime());
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return occurrences;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check for reference equality
        if (obj == null || getClass() != obj.getClass()) return false; // Check for class equality
        Event event = (Event) obj;
        return eventId != null && eventId.equals(event.eventId); // Compare based on eventId
    }

    @Override
    public int hashCode() {
        return eventId != null ? eventId.hashCode() : 0; // Generate hash based on eventId
    }


}
