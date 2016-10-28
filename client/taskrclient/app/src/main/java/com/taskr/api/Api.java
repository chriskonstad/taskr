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
    private static final int MAX_RETRIES = 2;
    private static final int RETRY_DELAY_MS = 500;

    private static class Endpoints {
        public static String get(String endpoint) {
            // Load stored hostname from settings
            String base = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString(mContext.getString(R.string.key_hostname), "");

            String url = "http://" + base + endpoint;
            Log.i(TAG, "Generated endpoint: " + url);
            return url;
        }

        public static final String PROFILE = "/api/v1/profile";
        public static final String NEARBY = "/api/v1/requests/nearby";
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC FACING API CALLS
    ////////////////////////////////////////////////////////////////////////////////////////////////
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
                callback.onFailure("Error (" +
                        statusCode +
                        "): Unable to get user profile for uid: " +
                        uid);
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
                callback.onFailure("Error (" + statusCode + "): Unable to get nearby requests");
            }
        };

        mClient.get(url, params, handler);
    }
}
