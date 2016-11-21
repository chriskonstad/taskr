package com.taskr.client;

import android.location.Location;
import android.util.Log;

import com.taskr.api.Request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.when;

/**
 * Created by chris on 11/21/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Location.class)
public class RequestTest {
    //Location mockLocation = Mockito.mock(Location.class);
    //double lng;
    //double lat;
    Request req;
    Date now;

    @Before
    public void setup() {
        //lng = -118.4455;
        //lat = 34.069;

        now = Calendar.getInstance().getTime();
        req = new Request();
        req.due = now;
    }

    /*
    @Test
    public void test() throws Exception {
        Request req = new Request();
        when(mockLocation.getLongitude()).thenReturn(lng);
        when(mockLocation.getLatitude()).thenReturn(lat);
        req.longitude = mockLocation.getLongitude();
        req.lat = mockLocation.getLatitude();

        Location somewhereElse = Mockito.mock(Location.class);
        when(mockLocation.getLongitude()).thenReturn(lng + 0.2);
        when(mockLocation.getLatitude()).thenReturn(lat + 0.2);

        // How to mock Location.getDistance?
        System.out.println(req.getDistance(somewhereElse));
    }
    */

    @Test
    public void check_due_format() throws Exception {
        final String DUE_FORMAT = "EEE, d MMM yyyy 'by' h:mma";

        Format format = new SimpleDateFormat(DUE_FORMAT);

        // Check that the formats are equal
        assertEquals(DUE_FORMAT, Request.DUE_FORMAT);

        // Check that nothing else weird happens
        assertEquals(format.format(now), req.getDue());
    }

    @Test
    public void check_status_text() throws Exception {
        assertEquals("open", Request.Status.OPEN);
        assertEquals("accepted", Request.Status.ACCEPTED);
        assertEquals("completed", Request.Status.COMPLETED);
        assertEquals("canceled", Request.Status.CANCELED);
        assertEquals("paid", Request.Status.PAID);
    }

    // TODO Test Request.Status.getColor?
}
