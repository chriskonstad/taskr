package com.taskr.client;

import com.taskr.api.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chris on 11/21/16.
 */

public class KeywordSearcher {
    private static final String TAG = "KeywordSearcher";

    /**
     * Filter a list of requests based on a query string.
     * <p>
     * The query is split into keywords using whitespace.
     * </p>
     * @param requests the list of requests to search through
     * @param query the query string
     * @return a list of requests matching the query string's keywords
     */
    public static ArrayList<Request> filterQuery(List<Request> requests, String query) {
        List<String> keywords = getKeywords(query);

        return filter(requests, keywords);
    }

    /**
     * Because fucking Java doesn't have a built-in filter for collections. Wtf Java.
     * @param requests
     * @param keywords
     * @return
     */
    private static ArrayList<Request> filter(List<Request> requests, List<String> keywords) {
        ArrayList<Request> ret = new ArrayList<>();

        for(Request r : requests) {
            if(matchesKeywords(r, keywords)) {
                ret.add(r);
            }
        }

        return ret;
    }

    /**
     * Turn a query into a list of keywords
     * @param query
     * @return
     */
    private static List<String> getKeywords(String query) {
        return Arrays.asList(query.split("\\s+"));
    }

    /**
     * Check if a request matches the given list of keywords
     * @param r
     * @param keywords
     * @return
     */
    private static boolean matchesKeywords(Request r, List<String> keywords) {
        boolean ret = false;
        for(String s : keywords) {
            if(null != r.title && r.title.toLowerCase().contains(s.toLowerCase())){
                ret = true;
                break;
            }

            if(null != r.description && r.description.toLowerCase().contains(s.toLowerCase())) {
                ret = true;
                break;
            }
        }

        return ret;
    }
}
