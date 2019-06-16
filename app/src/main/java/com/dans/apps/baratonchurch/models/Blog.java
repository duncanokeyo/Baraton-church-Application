package com.dans.apps.baratonchurch.models;

/**
 * Created by duncan on 10/13/18.
 */

public class Blog {
    String content;
    String author;
    String date;
    String title;
    String id;

    public Blog(String content, String author, String date, String title) {
        this.content = content;
        this.author = author;
        this.date = date;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
