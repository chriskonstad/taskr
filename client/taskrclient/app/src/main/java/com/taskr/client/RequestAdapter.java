package com.taskr.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

    @BindView(R.id.title) TextView title;
    @BindView(R.id.due) TextView due;

    public RequestAdapter(Context context, ArrayList<Request> requests) {
        super(context, -1, requests);
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
        Format formatter = new SimpleDateFormat(Request.DUE_FORMAT);
        due.setText("Due: " + formatter.format(r.due));

        return rowView;
    }
}
