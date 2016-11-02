package com.taskr.client;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.taskr.api.Api;
import com.taskr.api.Profile;
import com.taskr.api.Request;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guillaumelam34 on 10/29/2016.
 */

public class RequestOverviewFragment extends Fragment {
    private static final String TAG = "RequestOverviewFragment";
    private Request req;

    @BindView(R.id.request_title) TextView requestTitle;
    @BindView(R.id.request_amount) TextView requestAmount;
    @BindView(R.id.request_description) TextView requestDescription;
    @BindView(R.id.request_user_name) TextView requestUserName;
    @BindView(R.id.request_rating) TextView requestRating;
    @BindView(R.id.distance) TextView requestDistance;
    @BindView(R.id.due) TextView requestDue;

    public RequestOverviewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.request_overview, container, false);
        ButterKnife.bind(this, rootView);

        req = (Request)getArguments().getSerializable("request");

        getActivity().setTitle(getString(R.string.request_overview_title));

        requestTitle.setText(req.title);
        requestAmount.setText(Double.toString(req.amount) + " Tokens");
        requestDescription.setText("Description: " + req.description);

        // Show the distance from the user's current location to the request
        float distance = req.getDistance(Api.getInstance(getContext()).getLocation());
        requestDistance.setText(String.format("%.1fmi", distance));
        requestDue.setText(req.getDue());

        Api.getInstance(getContext()).getUserProfile(req.user_id, new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile returnValue) {
                requestUserName.setText(returnValue.name);
                requestRating.setText(String.format("%.1f", returnValue.avgRating));
            }

            @Override
            public void onFailure(String message) {
                ((MainActivity)getActivity()).showErrorDialog(getString(R.string.connection_error),
                        "Unable to load user profile information for user with id: " +
                                Integer.toString(req.user_id));
            }
        });

        return rootView;
    }
}
