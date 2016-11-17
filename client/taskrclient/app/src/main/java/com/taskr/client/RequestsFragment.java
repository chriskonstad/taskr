package com.taskr.client;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.taskr.api.Api;
import com.taskr.api.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
    private ArrayList<Request> nearbyRequests;
    private final HashMap<Marker, Request> markerRequests = new HashMap<Marker, Request>();

    private static final int DEFAULT_RADIUS = 100000;   // in miles

    private boolean specificUserFlag = false;

    @BindString(R.string.nearby_requests) String mTitleNearby;
    @BindString(R.string.my_requests) String mTitleMine;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.fab_map) FloatingActionButton fab_map;
    @BindView(R.id.fab_list) FloatingActionButton fab_list;
    @BindView(android.R.id.list) ListView requestList;
    @BindView(R.id.map_nearby_requests) FrameLayout mapContainer;
    SupportMapFragment mapFragment;

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
                        nearbyRequests = requests;
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
                        nearbyRequests = requests;
                        adapter = new RequestAdapter(getContext(), requests);
                        setListAdapter(adapter);
                        markerRequests.clear();
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
                ((MainActivity)getActivity()).showFragment(new RequestFragment(), true, new TransitionParams(getString(R.string.requests_fragment_tag), getString(R.string.request_fragment_tag)));
            }
        });

        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeRefreshLayout.animate().translationY(swipeRefreshLayout.getHeight()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mapContainer.setVisibility(View.VISIBLE);
                    }
                });
                fab_map.setVisibility(View.INVISIBLE);
                fab_list.setVisibility(View.VISIBLE);
            }
        });

        fab_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapContainer.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mapContainer.setVisibility(View.INVISIBLE);
                    }
                });
                fab_list.setVisibility(View.INVISIBLE);
                fab_map.setVisibility(View.VISIBLE);
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

        FragmentManager manager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) manager.findFragmentByTag("mapFragment");
        if(mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction trans = manager.beginTransaction();
            trans.add(R.id.map_nearby_requests, mapFragment, "mapFragment");
            trans.commit();
            manager.executePendingTransactions();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Location userLocation = Api.getInstance().getLocation();

                for(int i = 0 ; i < nearbyRequests.size(); i++) {
                    Request req = nearbyRequests.get(i);

                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(req.lat, req.longitude))
                            .title(req.title + " ($" + String.format("%.2f", req.amount) + ")")
                            .snippet(req.description);
                    Marker reqMarker = googleMap.addMarker(marker);
                    markerRequests.put(reqMarker, req);
                }

                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                googleMap.setInfoWindowAdapter(new RequestInfoWindowAdapter(getContext()));
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Request req = markerRequests.get(marker);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("request", req);

                        RequestOverviewFragment overviewFrag = new RequestOverviewFragment();
                        overviewFrag.setArguments(bundle);

                        ((MainActivity)getActivity()).showFragment(overviewFrag, true, new TransitionParams(getString(R.string.requests_fragment_tag), getString(R.string.request_overview_fragment_tag)));
                    }
                });

                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    Log.w(TAG, "Unable to enable myLocation on google maps fragment");
                }

                CameraUpdate center = CameraUpdateFactory
                        .newLatLng(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }
}