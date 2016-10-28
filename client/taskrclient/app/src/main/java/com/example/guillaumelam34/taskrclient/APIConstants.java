package com.example.guillaumelam34.taskrclient;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by guillaumelam34 on 10/27/2016.
 */
public class APIConstants {
    //Endpoints
    public static final String profile(Context context) {
        return getUrl(context, "/api/v1/profile");
    }

    public static final String nearbyRequests(Context context) {
        return getUrl(context, "/api/v1/requests/nearby");
    }

    //Intent putExtra() strings
    public static final String PROFILE_RESULT = "Profile result";

    private static String getUrl(Context context, String endpoint) {
        String url = "http://" + getPref(context, R.string.key_hostname) + endpoint;
        Log.i("APIConstants", "Generated URL: " + url);
        return url;
    }

    private static String getPref(Context context, int key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(key), "");
    }
}
