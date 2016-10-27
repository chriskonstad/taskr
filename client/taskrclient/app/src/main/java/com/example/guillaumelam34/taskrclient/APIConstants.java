package com.example.guillaumelam34.taskrclient;

/**
 * Created by guillaumelam34 on 10/27/2016.
 */
public class APIConstants {
    public static final String HOST_DOMAIN = "10.0.2.2:3000";

    //Endpoints
    public static final String PROFILE_ENDPOINT = "http://" + HOST_DOMAIN + "/api/v1/profile/";
    public static final String NEARBY_REQUESTS_ENDPOINT = "http://" + HOST_DOMAIN + "/api/v1/requests/nearby?";

    //Intent putExtra() strings
    public static final String PROFILE_RESULT = "Profile result";
}
