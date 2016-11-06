package com.taskr.client;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taskr.api.Api;
import com.taskr.api.Rating;
import com.taskr.api.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
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
    @BindViews({R.id.rstar1,
            R.id.rstar2,
            R.id.rstar3,
            R.id.rstar4,
            R.id.rstar5})
    List<ImageView> stars;
    static final ButterKnife.Setter<ImageView, Integer> SET_STARS =
            new ButterKnife.Setter<ImageView, Integer>() {
                @Override
                public void set(@NonNull ImageView view, Integer value, int index) {
                    int starNum = index + 1;
                    if(starNum <= value) {
                        view.setImageResource(R.drawable.star);
                    } else {
                        view.setImageResource(R.drawable.star_outline);
                    }
                }
            };

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
        ButterKnife.apply(stars, SET_STARS, r.rating);

        return rowView;
    }
}
