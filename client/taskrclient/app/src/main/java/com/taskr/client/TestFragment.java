package com.taskr.client;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";
    public static final int DEFAULT_RADIUS = 100000;

    @BindString(R.string.app_name) String mTitle;
    @BindView(R.id.logged_in_as) TextView loggedInAs;
    @BindView(R.id.profile_id_result) TextView profileJSON;
    @BindView(R.id.nearby_requests_result) TextView requestsJSON;
    @BindView(R.id.profile_id_field) EditText profileId;
    @BindView(R.id.profile_find) Button profileFind;
    @BindView(R.id.nearby_find) Button nearbyFind;

    public TestFragment() {
        // Required for fragment subclass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle(mTitle);

        profileFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findUserById(view);
            }
        });

        nearbyFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNearbyRequests(view);
            }
        });

        loggedInAs.setText("Logged in as user with id: " + Api.getInstance().getId());


        return rootView;
    }


    public void findUserById(View view) {
        int userId = Integer.parseInt(profileId.getText().toString());

        Api.getInstance().getUserProfile(userId, new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                String profileString = "Name: " + profile.name + "\n" +
                        "ID: " + profile.id + "\n" +
                        "Wallet: " + profile.wallet;
                profileJSON.setText(profileString);
            }

            @Override
            public void onFailure(String message) {
                ((MainActivity)getActivity()).showErrorDialog(getString(R.string.profile_error),
                        message);
            }
        });
    }

    public void findNearbyRequests(View view){
        ((MainActivity)getActivity()).showFragment(new RequestsFragment(), true);
    }
}
