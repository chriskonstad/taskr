package com.taskr.client;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.taskr.api.Api;
import com.taskr.api.Request;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 11/2/16.
 */

public class RequestAdapter extends ArrayAdapter<Request> {
    private static final String TAG = "RequestAdapter";
    private ArrayList<Request> mRequests;
    private Context mContext;
    private Api mApi;

    @BindView(R.id.title) TextView title;
    @BindView(R.id.due) TextView due;
    @BindView(R.id.distance) TextView distance;
    @BindView(R.id.amount) TextView amount;
    @BindView(R.id.status) TextView status;

    public RequestAdapter(Api api, Context context, ArrayList<Request> requests) {
        super(context, -1, requests);
        mApi = api;
        this.mContext = context;
        this.mRequests = requests;
    }


}
