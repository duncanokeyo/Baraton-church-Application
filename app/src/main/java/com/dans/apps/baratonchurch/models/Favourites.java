package com.dans.apps.baratonchurch.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by duncan on 12/22/17.
 */

public class Favourites implements Parcelable{
    String entryID;
    String date;
    String lessonCover;
    String lessonTitle;
    String title;
    String content;
    String bibleReference;

    public Favourites(String entryID, String date, String lessonCover, String lessonTitle, String title, String content, String bibleReference) {
        this.entryID = entryID;
        this.date = date;
        this.lessonCover = lessonCover;
        this.lessonTitle = lessonTitle;
        this.title = title;
        this.content = content;
        this.bibleReference = bibleReference;
    }

    protected Favourites(Parcel in) {
        entryID = in.readString();
        date = in.readString();
        lessonCover = in.readString();
        lessonTitle = in.readString();
        title = in.readString();
        content = in.readString();
        bibleReference = in.readString();
    }

    public static final Creator<Favourites> CREATOR = new Creator<Favourites>() {
        @Override
        public Favourites createFromParcel(Parcel in) {
            return new Favourites(in);
        }

        @Override
        public Favourites[] newArray(int size) {
            return new Favourites[size];
        }
    };

    public String getEntryID() {
        return entryID;
    }

    public String getDate() {
        return date;
    }

    public String getLessonCover() {
        return lessonCover;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getBibleReference() {
        return bibleReference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(entryID);
        parcel.writeString(date);
        parcel.writeString(lessonCover);
        parcel.writeString(lessonTitle);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(bibleReference);
    }
}
