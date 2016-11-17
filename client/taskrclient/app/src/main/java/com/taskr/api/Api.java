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

public class Api {
    private static final String TAG = "Api";
    private static Api mApi;
    private static Context mContext;
    private static AsyncHttpClient mClient = new AsyncHttpClient();
    private static Gson mGson = new Gson();
    private static int mId = -1;
    private static String mName;
    private static String mEmail;
    private static String mFbid;
    private static Location mLocation;
    private static final int MAX_RETRIES = 0;   // YOLO, we can change this if needed later
    private static final int RETRY_DELAY_MS = 0;
    private static final int NO_CONNECTION = 0; // "HTTP status code" for unable to reach server

    private static class Endpoints {
        /**
         * Generate the full URL of the given endpoint
         * @param endpoint endpoint to use for the URL
         * @return the full URL
         */
        public static String get(String endpoint) {
            // Load stored hostname from settings
            String base = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString(mContext.getString(R.string.key_hostname),
                            mContext.getString(R.string.default_hostname));

            String url = "http://" + base + endpoint;
            Log.i(TAG, "Generated endpoint: " + url);
            return url;
        }

        public static final String LOGIN = "/api/v1/login";
        public static final String NEARBY = "/api/v1/requests/nearby";
        public static final String PROFILE = "/api/v1/profile";
        public static final String CREATE_REQUEST = "/api/v1/requests";
        public static final String ACCEPT_REQUEST = "/api/v1/requests/accept";
        public static final String CANCEL_REQUEST = "/api/v1/requests/cancel";
        public static final String PAY_REQUEST = "/api/v1/requests/pay";
        public static final String EDIT_REQUEST = "/api/v1/requests";
        public static final String COMPLETE_REQUEST = "/api/v1/requests/complete";
        public static final String USER_REQUESTS = "/api/v1/requests/findByUid";
        public static final String RATE_REQUEST = "/api/v1/review/create";
        public static final String USER_REVIEWS = "/api/v1/review/show";
    }

    private static class Types {
        public static final Type REQUEST_LIST = new TypeToken<ArrayList<Request>>() {}.getType();
        public static final Type REVIEW_LIST = new TypeToken<ArrayList<Review>>() {}.getType();
    }

    private Api() {
        mClient.setMaxRetriesAndTimeout(MAX_RETRIES, RETRY_DELAY_MS);
    }

    private void checkInitialized() {
        if(null == mContext) {
            throw new NotInitializesException("No application context assigned");
        }
    }

    private void checkAuthenticated() {
        if(null == mFbid || null == mName || null == mEmail) {
            throw new AuthenticationException("No FBID associated with session");
        }
    }

