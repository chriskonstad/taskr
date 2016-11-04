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
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.taskr.api.Api;
import com.taskr.api.Profile;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public static final String UID = "user_id";
    @BindView (R.id.profile_picture) ImageView profilePicture;
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

        Api api = Api.getInstance(getContext());

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
                ButterKnife.apply(stars, SET_STARS, profile.avgRating);
                Ion.with(profilePicture)
                        .placeholder(R.drawable.loadingpng)
                        .load(profile.getProfilePictureUrl());
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
