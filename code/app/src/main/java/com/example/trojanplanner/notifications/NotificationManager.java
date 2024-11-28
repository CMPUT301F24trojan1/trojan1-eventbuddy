package com.example.trojanplanner.notifications;
import com.example.trojanplanner.App;
import com.example.trojanplanner.model.User;
import java.util.ArrayList;

public class NotificationManager {
    public NotificationManager() {}

    /**
     * Notify all users in the waiting list.
     *
     * @param waitlist List of users in the waiting list.
     * @param title    Notification title.
     * @param message  Notification message.
     */
    public void notifyWaitlist(ArrayList<User> waitlist, String title, String message) {
        for (User user : waitlist) {
            String topic = user.getDeviceId();
            App.sendAnnouncement(topic, title, message);
        }
    }

    /**
     * Notify all selected users.
     *
     * @param selectedList List of selected users.
     * @param title        Notification title.
     * @param message      Notification message.
     */
    public void notifySelected(ArrayList<User> selectedList, String title, String message) {
        for (User user : selectedList) {
            String topic = user.getDeviceId();
            App.sendAnnouncement(topic, title, message);
        }
    }

    /**
     * Notify all cancelled users.
     *
     * @param cancelledList List of cancelled users.
     * @param title         Notification title.
     * @param message       Notification message.
     */
    public void notifyCancelled(ArrayList<User> cancelledList, String title, String message) {
        for (User user : cancelledList) {
            String topic = user.getDeviceId();
            App.sendAnnouncement(topic, title, message);
        }
    }

    /**
     * Notify all pending users.
     *
     * @param pendingList List of pending users.
     * @param title       Notification title.
     * @param message     Notification message.
     */
    public void notifyPending(ArrayList<User> pendingList, String title, String message) {
        for (User user : pendingList) {
            String topic = user.getDeviceId();
            App.sendAnnouncement(topic, title, message);
        }
    }
}
