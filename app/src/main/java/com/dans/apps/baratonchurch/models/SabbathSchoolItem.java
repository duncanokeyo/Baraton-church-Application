package com.dans.apps.baratonchurch.models;

/**
 * Base class for all sabbath school models
 * Created by duncan on 11/19/17.
 */

public abstract class SabbathSchoolItem {
    String title;
    String entryID;
    String quartelyID;
    String id;


    public SabbathSchoolItem(String title, String entryID, String quartelyID, String id) {
        this.title = title;
        this.entryID = entryID;
        this.quartelyID = quartelyID;
        this.id = id;
    }
}
