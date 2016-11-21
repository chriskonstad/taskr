package com.taskr.client;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import com.taskr.api.Request;
import com.taskr.api.TestApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RequestAdapterInstrumentedTest {
    private RequestAdapter mAdapter;
    private TestApi mApi;

    private Request req0;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        ArrayList<Request> data = new ArrayList<>();
        mApi = new TestApi(appContext);
        Date time = Calendar.getInstance().getTime();
        time.setTime(time.getTime() + 1000 * 60 * 60 * 24 * 7);

        req0 = new Request();
        req0.title = "Sample Title";
        req0.description = "Sample Description";
        req0.longitude = mApi.getLocation().getLongitude() + 0.1;
        req0.lat = mApi.getLocation().getLatitude() + 0.1;
        req0.due = time;
        req0.amount = 100;
        req0.status = Request.Status.OPEN;

        data.add(req0);
        mAdapter = new RequestAdapter(mApi, appContext, data);
    }


    @Test
    public void testGetView() throws Exception {
        View view = mAdapter.getView(0, null, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView status = (TextView) view.findViewById(R.id.status);
        TextView due = (TextView) view.findViewById(R.id.due);
        TextView amount = (TextView) view.findViewById(R.id.amount);
        TextView distance = (TextView) view.findViewById(R.id.distance);

        // Ensure all views are present
        assertNotNull(view);
        assertNotNull(title);
        assertNotNull(status);
        assertNotNull(due);
        assertNotNull(amount);
        assertNotNull(distance);

        // Ensure contents are what they should be
        assertEquals(req0.title, title.getText());
        assertEquals(req0.status, status.getText());
        assertEquals(req0.getDue(), due.getText());
        assertEquals(String.format("$%.2f", req0.amount), amount.getText());
        assertEquals(String.format("%.1fmi", req0.getDistance(mApi.getLocation())), distance.getText());
    }
}
