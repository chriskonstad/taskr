package com.example.guillaumelam34.taskrclient;

import android.content.Context;

/**
 * Created by guillaumelam34 on 10/27/2016.
 */

public class APIStringBuilder {
    public String buildProfileString(Context context, int id){
        return APIConstants.profile(context) + "/" + id;
    }

    public String buildNearbyRequestsString(Context context,
                                            double latitude, double longitude, int radius){
        String rawUrl = APIConstants.nearbyRequests(context);
        rawUrl += "?";
        rawUrl += "lat=" + latitude;
        rawUrl += "&long=" + longitude;
        rawUrl += "&radius=" + radius;

        return rawUrl;
    }
}
