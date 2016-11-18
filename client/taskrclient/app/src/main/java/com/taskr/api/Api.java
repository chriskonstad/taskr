package com.taskr.api;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.gson.JsonElement;
import com.taskr.client.LocationProvider;
import com.taskr.client.MainActivity;
import com.taskr.client.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public interface Api {
    /**
     * The API's callback interface for asynchronous (network) calls
     * @param <T> the type the API call returns
     */
    public interface ApiCallback<T> {
        /**
         * The callback for a successful API call
         * @param returnValue the value returned from the server as a Java class
         */
        public void onSuccess(T returnValue);

        /**
         * The callback for an unsuccessful API call
         * @param message the error message
         */
        public void onFailure(String message);
    }

    /**
     * Get the logged in Taskr user ID
     * @return API's taskr user id
     */
    public int getId();

    /**
     * Get the name of the logged in user
     * @return user's name
     */
    public String getName();

    /**
     * get the email of the logged in user
     * @return user's email
     */
    public String getEmail();

    /**
     * Get the Facebook ID of the logged in user
     * @return user's Facebook ID
     */
    public String getFbid();

    /**
     * Force the API to refresh the user's current location
     * @param activity used for showing error dialog
     */
    public void refreshLocation(MainActivity activity);

    /**
     * Get the currently stored location
     * @return user's current location
     */
    public Location getLocation();

    /**
     * Log the logged in user out of Taskr and FB
     */
    public void logout();

    /**
     * Log the user into Taskr. They are already logged into FB
     * @param name user's name (from FB)
     * @param email user's email (from FB)
     * @param fbid user's Facebook ID
     * @param callback callback for response
     */
    public void login(final String name, final String email, final String fbid,
                      final ApiCallback<LoginResult> callback);

    /**
     * Get the profile of a user
     * @param uid user's ID
     * @param callback callback with response
     */
    public void getUserProfile(final int uid, final ApiCallback<Profile> callback);

    /**
     * Get all open nearby requests
     * @param latitude
     * @param longitude
     * @param radius in miles
     * @param callback
     */
    public void getNearbyRequests(double latitude, double longitude, double radius,
                                  final ApiCallback<ArrayList<Request>> callback);

    /**
     * Get the requests associated with a user
     * @param uid user's ID
     * @param callback
     */
    public void getUserRequests(int uid,
                                  final ApiCallback<ArrayList<Request>> callback);

    /**
     * Accept a request
     * @param requestId
     * @param callback
     */
    public void acceptRequest(final int requestId, final ApiCallback<Boolean> callback);

    /**
     * Complete a request
     * @param requestId
     * @param callback
     */
    public void completeRequest(final int requestId, final ApiCallback<Boolean> callback);

    /**
     * Cancel a request
     * @param requestId
     * @param callback
     */
    public void cancelRequest(final int requestId, final ApiCallback<Boolean> callback);

    /**
     * Pay a request
     * @param requestId
     * @param callback
     */
    public void payRequest(final int requestId, final ApiCallback<Boolean> callback);

    /**
     * Get all of the reviews a user has received
     * @param revieweeID user ID
     * @param callback
     */
    public void getUserReviews(final int revieweeID, final ApiCallback<ArrayList<Review>> callback);

    /**
     * Rate a request that has been completed
     * @param requestID
     * @param rating integer [1,5]
     * @param callback
     */
    public void rateCompletedRequest(final int requestID, final int rating,
                                     final ApiCallback<Boolean> callback);

    /**
     * Upload a request to the server
     * @param request
     * @param callback
     */
    public void createRequest(Request request, final ApiCallback<RequestResult> callback);

    /**
     * Update an existing request on the server
     * @param request
     * @param callback
     */
    public void editRequest(Request request, final ApiCallback<Void> callback);
}
