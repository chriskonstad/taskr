package com.taskr.api;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.taskr.client.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Api {
    private static final String TAG = "Api";
    private static Api mApi;
    private static Context mContext;
    private static AsyncHttpClient mClient = new AsyncHttpClient();
    private static Gson mGson = new Gson();
    private static int mId;
    private static final int MAX_RETRIES = 0;   // YOLO, we can change this if needed later
    private static final int RETRY_DELAY_MS = 0;
    private static final int NO_CONNECTION = 0; // "HTTP status code" for unable to reach server

    private static class Endpoints {
        public static String get(String endpoint) {
            // Load stored hostname from settings
            String base = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString(mContext.getString(R.string.key_hostname), "");

            String url = "http://" + base + endpoint;
            Log.i(TAG, "Generated endpoint: " + url);
            return url;
        }

        public static final String LOGIN = "/api/v1/login";
        public static final String NEARBY = "/api/v1/requests/nearby";
        public static final String PROFILE = "/api/v1/profile";
    }

    private static class Types {
        public static final Type REQUEST_LIST = new TypeToken<ArrayList<Request>>() {}.getType();
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
        params.add("long", Double.toString(longitude));
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
}
