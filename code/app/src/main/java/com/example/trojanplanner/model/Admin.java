package com.example.trojanplanner.model;


import java.util.List;

/**
 * Admin class extends the User class and provides additional responsibilities
 * and permissions specific to an administrator role.
 */
public class Admin extends User {

    /**
     * Constructor for the Admin class, inheriting from User.
     *
     * @param lastName     Admin's last name
     * @param firstName    Admin's first name
     * @param email        Admin's email address
     * @param phoneNumber  Admin's phone number
     * @param deviceId     Admin's device ID
     * @param role The role of the created user (should be "admin")
     * @param isOrganizer If the created admin has organizer rights
     * @param isAdmin If the created admin has admin rights (should be true)
     */
    public Admin(String lastName, String firstName, String email, String phoneNumber,
                 String deviceId, String role, boolean isOrganizer, boolean isAdmin) {
        super(lastName, firstName, email, phoneNumber, deviceId, "Admin", isOrganizer, true);
    }


    /**
     * Alternate constructor to create an INCOMPLETE Admin object to allow
     * setting attributes after object creation.
     *
     * @param deviceId The deviceId of the admin account
     * @author Jared Gourley
     */
    public Admin(String deviceId) {
        super(deviceId);
    }



    /**
     * Method to create a terms and conditions agreement to users.
     */
    public void createTermsAndConditionsAgreement(User user) {
        // Implementation for creating terms and conditions
    }

    /**
     * Method to remove events in the system.
     *
     * @param event The event to be removed
     */
    public void removeEvent(Event event) {
        // Implementation for removing an event
    }

    /**
     * Method to remove user profiles in the system.
     *
     * @param user The user profile to be removed
     */
    public void removeProfile(User user) {
        // Implementation for removing a user profile
    }

    /**
     * Method to remove images in the system.
     *
     * @param imagePath The file path of the image to be removed
     */
    public void removeImage(String imagePath) {
        // Implementation for removing an image
    }

    /**
     * Method to remove hashed QR code data associated with events or organizers.
     *
     * @param event The event for which QR code data is removed
     */
    public void removeHashedQRCodeData(Event event) {
        // Implementation for removing QR code data
    }

    /**
     * Method to browse all events in the system.
     *
     * @return List of all events
     */
    public List<Event> browseEvents() {
        // Implementation for browsing events
        return null; // Replace with actual return
    }

    /**
     * Method to browse all user profiles in the system.
     *
     * @return List of all users
     */
    public List<User> browseUserProfiles() {
        // Implementation for browsing user profiles
        return null; // Replace with actual return
    }

    /**
     * Method to browse all images in the system.
     *
     * @return List of image file paths
     */
    public List<String> browseImages() {
        // Implementation for browsing images
        return null; // Replace with actual return
    }

    /**
     * Method to browse all facilities in the system.
     *
     * @return List of facilities
     */
    public List<Facility> browseFacilities() {
        // Implementation for browsing facilities
        return null; // Replace with actual return
    }

    /**
     * Method to remove facilities that violate app terms and conditions.
     *
     * @param facility The facility to be removed
     */
    public void removeFacility(Facility facility) {
        // Implementation for removing a facility that violates terms and conditions
    }
}
