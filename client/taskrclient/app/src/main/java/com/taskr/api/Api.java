package com.taskr.api;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

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
    private static int mId;
    private static String mName;
    private static String mEmail;
    private static Location mLocation;
    private static final int MAX_RETRIES = 0;   // YOLO, we can change this if needed later
    private static final int RETRY_DELAY_MS = 0;
    private static final int NO_CONNECTION = 0; // "HTTP status code" for unable to reach server

    private static class Endpoints {
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
        public static final String ACCEPT_REQUEST = "/api/v1/requests/accept";
        public static final String USER_REQUESTS = "/api/v1/requests/findByUid";
        public static final String RATE_REQUEST = "/api/v1/review/create";
        public static final String USER_REVIEWS = "/api/v1/review/show";
    }

    private static class Types {
        public static final Type REQUEST_LIST = new TypeToken<ArrayList<Request>>() {}.getType();
        public static final Type REVIEW_LIST = new TypeToken<ArrayList<Review>>() {}.getType();
    }

    private Api(Context context) {
        mContext = context;
        mClient.setMaxRetriesAndTimeout(MAX_RETRIES, RETRY_DELAY_MS);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC FACING API INFORMATION
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Api getInstance(Context mContext) {
        if(null == mApi) {
            synchronized (Api.class) {
                if(null == mApi) {
                    mApi = new Api(mContext);
                }
            }
        }
        return mApi;
    }

    public interface ApiCallback<T> {
        public void onSuccess(T returnValue);
        // TODO: What is the best datatype to return on failure?
        public void onFailure(String message);
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void refreshLocation(MainActivity activity) {
        try {
            mLocation = LocationProvider.getInstance().getLastLocation();
        } catch (Exception e) {
            activity.showErrorDialog(activity.getString(R.string.location_error_title),
                    "Unable to get current location");
        }
    }

    public Location getLocation() {
        return mLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC FACING API CALLS
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void login(final String name, final String email,
                      final ApiCallback<LoginResult> callback) {
        final String url = Endpoints.get(Endpoints.LOGIN);
        RequestParams params = new RequestParams();
        params.add("name", name);
        params.add("email", email);

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                LoginResult result = mGson.fromJson(json, LoginResult.class);

                mId = result.id;
                mName = name;
                mEmail = email;

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

    public void getUserProfile(final int uid, final ApiCallback<Profile> callback) {
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

    public void getNearbyRequests(double latitude, double longitude, double radius,
                                  final ApiCallback<ArrayList<Request>> callback) {
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

    public void getUserRequests(int uid,
                                  final ApiCallback<ArrayList<Request>> callback) {
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

    public void acceptRequest(final int requestId, final int uid, final ApiCallback<Boolean> callback) {
        final String url = Endpoints.get(Endpoints.ACCEPT_REQUEST);
//        RequestParams params = new RequestParams();

        JSONObject p = new JSONObject();
        JSONObject a = new JSONObject();
        JSONObject params = new JSONObject();

        try {
            a.put("user_id", Integer.toString(uid));
            p.put("id", Integer.toString(requestId));

            params.put("params", p);
            params.put("auth", a);
        }catch(JSONException e){
            //to do error handling
        }

        try{
            StringEntity entity = new StringEntity(params.toString());

            AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                                "): Unable to accept request: " +
                                requestId);
                    }
                }
            };

            mClient.post(mContext, url, entity, "application/json", handler);
        }catch(UnsupportedEncodingException e){
            //to do more error handling...
        }
    }


    // get all of the reviews that a user has received
    // revieweeID is the id of the person the review is for
    public void getUserReviews(final int revieweeID, final ApiCallback<ArrayList<Review>> callback){
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

    // rate a request that has been completed
    public void rateCompletedRequest(final int requestID, final int reviewerID, final int revieweeID,
                                     final int rating, final ApiCallback<ReviewResult> callback) {
        final String url = Endpoints.get(Endpoints.RATE_REQUEST);
        RequestParams params = new RequestParams();
        params.add("reviewer_id", Integer.toString(reviewerID));
        params.add("reviewee_id", Integer.toString(revieweeID));
        params.add("request_id", Integer.toString(requestID));
        params.add("rating", Integer.toString(rating));

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                ReviewResult result = mGson.fromJson(json, ReviewResult.class);
                Log.i(TAG, "Successfully created review with id: " + result.id);
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
                            "): Unable rate request: " +
                            requestID);
                }
            }
        };

        mClient.post(url, params, handler);
    }
}
