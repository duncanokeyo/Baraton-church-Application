package com.dans.apps.baratonchurch.models;

/**
 * Created by duncan on 12/21/17.
 */

public class Announcement {
    String title;
    String message;
    String time;
    String senderFirstName;
    String senderLastName;

    public Announcement(String title, String message, String time, String senderFirstName, String senderLastName) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }
}
