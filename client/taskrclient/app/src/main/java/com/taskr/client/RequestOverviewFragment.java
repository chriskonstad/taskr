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

    @BindView(R.id.request_title) TextView requestTitle;
    @BindView(R.id.request_amount) TextView requestAmount;
    @BindView(R.id.request_description) TextView requestDescription;

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

        return rootView;
    }
}
