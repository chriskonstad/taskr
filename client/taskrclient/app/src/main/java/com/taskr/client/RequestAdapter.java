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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.request, parent, false);
        ButterKnife.bind(this, rowView);

        Request r = mRequests.get(position);

        title.setText(r.title);
        due.setText(r.getDue());

        // Show the distance from the user's current location to the request
        float d = r.getDistance(mApi.getLocation());
        distance.setText(String.format("%.1fmi", d));

        amount.setText(String.format("$%.2f", r.amount));
        status.setText(r.status);
        status.setTextColor(ContextCompat.getColor(getContext(), Request.Status.getColor(r.status)));

        return rowView;
    }
}
