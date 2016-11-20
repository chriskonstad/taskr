package com.taskr.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.koushikdutta.ion.Ion;
import com.taskr.api.Api;
import com.taskr.api.Profile;
import com.taskr.api.Rating;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

public class ProfileFragment extends Fragment {
    private String TAG;
    public static final String UID = "user_id";
    @BindView (R.id.profile_picture) ImageView profilePicture;
    @BindView (R.id.ratings_list) ListView ratingsList;
    @BindView (R.id.overall_rating) RatingBar ratingBar;
    @BindView (R.id.rating_descriptor) TextView ratingDescriptor;
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

        Api api = ((MainActivity)getActivity()).api();

        getActivity().setTitle(R.string.profile_loading);
        int uid = getArguments().getInt(UID);
        ratingDescriptor.setText(getString(R.string.average_rating_descriptor));

        final ProgressDialog dialog = ((MainActivity)getActivity())
                .showProgressDialog(getString(R.string.profile_loading));
        dialog.show();
        api.getUserProfile(uid, new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                dialog.dismiss();
                getActivity().setTitle(profile.name);
                setEmailListener(profile.email);
                Log.i(TAG, "Avg rating: " + Double.toString(profile.avgRating));
                ratingBar.setRating((float)profile.avgRating);
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

    private void setEmailListener(final String email)
    {
        // Don't set email listener if the profile displayed is our own
        if (((MainActivity)getActivity()).api().getId() == getArguments().getInt(UID)) {
            return;
        }
        final Fragment frag = this;
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject) + ((MainActivity)getActivity()).api().getName());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                }
            }
        });

    }


}
