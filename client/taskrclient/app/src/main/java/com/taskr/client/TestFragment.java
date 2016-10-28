package com.taskr.client;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.taskr.api.Api;
import com.taskr.api.Profile;
import com.taskr.api.Request;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";
    public static final int DEFAULT_RADIUS = 10;
    public static final int LOCATION_REQUEST = 1;
    public static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;

    @BindString(R.string.app_name) String mTitle;
    @BindView(R.id.profile_id_result) TextView profileJSON;
    @BindView(R.id.nearby_requests_result) TextView requestsJSON;
    @BindView(R.id.profile_id_field) EditText profileId;
    @BindView(R.id.profile_find) Button profileFind;
    @BindView(R.id.nearby_find) Button nearbyFind;

    public TestFragment() {
        // Required for fragment subclass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle(mTitle);

        profileFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findUserById(view);
            }
        });

        nearbyFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNearbyRequests(view);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        initLocationMgr();
    }

    public void initLocationMgr(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST
            );
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);
    }

    public void findUserById(View view) {
        int userId = Integer.parseInt(profileId.getText().toString());

        Api.getInstance(getActivity()).getUserProfile(userId, new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                String profileString = "Name: " + profile.name + "\n" +
                        "ID: " + profile.id + "\n" +
                        "Wallet: " + profile.wallet;
                profileJSON.setText(profileString);
            }

            @Override
            public void onFailure(String message) {
                profileJSON.setText(message);
            }
        });
    }

    public void findNearbyRequests(View view){
        if(null == lastLocation) {
            for(String provider : locationManager.getAllProviders()) {
                if(null == lastLocation) {
                    try {
                        lastLocation = locationManager.getLastKnownLocation(provider);
                    } catch (SecurityException e) { // Android Studio complains otherwise :)
                        Log.e(TAG, e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        // lastLocation not promised to be nonnull, but through a NPE is as good as anything else
        // right now. Fix later
        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();
        double radius = DEFAULT_RADIUS; // TODO: store/get from settings?

        Api.getInstance(getActivity()).getNearbyRequests(latitude, longitude, radius,
                new Api.ApiCallback<ArrayList<Request>>() {
                    @Override
                    public void onSuccess(ArrayList<Request> requests) {
                        String data = "";

                        for(Request req : requests) {
                            data += "ID: " + req.id + "\n" +
                                    "Title: " + req.title + "\n" +
                                    "Amount: " + req.amount + "\n" +
                                    "\n";
                        }

                        if(data.equals("")) {
                            data = "No requests nearby";
                        }

                        requestsJSON.setText(data);
                    }

                    @Override
                    public void onFailure(String message) {
                        requestsJSON.setText(message);
                    }
                });
    }
}
