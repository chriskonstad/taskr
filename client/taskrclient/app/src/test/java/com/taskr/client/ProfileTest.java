package com.taskr.client;

import com.taskr.api.Profile;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by chris on 11/21/16.
 */

public class ProfileTest {
    @Test
    public void check_profile_url() throws Exception {
        Profile profile = new Profile();
        profile.fbid = "1234";

        assertEquals("http://graph.facebook.com/1234/picture?height=960&width=960",
                profile.getProfilePictureUrl());
        assertEquals(Profile.buildProfilePictureUrl(profile.fbid),
                profile.getProfilePictureUrl());
    }
}
