package com.dans.apps.baratonchurch.models;

/**
 * Created by duncan on 11/19/17.
 */

public class LessonItem extends SabbathSchoolItem {
    String startDate;
    String endDate;
    String path;
    String fullPath;
    String coverPath;


    public LessonItem(String title, String entryID, String quartelyID, String id,
                      String startDate,String endDate,String path,String fullPath,
                      String coverPath) {
        super(title, entryID, quartelyID, id);

        this.startDate = startDate;
        this.endDate = endDate;
        this.path = path;
        this.fullPath = fullPath;
        this.coverPath = coverPath;
    }

    public String getQuarterlyId(){
        return quartelyID;
    }
    public String getTitle(){
        return title;
    }
    public String getId(){
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}


