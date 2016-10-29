package com.taskr.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    CallbackManager mCallbackManager;
    AccessTokenTracker mAccessTokenTracker;
    AccessToken mAccessToken;

    @BindString(R.string.app_name) String mTitle;
    @BindView(R.id.login_button) LoginButton loginButton;

    public LoginFragment() {
        // Required for fragment subclass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle(mTitle);
        setHasOptionsMenu(true);

        loginButton.setReadPermissions(EMAIL);
        loginButton.setFragment(this);

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "Successful login");
                // This is for the first time login only, not called when authorized after having
                // already logged in before
                // TODO Handle login here? But it already happens in updateWithToken??
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Canceled login");
                // TODO Handle cancel here
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Error during login");
                // TODO Handle error here
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWithToken(AccessToken.getCurrentAccessToken());
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                updateWithToken(currentAccessToken);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hide the overflow menu because we don't want that in settings
        for(int i=0; i<menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if(null != currentAccessToken && null != getActivity()) {
            mAccessToken = currentAccessToken;
            onAuthenticated();
        }
    }

    private void onAuthenticated() {
        // Get user information from FB

        GraphRequest request = GraphRequest.newMeRequest(mAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.i(TAG, "Got user information from FB");
                Bundle fbData = readFbData(object);

                String id = fbData.getString(ID);
                String name = fbData.getString(NAME);
                String email = fbData.getString(EMAIL);
                Log.i(TAG, "Id: " + id);
                Log.i(TAG, "Name: " + name);
                Log.i(TAG, "Email: " + email);

                // TODO Use this data to call the account login/creation endpoint,
                // TODO Use the response of account login/creation endpoint to show main fragment
                // TODO Store the Taskr User ID returned from login in the API singleton

                // Launch the rest of the app
                ((MainActivity)getActivity()).showFragment(new TestFragment(), false);
            }
        });

        Bundle params = new Bundle();
        params.putString("fields", ID + ", " + NAME + ", " + EMAIL);
        request.setParameters(params);
        // TODO Launch a loading alert dialog, and dismiss it when done
        request.executeAsync();
    }

    private Bundle readFbData(JSONObject object) {
        String[] fields = {ID, NAME, EMAIL};

        Bundle bundle = new Bundle();
        try {
            for (String f : fields) {
                bundle.putString(f, object.getString(f));
            }
        }
        catch (Exception e) {
            Log.wtf(TAG, e.getMessage());
        }

        return bundle;
    }
}
