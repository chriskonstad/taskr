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

public abstract class Api {
    private static final String TAG = "Api";
    protected final Context mContext;
    private int mId = -1;
    private String mName = null;
    private String mEmail = null;
    private String mFbid = null;
    protected Location mLocation;

    /**
     * Initialize the API with the given context
     * <p>
     * The context is stored for the life of the API, or until init is called again
     * </p>
     * @param baseApplicationContext context to init the API with
     */
    public Api(Context baseApplicationContext) {
        assert(null != baseApplicationContext);
        mContext = baseApplicationContext;
    }

    /**
     * Exception for authentication errors.
     * Mostly thrown when there is an API call requiring authentication but the API instance is not
     * authenticated against Taskr's server
     */
    public class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    private void checkAuthenticated() {
        if(null == mFbid) {
            throw new AuthenticationException("No FBID associated with session");
        } else if(null == mName) {
            throw new AuthenticationException("No name associated with session");
        } else if (null == mEmail) {
            throw new AuthenticationException("No email associated with session");
        }
    }

    /**
     * Make sure the API is ready to make authenticated requests
     */
    public final boolean checkReady() {
        // Throw exceptions if not ready, otherwise return true
        checkAuthenticated();
        return true;
    }

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
    public final int getId() {
        checkReady();
        return mId;
    }

    /**
     * Get the name of the logged in user
     * @return user's name
     */
    public final String getName() {
        checkReady();
        return mName;
    }

    /**
     * get the email of the logged in user
     * @return user's email
     */
    public final String getEmail() {
        checkReady();
        return mEmail;
    }

    /**
     * Get the Facebook ID of the logged in user
     * @return user's Facebook ID
     */
    public final String getFbid() {
        checkReady();
        return mFbid;
    }

    /**
     * Force the API to refresh the user's current location
     * @param activity used for showing error dialog
     */
    abstract public void refreshLocation(MainActivity activity);

    /**
     * Get the currently stored location
     * @return user's current location
     */
    public final Location getLocation() {
        return mLocation;
    }

    /**
     * Log the logged in user out of third party services
     */
    abstract protected void logoutOfThirdParty();

    /**
     * Log the logged in user out of Taskr and third party services
     */
    public final void logout() {
        logoutOfThirdParty();
        mId = -1;
        mName = null;
        mEmail = null;
        mFbid = null;
    }

    /**
     * Log the user into Taskr. They are already logged into FB
     * @param name user's name (from FB)
     * @param email user's email (from FB)
     * @param fbid user's Facebook ID
     * @param callback callback for response
     */
    public final void login(final String name, final String email, final String fbid,
                      final ApiCallback<LoginResult> callback) {
        loginHandler(name, email, fbid, new ApiCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult returnValue) {
                mId = returnValue.id;
                mName = name;
                mEmail = email;
                mFbid = fbid;
                callback.onSuccess(returnValue);
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }
        });
    }
    abstract protected void loginHandler(final String name, final String email, final String fbid,
                                         final ApiCallback<LoginResult> callback);

    /**
     * Get the profile of a user
     * @param uid user's ID
     * @param callback callback with response
     */
    public final void getUserProfile(final int uid, final ApiCallback<Profile> callback) {
        checkReady();
        getUserProfileHandler(uid, callback);
    }
    abstract protected void getUserProfileHandler(final int uid, final ApiCallback<Profile> callback);

    /**
     * Get all open nearby requests
     * @param latitude
     * @param longitude
     * @param radius in miles
     * @param callback
     */
    public final void getNearbyRequests(double latitude, double longitude, double radius,
                                  final ApiCallback<ArrayList<Request>> callback) {
        checkReady();
        getNearbyRequestsHandler(latitude, longitude, radius, callback);
    }
    abstract protected void getNearbyRequestsHandler(double latitude, double longitude, double radius,
                                                     final ApiCallback<ArrayList<Request>> callback);

    /**
     * Get the requests associated with a user
     * @param uid user's ID
     * @param callback
     */
    public final void getUserRequests(int uid,
                                  final ApiCallback<ArrayList<Request>> callback) {
        checkReady();
        getUserRequestsHandler(uid, callback);
    }
    abstract protected void getUserRequestsHandler(int uid,
                                                   final ApiCallback<ArrayList<Request>> callback);

    /**
     * Accept a request
     * @param requestId
     * @param callback
     */
    public final void acceptRequest(final int requestId, final ApiCallback<Boolean> callback) {
        checkReady();
        acceptRequestHandler(requestId, callback);
    }
    abstract protected void acceptRequestHandler(final int requestId,
                                                 final ApiCallback<Boolean> callback);

    /**
     * Complete a request
     * @param requestId
     * @param callback
     */
    public final void completeRequest(final int requestId, final ApiCallback<Boolean> callback) {
        checkReady();
        completeRequestHandler(requestId, callback);
    }
    abstract protected void completeRequestHandler(final int requestId, final ApiCallback<Boolean> callback);

    /**
     * Cancel a request
     * @param requestId
     * @param callback
     */
    public final void cancelRequest(final int requestId, final ApiCallback<Boolean> callback) {
        checkReady();
        cancelRequestHandler(requestId, callback);
    }
    abstract protected void cancelRequestHandler(final int requestId, final ApiCallback<Boolean> callback);

    /**
     * Pay a request
     * @param requestId
     * @param callback
     */
    public final void payRequest(final int requestId, final ApiCallback<Boolean> callback) {
        checkReady();
        payRequestHandler(requestId, callback);
    }
    abstract protected void payRequestHandler(final int requestId, final ApiCallback<Boolean> callback);

    /**
     * Get all of the reviews a user has received
     * @param revieweeID user ID
     * @param callback
     */
    public final void getUserReviews(final int revieweeID, final ApiCallback<ArrayList<Review>> callback) {
        checkReady();
        getUserReviewsHandler(revieweeID, callback);
    }
    abstract protected void getUserReviewsHandler(final int revieweeID,
                                                  final ApiCallback<ArrayList<Review>> callback);

    /**
     * Rate a request that has been completed
     * @param review the review to send (rating [1,5])
     * @param callback
     */
    public final void rateCompletedRequest(final Review review,
                                     final ApiCallback<Boolean> callback) {
        checkReady();
        rateCompletedRequestHandler(review, callback);
    }
    abstract protected void rateCompletedRequestHandler(final Review review,
                                     final ApiCallback<Boolean> callback);

    /**
     * Upload a request to the server
     * @param request
     * @param callback
     */
    public final void createRequest(Request request, final ApiCallback<RequestResult> callback) {
        checkReady();
        createRequestHandler(request, callback);
    }
    abstract protected void createRequestHandler(Request request, final ApiCallback<RequestResult> callback);

    /**
     * Update an existing request on the server
     * @param request
     * @param callback
     */
    public final void editRequest(Request request, final ApiCallback<Void> callback) {
        checkReady();
        editRequestHandler(request, callback);
    }
    abstract protected void editRequestHandler(Request request, final ApiCallback<Void> callback);

    /**
     * Update a user's device id mapping on the server
     * @param deviceID  value GCM associates the user's device with currently
     * @param callback
     */
    public final void updateDevice(final String deviceID, final ApiCallback<Void> callback) {
        checkReady();
        updateDeviceHandler(deviceID, callback);
    }
    abstract protected void updateDeviceHandler(final String deviceID, final ApiCallback<Void> callback);
}
