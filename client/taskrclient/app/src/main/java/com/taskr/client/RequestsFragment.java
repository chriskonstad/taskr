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
    private ArrayList<Request> nearbyRequests;
    private static final int DEFAULT_RADIUS = 100000;   // in miles

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

    private void loadData() {
        try {
            Location lastLocation = LocationProvider.getInstance().getLastLocation();

            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            double radius = DEFAULT_RADIUS; // TODO: store/get from settings?

            Api.getInstance(getActivity()).getNearbyRequests(latitude, longitude, radius,
                    new Api.ApiCallback<ArrayList<Request>>() {
                        @Override
                        public void onSuccess(ArrayList<Request> requests) {
                            ArrayList<String> request_text = new ArrayList<String>();

                            nearbyRequests = requests;
                            if(!nearbyRequests.isEmpty()){
                                for(int x = 0; x < nearbyRequests.size(); x++){
                                    Request req = nearbyRequests.get(x);
                                    request_text.add(req.title);
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                    R.layout.request_single, request_text);
                            setListAdapter(adapter);
                        }

                        @Override
                        public void onFailure(String message) {
                            ((MainActivity)getActivity())
                                    .showErrorDialog(getString(R.string.connection_error),
                                            "Unable to load nearby requests");
                        }
                    });
        } catch (Exception e) {
            ((MainActivity)getActivity()).showErrorDialog(getString(R.string.location_error_title),
                    e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.requests_list, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle(mTitle);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
