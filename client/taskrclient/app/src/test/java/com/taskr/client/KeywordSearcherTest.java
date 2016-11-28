package com.taskr.client;

import com.taskr.api.Request;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Patched by alejandro on 11/26/16.
 */

/**
 * Unit test for the KeywordSearcher utility class.
 *
 * This should thoroughly test the base of the searching/filtering feature of the client.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class KeywordSearcherTest {
    Request reqEmpty;
    Request reqComp;
    Request reqHelp;
    Request reqNoMatch;

    ArrayList<Request> requests;

    @Before
    public void setup() {

        reqEmpty = new Request();
        reqEmpty.title = "";

        reqComp = new Request();
        reqComp.title = "Computer broken";

        reqHelp = new Request();
        reqHelp.description = "Need help with homework.";

        reqNoMatch = new Request();
        reqNoMatch.title = "This request doesn't match";
        reqNoMatch.description = "Neither does the description";

        requests = new ArrayList<>();
        requests.add(reqEmpty);
        requests.add(reqComp);
        requests.add(reqHelp);
        requests.add(reqNoMatch);
    }

    @Test
    public void basic_query() throws Exception {
        String query = "computer";

        ArrayList<Request> filtered = KeywordSearcher.filterQuery(requests, query);
        assertTrue(filtered.contains(reqComp));
        assertFalse(filtered.contains(reqEmpty));
        assertFalse(filtered.contains(reqHelp));
        assertFalse(filtered.contains(reqNoMatch));
    }

    @Test
    public void compound_query() throws Exception {
        String query = "computer help";

        ArrayList<Request> filtered = KeywordSearcher.filterQuery(requests, query);
        assertTrue(filtered.contains(reqComp));
        assertTrue(filtered.contains(reqHelp));
    }

    @Test
    public void case_insensitive() throws Exception {
        String query = "COMPUTER";

        ArrayList<Request> filtered = KeywordSearcher.filterQuery(requests, query);
        assertTrue(filtered.contains(reqComp));
    }

    @Test
    public void empty_query() throws Exception {
        String query = "";

        ArrayList<Request> filtered = KeywordSearcher.filterQuery(requests, query);
        assertEquals(requests.size(), filtered.size());
    }

    @Test
    public void empty_list() throws Exception {
        String query = "computer";

        ArrayList<Request> filted = KeywordSearcher.filterQuery(new ArrayList<Request>(), query);
        assertEquals(0, filted.size());
    }
}