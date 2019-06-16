package com.dans.apps.baratonchurch.utils;

import android.os.RemoteException;
import android.util.Log;

import com.dans.apps.baratonchurch.BuildConfig;

/**
 * Base Class for logging app content
 * Created by duncan on 6/25/17.
 */

public class LogUtils {

    private static boolean shouldLog = BuildConfig.DEBUG;

    public static void d(String TAG, String message){
        if(shouldLog){
            Log.d("BARATONCHURCH/"+TAG,message);
        }
    }

    public static void e(String TAG,String message){
        if(shouldLog){
            Log.e("BARATONCHURCH/"+TAG,message);
        }
    }

    public static void i(String tag, String s) {
        if(shouldLog){
            Log.e("BARATONCHURCH/"+tag,s);
        }
    }

    public static void v(String tag, String s) {
        if(shouldLog){
            Log.v("BARATONCHURCH/"+tag,s);
        }
    }

    public static void w(String tag, String s, RemoteException e) {
        if(shouldLog){
            if(e!=null){
                Log.w("BARATONCHURCH/"+tag,s,e);
            }else {
                Log.w("BARATONCHURCH/"+tag,s);
            }
        }
    }

}
