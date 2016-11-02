package com.taskr.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taskr.api.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by guillaumelam34 on 10/29/2016.
 */

public class RequestsFragment extends ListFragment {
    private static final String TAG = "RequestsFragment";
    private ArrayList<Request> nearbyRequests;

    @BindString(R.string.nearby_requests) String mTitle;

    public RequestsFragment(){

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Request req = nearbyRequests.get(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable("request", req);

        RequestOverviewFragment overviewFrag = new RequestOverviewFragment();
        overviewFrag.setArguments(bundle);

        ((MainActivity)getActivity()).showFragment(overviewFrag, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.requests_list, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle(mTitle);

        ArrayList<Integer> request_ids = new ArrayList<Integer>();
        ArrayList<String> request_text = new ArrayList<String>();

        nearbyRequests = (ArrayList<Request>)getArguments().getSerializable("requests");
        if(!nearbyRequests.isEmpty()){
            for(int x = 0; x < nearbyRequests.size(); x++){
                Request req = nearbyRequests.get(x);
                request_ids.add(req.id);
                request_text.add(req.title);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),R.layout.request_single, request_text);
        setListAdapter(adapter);

        return rootView;
    }
}
