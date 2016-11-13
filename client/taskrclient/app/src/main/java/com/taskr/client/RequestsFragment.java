package com.taskr.client;

import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guillaumelam34 on 10/29/2016.
 */

public class RequestsFragment extends ListFragment {
    private String TAG;
    public static final String LOGGED_IN_USER = "IsSpecificUser";
    private ArrayAdapter<Request> adapter;
    private static final int DEFAULT_RADIUS = 100000;   // in miles

    private boolean specificUserFlag = false;

    @BindString(R.string.nearby_requests) String mTitleNearby;
    @BindString(R.string.my_requests) String mTitleMine;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab) FloatingActionButton fab;

    public RequestsFragment(){

    }

    public boolean isLoggedInUser() {
        return specificUserFlag;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Request req = adapter.getItem(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable("request", req);

        TAG = getString(R.string.requests_fragment_tag);

        RequestOverviewFragment overviewFrag = new RequestOverviewFragment();
        overviewFrag.setArguments(bundle);

        ((MainActivity)getActivity()).showFragment(overviewFrag, true, new TransitionParams(getString(R.string.requests_fragment_tag), getString(R.string.request_overview_fragment_tag)));
    }

    private void loadNearbyRequests() {
        Api.getInstance().refreshLocation((MainActivity)getActivity());
        Location lastLocation = Api.getInstance().getLocation();

        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();
        double radius = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getString(R.string.key_search_radius),
                        getContext().getString(R.string.default_search_radius)));

        Log.i(TAG, "the search radius is " + Double.toString(radius) + " miles");

        Api.getInstance().getNearbyRequests(latitude, longitude, radius,
                new Api.ApiCallback<ArrayList<Request>>() {
                    @Override
                    public void onSuccess(ArrayList<Request> requests) {
                        adapter = new RequestAdapter(getContext(), requests);
                        setListAdapter(adapter);
                        onDoneRefreshing();
                    }

                    @Override
                    public void onFailure(String message) {
                        ((MainActivity)getActivity())
                                .showErrorDialog(getString(R.string.connection_error),
                                        "Unable to load nearby requests");
                        onDoneRefreshing();
                    }
                });
    }

    private void loadUserRequests(){
        Api.getInstance().refreshLocation((MainActivity)getActivity());
        
        Api.getInstance().getUserRequests(Api.getInstance().getId(),
                new Api.ApiCallback<ArrayList<Request>>() {
                    @Override
                    public void onSuccess(ArrayList<Request> requests) {
                        adapter = new RequestAdapter(getContext(), requests);
                        setListAdapter(adapter);
                        onDoneRefreshing();
                    }

                    @Override
                    public void onFailure(String message) {
                        ((MainActivity)getActivity())
                                .showErrorDialog(getString(R.string.connection_error),
                                        "Unable to load user requests");
                        onDoneRefreshing();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.requests_list, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(LOGGED_IN_USER)){
            specificUserFlag = true;
            getActivity().setTitle(mTitleMine);
        } else {
            getActivity().setTitle(mTitleNearby);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Show create request fragment
                Log.i(TAG, "FAB pressed");
                ((MainActivity)getActivity()).showFragment(new RequestFragment(), true, new TransitionParams(getString(R.string.requests_fragment_tag), getString(R.string.request_fragment_tag)));
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return rootView;
    }

    private void refresh() {
        if(specificUserFlag) {
            loadUserRequests();
        }
        else{
            loadNearbyRequests();
        }
    }

    private void onDoneRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }
}