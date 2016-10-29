package com.taskr.client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.taskr.api.Api;
import com.taskr.api.Profile;
import com.taskr.api.Request;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    CallbackManager mCallbackManager;
    AccessTokenTracker mAccessTokenTracker;

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

        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "Successful login");
                // TODO Handle login here? But it already happens in updateWithToken??
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Canceled login");
                // TODO Handle cancel here
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "Error during login");
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
            ((MainActivity)getActivity()).showFragment(new TestFragment(), false);
        }
    }
}