    // Make sure the API is ready to make authenticated requests
    private void checkReady() {
        checkInitialized();
        checkAuthenticated();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC FACING API INFORMATION
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the API singleton
     * @return the API instance
     */
    public static Api getInstance() {
        if(null == mApi) {
            synchronized (Api.class) {
                if(null == mApi) {
                    mApi = new Api();
                }
            }
        }
        return mApi;
    }

    /**
     * Initialize the API with the given context
     * <p>
     * The context is stored for the life of the APi, or until init is called again
     * </p>
     * @param baseApplicationContext context to init the API with
     */
    public void init(Context baseApplicationContext) {
        mContext = baseApplicationContext;
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
     * Exception for authentication errors.
     * Mostly thrown when there is an API call requiring authentication but the API instance is not
     * authenticated against Taskr's server
     */
    public class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    /**
     * Exception for when the API is used before being initialized
     */
    public class NotInitializesException extends RuntimeException {
        public NotInitializesException(String message) {
            super(message);
        }
    }

    /**
     * Get the logged in Taskr user ID
     * @return API's taskr user id
     */
    public int getId() {
        checkReady();
        return mId;
    }

    /**
     * Get the name of the logged in user
     * @return user's name
     */
    public String getName() {
        checkReady();
        return mName;
    }

    /**
     * get the email of the logged in user
     * @return user's email
     */
    public String getEmail() {
        checkReady();
        return mEmail;
    }

    /**
     * Get the Facebook ID of the logged in user
     * @return user's Facebook ID
     */
    public String getFbid() {
        checkReady();
        return mFbid;
    }

    /**
     * Force the API to refresh the user's current location
     * @param activity used for showing error dialog
     */
    public void refreshLocation(MainActivity activity) {
        try {
            mLocation = LocationProvider.getInstance().getLastLocation();
        } catch (Exception e) {
            activity.showErrorDialog(activity.getString(R.string.location_error_title),
                    "Unable to get current location");
        }
    }

    /**
     * Get the currently stored location
     * @return user's current location
     */
    public Location getLocation() {
        return mLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC FACING API CALLS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Log the logged in user out of Taskr and FB
     */
    public void logout() {
        LoginManager.getInstance().logOut();
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
    public void login(final String name, final String email, final String fbid,
                      final ApiCallback<LoginResult> callback) {
        checkInitialized();
        final String url = Endpoints.get(Endpoints.LOGIN);
        RequestParams params = new RequestParams();
        params.add("name", name);
        params.add("email", email);
        params.add("fbid", fbid);

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                LoginResult result = mGson.fromJson(json, LoginResult.class);

                mId = result.id;
                mName = name;
                mEmail = email;
                mFbid = fbid;

                Log.i(TAG, "Logged in as user with id: " + mId);

                callback.onSuccess(result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                if(NO_CONNECTION == statusCode) {
                    callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                } else {
                    callback.onFailure("Error (" +
                            statusCode +
                            "): Unable to login for user with email: " +
                            email);
                }
            }
        };

        mClient.post(url, params, handler);
    }

    /**
     * Get the profile of a user
     * @param uid user's ID
     * @param callback callback with response
     */
    public void getUserProfile(final int uid, final ApiCallback<Profile> callback) {
        checkReady();
        final String url = Endpoints.get(Endpoints.PROFILE) + "/" + uid;
        RequestParams params = new RequestParams();

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                Profile profile = mGson.fromJson(json, Profile.class);

                callback.onSuccess(profile);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                if(NO_CONNECTION == statusCode) {
                    callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                } else {
                    callback.onFailure("Error (" +
                            statusCode +
                            "): Unable to get user profile for uid: " +
                            uid);
                }
            }
        };

        mClient.get(url, params, handler);
    }

    /**
     * Get all open nearby requests
     * @param latitude
     * @param longitude
     * @param radius in miles
     * @param callback
     */
    public void getNearbyRequests(double latitude, double longitude, double radius,
                                  final ApiCallback<ArrayList<Request>> callback) {
        checkReady();
        final String url = Endpoints.get(Endpoints.NEARBY);
        RequestParams params = new RequestParams();
        params.add("lat", Double.toString(latitude));
        params.add("longitude", Double.toString(longitude));
        params.add("radius", Double.toString(radius));

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                ArrayList<Request> requests = mGson.fromJson(json, Types.REQUEST_LIST);
                callback.onSuccess(requests);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                if(NO_CONNECTION == statusCode) {
                    callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                } else {
                    callback.onFailure("Error (" + statusCode + "): Unable to get nearby requests");
                }
            }
        };

