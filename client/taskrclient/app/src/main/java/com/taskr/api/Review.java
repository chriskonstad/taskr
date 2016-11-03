package com.taskr.api;
import java.util.Date;

/**
 * Created by Roger on 11/3/2016.
 */

public class Review {
    public int id;
    public int reviewer_id;
    public int reviewee_id;
    public int request_id;
    public int rating;
    public Date created_at;
}
