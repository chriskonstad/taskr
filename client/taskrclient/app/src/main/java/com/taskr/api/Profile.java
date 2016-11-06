package com.taskr.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 10/28/16.
 */

public class Profile {
    public int id;
    public String name;
    public double wallet;
    public double avgRating;
    public String fbid;
    public ArrayList<Rating> ratings;

    // TODO add more of the data fields
    public String getProfilePictureUrl() {
        return buildProfilePictureUrl(fbid);
    }

    public static String buildProfilePictureUrl(String fbid) {
        return "http://graph.facebook.com/" + fbid + "/picture?height=960&width=960";
    }
}
