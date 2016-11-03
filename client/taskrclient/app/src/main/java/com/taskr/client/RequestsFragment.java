package com.taskr.client;

import android.location.Location;
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
import com.taskr.api.Api;
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
    private ArrayAdapter<Request> adapter;
    private static final int DEFAULT_RADIUS = 100000;   // in miles

    private boolean specificUserFlag = false;

    @BindString(R.string.nearby_requests) String mTitle;

    public RequestsFragment(){

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Request req = adapter.getItem(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable("request", req);

        RequestOverviewFragment overviewFrag = new RequestOverviewFragment();
        overviewFrag.setArguments(bundle);

        ((MainActivity)getActivity()).showFragment(overviewFrag, true);
    }

    private void loadNearbyRequests() {
        Api.getInstance(getContext()).refreshLocation((MainActivity)getActivity());
        Location lastLocation = Api.getInstance(getContext()).getLocation();

        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();
        double radius = DEFAULT_RADIUS; // TODO: store/get from settings?

        Api.getInstance(getActivity()).getNearbyRequests(latitude, longitude, radius,
                new Api.ApiCallback<ArrayList<Request>>() {
                    @Override
                    public void onSuccess(ArrayList<Request> requests) {
                        adapter = new RequestAdapter(getContext(), requests);
                        setListAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String message) {
                        ((MainActivity)getActivity())
                                .showErrorDialog(getString(R.string.connection_error),
                                        "Unable to load nearby requests");
                    }
                });
    }

    private void loadUserRequests(){
        Api.getInstance(getContext()).refreshLocation((MainActivity)getActivity());
        
        Api.getInstance(getActivity()).getUserRequests(Api.getInstance(getContext()).getId(),
                new Api.ApiCallback<ArrayList<Request>>() {
                    @Override
                    public void onSuccess(ArrayList<Request> requests) {
                        adapter = new RequestAdapter(getContext(), requests);
                        setListAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String message) {
                        ((MainActivity)getActivity())
                                .showErrorDialog(getString(R.string.connection_error),
                                        "Unable to load user requests");
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.requests_list, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(getContext().getString(R.string.is_specific_user))){
            specificUserFlag = true;
        }

        getActivity().setTitle(mTitle);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(specificUserFlag) {
            loadUserRequests();
        }
        else{
            loadNearbyRequests();
        }
    }
}