        mClient.get(url, params, handler);
    }

    /**
     * Get the requests associated with a user
     * @param uid user's ID
     * @param callback
     */
    public void getUserRequests(int uid,
                                  final ApiCallback<ArrayList<Request>> callback) {
        checkReady();
        final String url = Endpoints.get(Endpoints.USER_REQUESTS);
        RequestParams params = new RequestParams();
        params.put("user_id", Integer.toString(uid));

        //Will need to eventually make this take a parameter to allow users to filter between requests
        //that they have picked up and requests that they have posted
        params.put("role", mContext.getString(R.string.role_any));

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                ArrayList<Request> requests = mGson.fromJson(json, Types.REQUEST_LIST);
                callback.onSuccess(requests);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                if(NO_CONNECTION == statusCode) {
                    callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                } else {
                    callback.onFailure("Error (" + statusCode + "): Unable to get user's requests");
                }
            }
        };

        mClient.get(url, params, handler);
    }

    /**
     * Act (accept, complete, cancel, pay, etc.) on a request
     * @param url URL of the action to perform
     * @param requestId
     * @param callback
     */
    private void actOnRequest(final String url, final int requestId,
                              final ApiCallback<Boolean> callback) {
        checkReady();

        JSONObject p = new JSONObject();
        JSONObject a = new JSONObject();
        JSONObject params = new JSONObject();

        try {
            a.put("user_id", Integer.toString(mId));
            p.put("id", Integer.toString(requestId));

            params.put("params", p);
            params.put("auth", a);

            StringEntity entity = new StringEntity(params.toString());

            AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(TAG, "Got status code: " + Integer.toString(statusCode));
                    callback.onSuccess(true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                      Throwable error) {
                    if(NO_CONNECTION == statusCode) {
                        callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                    } else {
                        callback.onFailure("Error (" +
                                statusCode +
                                "): Unable to act on request: " +
                                requestId);
                    }
                }
            };

            mClient.post(mContext, url, entity, "application/json", handler);
        }catch(Exception e){
            Log.e(TAG, "Unable to accept request: " + e.getMessage());
        }
    }

    /**
     * Accept a request
     * @param requestId
     * @param callback
     */
    public void acceptRequest(final int requestId, final ApiCallback<Boolean> callback) {
        final String url = Endpoints.get(Endpoints.ACCEPT_REQUEST);
        actOnRequest(url, requestId, callback);
    }

    /**
     * Complete a request
     * @param requestId
     * @param callback
     */
    public void completeRequest(final int requestId, final ApiCallback<Boolean> callback) {
        final String url = Endpoints.get(Endpoints.COMPLETE_REQUEST);
        actOnRequest(url, requestId, callback);
    }

    /**
     * Cancel a request
     * @param requestId
     * @param callback
     */
    public void cancelRequest(final int requestId, final ApiCallback<Boolean> callback) {
        final String url = Endpoints.get(Endpoints.CANCEL_REQUEST);
        actOnRequest(url, requestId, callback);
    }

    /**
     * Pay a request
     * @param requestId
     * @param callback
     */
    public void payRequest(final int requestId, final ApiCallback<Boolean> callback) {
        final String url = Endpoints.get(Endpoints.PAY_REQUEST);
        actOnRequest(url, requestId, callback);
    }

    /**
     * Get all of the reviews a user has received
     * @param revieweeID user ID
     * @param callback
     */
    public void getUserReviews(final int revieweeID, final ApiCallback<ArrayList<Review>> callback){
        checkReady();
        final String url = Endpoints.get(Endpoints.USER_REVIEWS);
        RequestParams params = new RequestParams();
        params.put("id", Integer.toString(revieweeID));


        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                ArrayList<Review> reviews = mGson.fromJson(json, Types.REVIEW_LIST);
                callback.onSuccess(reviews);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                if(NO_CONNECTION == statusCode) {
                    callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                } else {
                    callback.onFailure("Error (" + statusCode + "): Unable to get user's requests");
                }
            }
        };


        mClient.get(url, params, handler);
    }

    /**
     * Rate a request that has been completed
     * @param requestID
     * @param rating integer [1,5]
     * @param callback
     */
    public void rateCompletedRequest(final int requestID, final int rating, final ApiCallback<Boolean> callback) {
        checkReady();
        final String url = Endpoints.get(Endpoints.RATE_REQUEST);
        RequestParams params = new RequestParams();
        params.add("reviewer_id", Integer.toString(mId));
        params.add("reviewee_id", Integer.toString(-1));
        params.add("request_id", Integer.toString(requestID));
        params.add("rating", Integer.toString(rating));

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                Log.i(TAG, "Successfully created review.");
                callback.onSuccess(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                if(NO_CONNECTION == statusCode) {
                    callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                } else {
                    callback.onFailure("Error (" +
                            statusCode +
                            "): Unable to rate request id: " +
                            requestID + " and rating: " + rating);
                }
            }
        };

        mClient.post(url, params, handler);
    }

    /**
     * Upload a request to the server
     * @param request
     * @param callback
     */
    public void createRequest(Request request, final ApiCallback<RequestResult> callback) {
        checkReady();
        final String url = Endpoints.get(Endpoints.CREATE_REQUEST);

        try {
            String json = mGson.toJson(request);
            StringEntity entity = new StringEntity(json);

            AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String resultJson = new String(responseBody);
                    RequestResult result = mGson.fromJson(resultJson, RequestResult.class);
                    callback.onSuccess(result);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                      Throwable error) {
                    if(NO_CONNECTION == statusCode) {
                        callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                    } else {
                        callback.onFailure("Error (" + statusCode + "): Unable to create request");
                    }
                }
            };

            mClient.post(mContext, url, entity, "application/json", handler);
        }catch(Exception e){
            Log.e(TAG, "Unable to create request: " + e.getMessage());
        }
    }

    /**
     * Update an existing request on the server
     * @param request
     * @param callback
     */
    public void editRequest(Request request, final ApiCallback<Void> callback) {
        checkReady();
        final String url = Endpoints.get(Endpoints.EDIT_REQUEST) + "/" + request.id;

        JSONObject a = new JSONObject();
        JSONObject params = new JSONObject();

        try {
            a.put("user_id", Integer.toString(mId));
            params.put("auth", a);
            JSONObject json = new JSONObject(mGson.toJson(request, Request.class));
            params.put("request", json);
            StringEntity entity = new StringEntity(params.toString());

            AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    callback.onSuccess(null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                      Throwable error) {
                    if (NO_CONNECTION == statusCode) {
                        callback.onFailure(mContext.getString(R.string.unable_to_reach_server));
                    } else {
                        callback.onFailure("Error (" + statusCode + "): Unable to edit request: ");
                    }
                }
            };

            mClient.post(mContext, url, entity, "application/json", handler);
        } catch (Exception e) {
            Log.e(TAG, "Unable to accept request: " + e.getMessage());
        }
    }
}
