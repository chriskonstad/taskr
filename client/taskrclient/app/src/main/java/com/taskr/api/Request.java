package com.taskr.api;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chris on 10/28/16.
 */

public class Request implements Serializable {
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
}
