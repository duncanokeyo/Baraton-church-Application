package com.dans.apps.baratonchurch.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.utils.LogUtils;

import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by duncan on 10/29/18.
 */

public class FetchWorker extends Worker {
    public String TAG = "FetchService";

    public FetchWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        LogUtils.d(TAG, "start of work");

        try {
            Fetcher.checkQuarterlies(false, Constants.DefaultLanguage,getApplicationContext(),true);
        } catch (Exception e) {
            return Result.FAILURE;
        }
        try {
            Fetcher.checkSermons(getApplicationContext());
        } catch (Exception e) {
            return Result.FAILURE;
        }
        try{
            Fetcher.updateBlogTable(getApplicationContext());
        }catch (Exception e){
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }
}
