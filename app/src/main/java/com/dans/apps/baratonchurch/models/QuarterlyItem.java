package com.dans.apps.baratonchurch.models;

/**
 * Created by duncan on 11/19/17.
 */

public class QuarterlyItem extends SabbathSchoolItem {

    String description;
    String language;
    String human_date;
    String coverPath;
    String fullPath;
    String primaryColor;
    String secondaryColor;
    String index;
    String startDate;
    String endDate;
    public QuarterlyItem(String entryID, String title, String description,
                         String id, String language, String human_date, String coverPath,
                         String fullPath, String primaryColor, String secondaryColor, String index, String startDate, String endDate) {

        super(title,entryID,id,id);

        this.description = description;
        this.language = language;
        this.human_date = human_date;
        this.coverPath = coverPath;
        this.fullPath = fullPath;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.index=index;
        this.startDate=startDate;
        this.endDate=endDate;

    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHuman_date() {
        return human_date;
    }

    public void setHuman_date(String human_date) {
        this.human_date = human_date;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }
}
