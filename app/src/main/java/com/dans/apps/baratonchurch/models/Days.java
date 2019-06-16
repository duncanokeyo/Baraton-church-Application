package com.dans.apps.baratonchurch.models;

/**
 * Represents the day
 * Created by duncan on 12/18/17.
 */

public class Days {
    String title;
    String id;
    String quarterlyID;
    String lessonID;
    String date;
    String fullReadPath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuarterlyID() {
        return quarterlyID;
    }

    public void setQuarterlyID(String quarterlyID) {
        this.quarterlyID = quarterlyID;
    }

    public String getLessonID() {
        return lessonID;
    }

    public void setLessonID(String lessonID) {
        this.lessonID = lessonID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFullReadPath() {
        return fullReadPath;
    }

    public void setFullReadPath(String fullReadPath) {
        this.fullReadPath = fullReadPath;
    }
}
