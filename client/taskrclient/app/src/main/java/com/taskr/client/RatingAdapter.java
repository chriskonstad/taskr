package com.taskr.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.support.v7.widget.AppCompatRatingBar;
import android.widget.TextView;

import com.taskr.api.Rating;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 11/2/16.
 */

public class RatingAdapter extends ArrayAdapter<Rating> {
    private static final String TAG = "RatingAdapter";
    private ArrayList<Rating> mRatings;
    private Context mContext;

    @BindView(R.id.title) TextView title;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.when) TextView when;
    @BindView(R.id.rating) AppCompatRatingBar ratingBar;

    public RatingAdapter(Context context, ArrayList<Rating> ratings) {
        super(context, -1, ratings);
        this.mContext = context;
        this.mRatings = ratings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rating, parent, false);
        ButterKnife.bind(this, rowView);

        Rating r = mRatings.get(position);

        title.setText(r.title);
        name.setText(r.name);
        when.setText(r.getCreatedAt());
        ratingBar.setRating((float)r.rating);

        return rowView;
    }
}
