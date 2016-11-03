package com.taskr.api;

import android.location.Location;
import android.os.Parcelable;

import com.taskr.client.LocationProvider;
import com.taskr.client.MainActivity;
import com.taskr.client.R;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chris on 10/28/16.
 */

public class Request implements Serializable {
    public static final String DUE_FORMAT = "EEE, d MMM yyyy 'by' h:mma";
    public int id;
    public String title;
    public double amount;
    public int user_id;
    public double lat;
    public double longitude;
    public Date due;
    public String description;
    public Date created_at;
    public Date updated_at;
    public String status;
    public int actor_id;
    // TODO add more of the data fields

    // Get difference between current
    public float getDistance(Location location) {
        // Get the distance in meter, blame Android's API here
        float[] distanceTemp = new float[1];
        Location.distanceBetween(lat, longitude,
                location.getLatitude(), location.getLongitude(),
                distanceTemp);
        return distanceTemp[0] / 1609.34f; // convert meters to miles
    }

    // Return the due date as a formatted string
    public String getDue() {
        Format formatter = new SimpleDateFormat(DUE_FORMAT);
        return formatter.format(due);
    }
}
