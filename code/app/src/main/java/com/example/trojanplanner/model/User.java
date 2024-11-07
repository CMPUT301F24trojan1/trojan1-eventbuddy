package com.example.trojanplanner.model;

/**
 * Contains and stores information tied to a user.
 * @author Madelaine Dalangin modified by Dricmoy Bhattacharjee
 */
public class User implements UserProfile {
    private String deviceId;  // Unique identifier
    private String email;     // User's email
    private String firstName; // User's first name
    private boolean hasAdminRights; // Admin rights flag
    private boolean hasOrganizerRights; // Organizer rights flag
    private String lastName;  // User's last name
    private String pfp;       // Profile picture URL
    private String phone;      // User's phone number

    /**
     * Constructor Method for User
     * @author Madelaine Dalangin, modified by Dricmoy Bhattacharjee
     * @param lastName String
     * @param firstName String
     * @param email String
     * @param phoneNumber String
     * @param deviceId String
     * @param role String
     * @param isOrganizer boolean
     * @param isAdmin boolean
     */
    public User(String deviceId, String email, String firstName, boolean hasAdminRights,
                boolean hasOrganizerRights, String lastName, String pfp, String phone){
        this.deviceId = deviceId;
        this.email = email;
        this.firstName = firstName;
        this.hasAdminRights = hasAdminRights;
        this.hasOrganizerRights = hasOrganizerRights;
        this.lastName = lastName;
        this.pfp = pfp;
        this.phone = phone;
    }

    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean isHasAdminRights() {
        return hasAdminRights;
    }

    public void setHasAdminRights(boolean hasAdminRights) {
        this.hasAdminRights = hasAdminRights;
    }

    public boolean isHasOrganizerRights() {
        return hasOrganizerRights;
    }

    public void setHasOrganizerRights(boolean hasOrganizerRights) {
        this.hasOrganizerRights = hasOrganizerRights;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }
}