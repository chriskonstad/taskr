package com.taskr.api;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.taskr.client.LocationProvider;
import com.taskr.client.MainActivity;
import com.taskr.client.R;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TestApi extends Api {
    private static final String TAG = "TestApi";

    public Profile profile = new Profile();
    public String email = "chriskon149@gmail.com";

    public TestApi(Context baseApplicationContext) {
        super(baseApplicationContext);
        profile.id = 1;
        profile.name = "Chris Konstad (TEST)";
        profile.wallet = 0.0;
        profile.avgRating = 4.5;
        profile.fbid = "867392256730734";   // Chris's FBID
    }

    @Override
    public void refreshLocation(MainActivity activity) {
        // TODO Mock out
    }

    @Override
    public void logoutOfThirdParty() {
        // NOTHING TO DO HERE :)
    }

    @Override
    public void loginHandler(final String name, final String email, final String fbid,
                      final ApiCallback<LoginResult> callback) {
        if(name.equals(profile.name) &&
                email.equals(this.email) &&
                fbid.equals(profile.fbid)) {
            LoginResult result = new LoginResult();
            result.id = profile.id;
            callback.onSuccess(result);
        } else {
            callback.onFailure("Does not match hardcoded test user");
        }
    }

    @Override
    public void getUserProfileHandler(final int uid, final ApiCallback<Profile> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void getNearbyRequestsHandler(double latitude, double longitude, double radius,
                                  final ApiCallback<ArrayList<Request>> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void getUserRequestsHandler(int uid,
                                  final ApiCallback<ArrayList<Request>> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void acceptRequestHandler(final int requestId, final ApiCallback<Boolean> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void completeRequestHandler(final int requestId, final ApiCallback<Boolean> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void cancelRequestHandler(final int requestId, final ApiCallback<Boolean> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void payRequestHandler(final int requestId, final ApiCallback<Boolean> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void getUserReviewsHandler(final int revieweeID, final ApiCallback<ArrayList<Review>> callback){
        // TODO MOCK OUT
    }

    @Override
    public void rateCompletedRequestHandler(final int requestID, final int rating, final ApiCallback<Boolean> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void createRequestHandler(Request request, final ApiCallback<RequestResult> callback) {
        // TODO MOCK OUT
    }

    @Override
    public void editRequestHandler(Request request, final ApiCallback<Void> callback) {
        // TODO MOCK OUT
    }
}
