package com.example.guillaumelam34.taskrclient;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Api {
    private static final String TAG = "Api";
    private static Api mApi;
    private static Context mContext;
    private static AsyncHttpClient mClient = new AsyncHttpClient();
    private static final int MAX_RETRIES = 2;
    private static final int RETRY_DELAY_MS = 500;

    private class Endpoints {
        public static final String profile = "/api/v1/profile";
        public static final String nearby = "/api/v1/requests/nearby";
    }

    private Api(Context context) {
        mContext = context;
        mClient.setMaxRetriesAndTimeout(MAX_RETRIES, RETRY_DELAY_MS);
    }

    // Public facing Api
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

    // Public facing Api objects
    public class Profile {
        public int id;
        public String name;
        public double wallet;

        // TODO add more of the data fields
    }

    public class Request {
        public int id;
        public String title;
        public double amount;

        // TODO add more of the data fields
    }

    public void getUserProfile(final int uid, final ApiCallback<Profile> callback) {
        final String url = getEndpoint(Endpoints.profile) + "/" + uid;
        RequestParams params = new RequestParams();

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                Profile profile = new Profile();
                try {
                    JSONObject jProfile = new JSONObject(json);
                    profile.id = jProfile.getInt("id");
                    profile.name = jProfile.getString("name");
                    profile.wallet = jProfile.getDouble("wallet");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

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
        final String url = getEndpoint(Endpoints.nearby);
        RequestParams params = new RequestParams();
        params.add("lat", Double.toString(latitude));
        params.add("long", Double.toString(longitude));
        params.add("radius", Double.toString(radius));

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                ArrayList<Request> requests = new ArrayList<>();

                try {
                    JSONArray jRequests = new JSONArray(json);
                    for(int i=0; i<jRequests.length(); i++) {
                        JSONObject jReq = jRequests.getJSONObject(i);
                        Request req = new Request();
                        req.id = jReq.getInt("id");
                        req.title = jReq.getString("title");
                        req.amount = jReq.getDouble("amount");
                        requests.add(req);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

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


    // PRIVATE HELPER FUNCTIONS
    private String getEndpoint(String endpoint) {
        // Load stored hostname from settings
        String base = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(mContext.getString(R.string.key_hostname), "");

        String url = "http://" + base + endpoint;
        Log.i(TAG, "Generated endpoint: " + url);
        return url;
    }

}
