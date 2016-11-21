package com.taskr.client;

import android.location.Location;

import com.taskr.api.Request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
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
@PrepareForTest(Location.class)
public class RequestTest {
    Request req;
    Date now;

    @Before
    public void setup() {
        now = Calendar.getInstance().getTime();
        req = new Request();
        req.due = now;
    }

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

    // Tests for Request.Status.getColor and Request.getDistance are in
    // RequestAdapterInstrumentationTest because they rely upon Android APIs
}
