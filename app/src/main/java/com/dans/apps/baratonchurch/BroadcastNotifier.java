package com.dans.apps.baratonchurch;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.dans.apps.baratonchurch.service.SabbathSchoolFetcherService;

/**
 * Created by duncan on 11/23/17.
 */

public class BroadcastNotifier {

        private LocalBroadcastManager mBroadcaster;
        String intentAction = null;

        public BroadcastNotifier(Context context){
            mBroadcaster=LocalBroadcastManager.getInstance(context);
        }

        public void setCategory(int action){
            if(action == SabbathSchoolFetcherService.ACTION_FETCH_QUARTERLIES){
                intentAction = SabbathSchoolFetcherService.GENERAL_ACTION_QUARTERLY;
            }else if(action == SabbathSchoolFetcherService.ACTION_FETCH_LESSONS){
                intentAction = SabbathSchoolFetcherService.GENERAL_ACTION_LESSONS;
            }else if(action == SabbathSchoolFetcherService.ACTION_FETCH_DAYS){
                intentAction = SabbathSchoolFetcherService.GENERAL_ACTION_DAYS;
            }else if(action == SabbathSchoolFetcherService.ACTION_FETCH_READ){

            }


        }

        public void broadcastError(){
            Intent intent = new Intent();
            intent.setAction(intentAction);
            intent.putExtra(SabbathSchoolFetcherService.RESULTS, SabbathSchoolFetcherService.RESULT_ERROR);
            mBroadcaster.sendBroadcast(intent);
        }

        public void broadcastSuccess(){
            Intent intent = new Intent();
            intent.setAction(intentAction);
            intent.putExtra(SabbathSchoolFetcherService.RESULTS, SabbathSchoolFetcherService.RESULT_OK);
            mBroadcaster.sendBroadcast(intent);
        }



}
