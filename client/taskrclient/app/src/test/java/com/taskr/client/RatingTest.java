package com.taskr.client;

import com.taskr.api.Rating;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Created by chris on 11/21/16.
 */

@RunWith(PowerMockRunner.class)
public class RatingTest {
    @Test
    public void check_created_at_format() throws Exception {
        Date now = Calendar.getInstance().getTime();
        Rating rating = new Rating();
        rating.created_at = now;
        final String FORMAT = "d MMM yyyy";

        Format format = new SimpleDateFormat(Rating.DATE_FORMAT);
        assertEquals(FORMAT, Rating.DATE_FORMAT);
        assertEquals(format.format(now), rating.getCreatedAt());
    }
}
