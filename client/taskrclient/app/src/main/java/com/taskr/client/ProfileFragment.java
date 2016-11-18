package com.taskr.client;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.taskr.api.Api;
import com.taskr.api.Profile;
import com.taskr.api.Rating;
import com.taskr.api.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

public class ProfileFragment extends Fragment {
    private String TAG;
    public static final String UID = "user_id";
    @BindView (R.id.profile_picture) ImageView profilePicture;
    @BindView (R.id.ratings_list) ListView ratingsList;
    //@BindView (R.id.rating) RatingBar ratingBar;
    @BindViews({R.id.star1,
            R.id.star2,
            R.id.star3,
            R.id.star4,
            R.id.star5})
    List<ImageView> stars;
    static final ButterKnife.Setter<ImageView, Double> SET_STARS =
            new ButterKnife.Setter<ImageView, Double>() {
        @Override
        public void set(@NonNull ImageView view, Double value, int index) {
            int starNum = index + 1;
            double diff = value - starNum;
            if(-0.25 <= diff) {
                view.setImageResource(R.drawable.star);
            } else if (-0.75 <= diff) {
                view.setImageResource(R.drawable.star_half);
            } else {
                view.setImageResource(R.drawable.star_outline);
            }
        }
    };

    public ProfileFragment() {
        // Required for fragment subclass
    }

    public static ProfileFragment newInstance(int uid) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(UID, uid);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            View rootView = inflater.inflate(R.layout.profile, container, false);
        ButterKnife.bind(this, rootView);

        TAG = getString(R.string.profile_fragment_tag);

        Api api = Api.getInstance();

        getActivity().setTitle(R.string.profile_loading);
        int uid = getArguments().getInt(UID);

        final ProgressDialog dialog = ((MainActivity)getActivity())
                .showProgressDialog(getString(R.string.profile_loading));
        dialog.show();
        api.getUserProfile(uid, new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                dialog.dismiss();
                getActivity().setTitle(profile.name);
                Log.i(TAG, "Avg rating: " + Double.toString(profile.avgRating));
                ButterKnife.apply(stars, SET_STARS, profile.avgRating);
                //ratingBar.setRating((float)profile.avgRating);
                Ion.with(profilePicture)
                        .placeholder(R.drawable.loadingpng)
                        .load(profile.getProfilePictureUrl());
                ArrayList<Rating> ratings = profile.ratings;
                if(null == ratings) {
                    ratings = new ArrayList<Rating>();
                }
                RatingAdapter adapter = new RatingAdapter(getContext(), ratings);
                ratingsList.setAdapter(adapter);
            }

            @Override
            public void onFailure(String message) {
                dialog.dismiss();
                ((MainActivity)getActivity())
                        .showErrorDialog(getString(R.string.profile_error),
                                "Failed to load profile");
            }
        });

        return rootView;
    }

}
