package com.example.guillaumelam34.taskrclient;

/**
 * Created by guillaumelam34 on 10/27/2016.
 */

public class APIStringBuilder {
    public String buildProfileString(int id){
        return APIConstants.PROFILE_ENDPOINT + id;
    }
    public String buildNearbyRequestsString(double latitude, double longitude, int radius){
        String rawUrl = APIConstants.NEARBY_REQUESTS_ENDPOINT;
        rawUrl += "lat=" + latitude;
        rawUrl += "&long=" + longitude;
        rawUrl += "&radius=" + radius;

        return rawUrl;
    }
}
