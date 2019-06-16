package com.dans.apps.baratonchurch.models;

import com.dans.apps.baratonchurch.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duncan on 11/22/17.
 */

public class DailyRead {
    String TAG = "DailyRead";
    String id;
    String date;
    String index;
    String title;
    String content;

    Map<String,String> verses;

    public DailyRead(String id, String date, String index, String title, String content) {
        this.id = id;
        this.date = date;
        this.index = index;
        this.title = title;
        this.content = content;
        this.verses = new HashMap<>(5);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getVerses() {
        return verses;
    }

    public void setVerses(Map<String, String> verses) {
        this.verses = verses;
    }

    public void addToVerses(String bibleName,String text){
        verses.put(bibleName,text);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String s = "id : "+id+"\n"
                +"date : "+date+"\n"
                +"index : "+index+"\n"
                +"title : "+title+"\n\n";
       builder.append(s);

       for(String key:verses.keySet()){
           builder.append("bible version : "+key+" \n");
            builder.append("text : "+verses.get(key)+"\n");
       }

        builder.append(" content : "+content+"\n\n");

        return builder.toString();
    }
}
