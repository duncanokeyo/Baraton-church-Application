package com.dans.apps.baratonchurch.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.utils.UiUtils;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by duncan on 12/24/17.
 */

public class BaseFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(TAG," on receive called  .... ");
            isNetworkAvailable(context);
        }


        private boolean isNetworkAvailable(Context context) {
            boolean isConnected = UiUtils.isOnline(context);
            LogUtils.d(TAG,"checking if device is online "+isConnected);
            return isConnected;
        }
    }

}
