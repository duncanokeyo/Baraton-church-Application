package com.dans.apps.baratonchurch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;

/**
 * Created by duncan on 12/24/17.
 */

public class UiUtils {

    public static boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public static void shareText(String subject, String body, Activity host) {
        Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
        txtIntent .setType("text/plain");
        txtIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        txtIntent .putExtra(android.content.Intent.EXTRA_TEXT, body);
        host.startActivity(Intent.createChooser(txtIntent ,"Share"));
    }

    public static String prettify(HashMap<String, String> body) {
        StringBuilder builder = new StringBuilder();
        for(String key:body.keySet()){
            builder.append("\n").append(key).append("\n").append(body.get(key));
        }
        return builder.toString();
    }
}
