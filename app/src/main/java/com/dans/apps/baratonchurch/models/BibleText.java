package com.dans.apps.baratonchurch.models;

/**
 * Created by duncan on 11/12/17.
 */

public class BibleText {
    /**bible version**/
    String mVersion;
    /**Reference from the bible*/
    String mReference;
    /**The message**/
    String mMessage;

    public BibleText(String mVersion, String mReference, String mMessage) {
        this.mVersion = mVersion;
        this.mReference = mReference;
        this.mMessage = mMessage;
    }

    public String getmVersion() {
        return mVersion;
    }

    public void setmVersion(String mVersion) {
        this.mVersion = mVersion;
    }

    public String getmReference() {
        return mReference;
    }

    public void setmReference(String mReference) {
        this.mReference = mReference;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
