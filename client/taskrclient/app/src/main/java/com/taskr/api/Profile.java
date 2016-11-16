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

    /**
     * Get the URL of the FB profile picture associated with this account.
     * @return URL of the FB profile picture.
     */
    public String getProfilePictureUrl() {
        return buildProfilePictureUrl(fbid);
    }

    /**
     * Build the URL of the FB profile picture associated with the given FBID.
     * <p>
     * This is a static function so that we can get profile pictures for arbitrary FBIDs instead of
     * only logged in FBIDs.
     * </p>
     * @param fbid the Facebook ID
     * @return URL of the FB profile picture
     */
    public static String buildProfilePictureUrl(String fbid) {
        return "http://graph.facebook.com/" + fbid + "/picture?height=960&width=960";
    }
}
