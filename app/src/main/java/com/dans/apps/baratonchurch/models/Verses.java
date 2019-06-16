package com.dans.apps.baratonchurch.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by duncan on 11/22/17.
 */

public class Verses {
    String bibleVersion;
    Map<String,String> map;

    public Verses() {
        map = new HashMap<>();
    }

    public String getBibleVersion() {
        return bibleVersion;
    }

    public void setBibleVersion(String bibleVersion) {
        this.bibleVersion = bibleVersion;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void insertVerse(String reference,String content){
        map.put(reference,content);
    }
}

