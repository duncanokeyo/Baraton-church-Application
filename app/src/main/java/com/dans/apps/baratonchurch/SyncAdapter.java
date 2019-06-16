package com.dans.apps.baratonchurch;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Syncs data from the remote server and our local database
 * Created by duncan on 11/15/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private String TAG = "SyncAdapter";
    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize,allowParallelSyncs);
        mContentResolver=context.getContentResolver();
    }

    //validates data in the quarterlies database
    //validates data in the sermons database


    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        //fetch data from the server and update the database
        LogUtils.d(TAG, "start of Sync");
        try {
            Fetcher.checkQuarterlies(false,Constants.DefaultLanguage,getContext(),true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Fetcher.checkSermons(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
