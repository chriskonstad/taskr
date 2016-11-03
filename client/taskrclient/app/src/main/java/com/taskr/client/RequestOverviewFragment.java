package com.taskr.client;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taskr.api.Api;
import com.taskr.api.Profile;
import com.taskr.api.Request;

import java.text.Format;
import java.text.SimpleDateFormat;
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
    @BindView(R.id.request_description) TextView requestDescription;
    @BindView(R.id.request_user_name) TextView requestUserName;
    @BindView(R.id.request_rating) TextView requestRating;
    @BindView(R.id.distance) TextView requestDistance;
    @BindView(R.id.due) TextView requestDue;
    @BindView(R.id.amount) TextView requestAmount;
    @BindView(R.id.map) FrameLayout mapContainer;
    SupportMapFragment mapFragment;

    public RequestOverviewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.request_overview, container, false);
        ButterKnife.bind(this, rootView);

        req = (Request)getArguments().getSerializable("request");

        getActivity().setTitle(getString(R.string.request_overview_title));

        requestTitle.setText(req.title);
        requestDescription.setText("Description: " + req.description);

        // Show the distance from the user's current location to the request
        float distance = req.getDistance(Api.getInstance(getContext()).getLocation());
        requestDistance.setText(String.format("%.1fmi", distance));
        requestDue.setText(req.getDue());
        requestAmount.setText(String.format("$%.1f", req.amount));

        Api.getInstance(getContext()).getUserProfile(req.user_id, new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile returnValue) {
                requestUserName.setText(returnValue.name);
                requestRating.setText(String.format("%.1f", returnValue.avgRating));
            }

            @Override
            public void onFailure(String message) {
                ((MainActivity)getActivity()).showErrorDialog(getString(R.string.connection_error),
                        "Unable to load user profile information for user with id: " +
                                Integer.toString(req.user_id));
            }
        });

        FragmentManager manager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) manager.findFragmentByTag("mapFragment");
        if(mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction trans = manager.beginTransaction();
            trans.add(R.id.map, mapFragment, "mapFragment");
            trans.commit();
            manager.executePendingTransactions();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Add marker for request location
                MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(req.lat, req.longitude));
                googleMap.addMarker(marker);
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    Log.w(TAG, "Unable to enable myLocation on google maps fragment");
                }

                CameraUpdate center = CameraUpdateFactory
                        .newLatLng(new LatLng(req.lat, req.longitude));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            }
        });

        return rootView;
    }
}
