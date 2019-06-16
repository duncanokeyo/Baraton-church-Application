package com.dans.apps.baratonchurch;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Created by duncan on 10/7/18.
 */

public class BUCApplication extends Application{
    private static BUCApplication application;
    private FirebaseFirestore store;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true).
                        setTimestampsInSnapshotsEnabled(true)
                .build();
        store = FirebaseFirestore.getInstance();
        store.setFirestoreSettings(settings);
    }

    public static BUCApplication getInstance() {
        return application;
    }

    public FirebaseFirestore getStore() {
        return store;
    }
}
