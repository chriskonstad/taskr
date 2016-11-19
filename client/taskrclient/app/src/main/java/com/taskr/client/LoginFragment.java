package com.taskr.client;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.taskr.api.Api;

import org.json.JSONObject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

public class LoginFragment extends Fragment {
    private String TAG;
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String ID = "id";
    CallbackManager mCallbackManager;
    AccessTokenTracker mAccessTokenTracker;
    AccessToken mAccessToken;
    Api mApi;

    @BindString(R.string.app_name) String mTitle;
    @BindString(R.string.default_hostname) String DEFAULT_HOSTNAME;
    @BindView(R.id.login_button) LoginButton loginButton;
    @BindView(R.id.hostname) TextView hostnameView;

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

        TAG = getString(R.string.login_fragment_tag);
        mApi = ((MainActivity)getActivity()).api();

        loginButton.setReadPermissions(EMAIL);
        loginButton.setFragment(this);
        loginButton.setBackgroundResource(R.drawable.rounded_button);

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "Successful first login");
                // This is for the first time login only, not called when authorized after having
                // already logged in before
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Canceled login");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Error during login");
            }
        });

        // Display the set server hostname IF it is not set to the production server
        String hostname = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getString(R.string.key_hostname), DEFAULT_HOSTNAME);
        if(!hostname.equals(DEFAULT_HOSTNAME)) {
            hostnameView.setText(hostname);
        }

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

    /**
     * Handle return results from started activities
     * @param requestCode code of the request that started the activity
     * @param resultCode the result code from the started activity
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hide the overflow menu (except settings for now) because we don't want that in settings
        for(int i=0; i<menu.size(); i++) {
            if(menu.getItem(i).getItemId() != R.id.action_settings) {
                menu.getItem(i).setVisible(false);
            }
        }
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if(null != currentAccessToken && null != getActivity()) {
            mAccessToken = currentAccessToken;
            onAuthenticatedWithFb();
        }
    }

    /**
     * Call this when we know we're authenticated with FB.  This logs the user in with Taksr.
     */
    private void onAuthenticatedWithFb() {
        GraphRequest request = GraphRequest.newMeRequest(mAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.i(TAG, "Got user information from FB");
                Bundle fbData = readFbData(object);
                Log.i(TAG, object.toString());

                final String name = fbData.getString(NAME);
                final String email = fbData.getString(EMAIL);
                final String id = fbData.getString(ID);
                Log.i(TAG, "Name: " + name);
                Log.i(TAG, "Email: " + email);
                Log.i(TAG, "Id: " + id);

                final ProgressDialog dialog;
                if(null != getActivity()) {
                    dialog = ((MainActivity)getActivity())
                            .showProgressDialog(getString(R.string.logging_into_taskr));
                    dialog.show();
                } else {
                    dialog = null;
                }

                mApi.login(name, email, id,
                        new Api.ApiCallback<com.taskr.api.LoginResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.LoginResult returnValue) {
                        if(null != dialog) {
                            dialog.dismiss();
                        }
                        onLoggedIn();
                    }

                    @Override
                    public void onFailure(String message) {
                        // Logout of FB to force the user to re-login
                        if(null != dialog) {
                            dialog.dismiss();
                        }
                        mApi.logout();
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.login_error_title))
                                .setMessage(message)
                                .setIcon(R.drawable.alert_circle)
                                .setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            }
        });

        Bundle params = new Bundle();
        params.putString("fields", NAME + ", " + EMAIL);
        request.setParameters(params);
        request.executeAsync();
    }

    /**
     * Only call this when fully authenticated with the Taskr server, which happens after
     * authenticating with FB
     */
    private void onLoggedIn() {
        // Launch the rest of the app
        if(null != getActivity()) {
            ((MainActivity)getActivity()).onLogin();
        } else {
            Log.w(TAG, "Unable to get mainactivity.");
        }
    }

    /**
     * Read a JSONObject into a bundle
     * @param object data from fb auth
     * @return bundle with FB auth data
     */
    private Bundle readFbData(JSONObject object) {
        String[] fields = {NAME, EMAIL, ID};

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
