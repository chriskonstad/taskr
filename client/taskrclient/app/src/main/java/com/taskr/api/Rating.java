package com.taskr.api;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chris on 11/5/16.
 */

// Basically a review, but some of the IDs have been replaced with text representing the data we
// care about.  This is to reduce the number of HTTP requests
public class Rating {
    public static final String DATE_FORMAT = "d MMM yyyy";
    public int id;
    public String name;
    public String title;
    public int rating;
    public Date created_at;

    public String getCreatedAt() {
        Format formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(created_at);
    }
}
