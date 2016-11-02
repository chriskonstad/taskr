package com.taskr.client;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.taskr.api.Request;

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

//    @BindView(R.id.request_id) TextView requestId;
    @BindView(R.id.request_title) TextView requestTitle;
    @BindView(R.id.request_amount) TextView requestAmount;
//    @BindView(R.id.request_lat) TextView requestLat;
//    @BindView(R.id.request_long) TextView requestLong;
//    @BindView(R.id.request_due) TextView requestDue;
    @BindView(R.id.request_description) TextView requestDescription;
//
    public RequestOverviewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.request_overview, container, false);
        ButterKnife.bind(this, rootView);

        req = (Request)getArguments().getSerializable("request");

//        getActivity().setTitle(req.title);
        getActivity().setTitle("");

        //requestId.setText(Integer.toString(req.id));
        requestTitle.setText(req.title);
        requestAmount.setText(Double.toString(req.amount) + " Tokens");
//        requestLat.setText("Latitude: " + Double.toString(req.lat));
//        requestLong.setText("Longitude: " + Double.toString(req.longitude));
        requestDescription.setText("Description: " + req.description);

//        DatetimeFormatter
//        requestDue.setText(Date.toString);

        return rootView;
    }
}
