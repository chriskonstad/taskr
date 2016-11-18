package com.taskr.client;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by chris on 10/31/16.
 */

public class LocationProvider {
    private static final String TAG = "LocationProvider";
    private static LocationProvider mInstance;
    private static LocationManager mLocationManager;
    private static LocationListener mLocationListener;
    private static Location mLastLocation;

    public static final int LOCATION_REQUEST = 1;
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationProvider() {
    }

    /**
     * Checks if the user has granted Taskr location permissions already
     * @param activity the activity calling the function
     * @return whether or not location permissions have been granted to Taskr, in the form of a boolean
     */
    public static Boolean hasPermissions(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks if the user has granted Taskr location permissions
     * and prompts the user to grant them if they haven't been already
     * @param activity the activity calling the function
     */
    public static void checkPermissions(Activity activity) {
        if (!hasPermissions(activity)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);
        }
    }

    /**
     * Handles actions after a change has been made to Taskr's location permissions
     * A listener on changes to the user's Location is instantiated so that
     * a user's last location will be easily retrievable in the future
     * @param activity the activity calling the function
     */
    public static void onPermissionsChanged(Activity activity) {
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        // TODO Handle when permissions are denied
        try {
            mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, mLocationListener);
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Returns a singleton for LocationProvider
     * @return LocationProvider object singleton
     */
    public static LocationProvider getInstance() {
        if(null == mInstance) {
            synchronized (LocationProvider.class) {
                if(null == mInstance) {
                    mInstance = new LocationProvider();
                }
            }
        }

        return mInstance;
    }

    /**
     * Gets the user's last known location
     * @return the user's last known location in the form of a Location object
     */
    public Location getLastLocation() throws Exception {
        if(null == mLastLocation) {
            for(String provider : mLocationManager.getAllProviders()) {
                if(null == mLastLocation) {
                    try {
                        mLastLocation = mLocationManager.getLastKnownLocation(provider);
                    } catch (SecurityException e) { // Android Studio complains otherwise :)
                        Log.e(TAG, e.getMessage());
                        throw new Exception(e.getMessage());
                    }
                }
            }
        }

        // If unable to get location at all, throw an exception
        if(null == mLastLocation) {
            throw new Exception("Unable to get current location");
        }

        return mLastLocation;
    }
}
