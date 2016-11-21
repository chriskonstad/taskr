package com.taskr.client;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taskr.api.Api;
import com.taskr.api.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private Api mApi;

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        for(int i=0; i<menu.size(); i++) {
            if(menu.getItem(i).getItemId() == R.id.search) {
                menu.getItem(i).setVisible(true);
            }
        }
    }

    /**
     * Load all nearby requests from the Taskr server
     */
    private void loadNearbyRequests() {
        mApi.refreshLocation((MainActivity)getActivity());
        Location lastLocation = mApi.getLocation();

        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();
        double radius = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getString(R.string.key_search_radius),
                        getContext().getString(R.string.default_search_radius)));

        Log.i(TAG, "the search radius is " + Double.toString(radius) + " miles");

        mApi.getNearbyRequests(latitude, longitude, radius,
                new Api.ApiCallback<ArrayList<Request>>() {
                    @Override
                    public void onSuccess(ArrayList<Request> requests) {
                        nearbyRequests = requests;
                        displayRequests(nearbyRequests);
                    }

                    @Override
                    public void onFailure(String message) {
                        ((MainActivity)getActivity())
                                .showErrorDialog(getString(R.string.connection_error),
                                        "Unable to load nearby requests");
                        onDoneRefreshing(new ArrayList<Request>());
                    }
                });
    }

    /**
     * Load all requests for a user
     */
    private void loadUserRequests(){
        mApi.refreshLocation((MainActivity)getActivity());
        
        mApi.getUserRequests(mApi.getId(),
                new Api.ApiCallback<ArrayList<Request>>() {
                    @Override
                    public void onSuccess(ArrayList<Request> requests) {
                        nearbyRequests = requests;
                        displayRequests(nearbyRequests);
                    }

                    @Override
                    public void onFailure(String message) {
                        ((MainActivity)getActivity())
                                .showErrorDialog(getString(R.string.connection_error),
                                        "Unable to load user requests");
                        onDoneRefreshing(new ArrayList<Request>());
                    }
                });
    }

    /**
     * Display the given requests
     * @param requests
     */
    private void displayRequests(ArrayList<Request> requests) {
        adapter = new RequestAdapter(mApi, getContext(), requests);
        setListAdapter(adapter);
        markerRequests.clear();
        onDoneRefreshing(requests);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.requests_list, container, false);
        ButterKnife.bind(this, rootView);
        mApi = ((MainActivity)getActivity()).api();
        setHasOptionsMenu(true);

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

    /**
     * Refresh the data to be displayed
     */
    private void refresh() {
        if(specificUserFlag) {
            loadUserRequests();
        }
        else{
            loadNearbyRequests();
        }
    }

    /**
     * Callback for when done refreshing.
     * <p>
     * This handles displaying all loaded data.
     * </p>
     * @param requests the requests to display
     */
    private void onDoneRefreshing(final ArrayList<Request> requests) {
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
                Location userLocation = mApi.getLocation();

                markerRequests.clear();
                googleMap.clear();
                for(int i = 0 ; i < requests.size(); i++) {
                    Request req = requests.get(i);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
        Log.i(TAG, "Inflated menu");
        SearchView mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displaySearchResults(newText);
                return false;
            }
        });
    }

    /**
     * Because fucking Java doesn't have a built-in filter for collections. Wtf Java.
     * @param requests
     * @param keywords
     * @return
     */
    private ArrayList<Request> filter(List<Request> requests, List<String> keywords) {
        ArrayList<Request> ret = new ArrayList<>();

        for(Request r : requests) {
            if(matchesKeywords(r, keywords)) {
                ret.add(r);
            }
        }

        return ret;
    }

    /**
     * Display the results of a query
     * @param query
     */
    private void displaySearchResults(String query) {
        List<String> keywords = getKeywords(query);
        ArrayList<Request> filteredResults = filter(nearbyRequests, keywords);

        Log.i(TAG, "Displaying " + filteredResults.size() + " results for query: " + query);

        displayRequests(filteredResults);
    }

    /**
     * Turn a query into a list of keywords
     * @param query
     * @return
     */
    private List<String> getKeywords(String query) {
        return Arrays.asList(query.split("\\s+"));
    }

    /**
     * Check if a request matches the given list of keywords
     * @param r
     * @param keywords
     * @return
     */
    private boolean matchesKeywords(Request r, List<String> keywords) {
        boolean ret = false;
        for(String s : keywords) {
            if(null != r.title && r.title.toLowerCase().contains(s.toLowerCase())){
                ret = true;
                break;
            }

            if(null != r.description && r.description.toLowerCase().contains(s.toLowerCase())) {
                ret = true;
                break;
            }
        }

        return ret;
    }
}