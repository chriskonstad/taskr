package com.taskr.client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.Button;


import com.koushikdutta.ion.Ion;
import com.taskr.api.Api;
import com.taskr.api.Profile;
import com.taskr.api.Request;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guillaumelam34 on 10/29/2016.
 */

public class RequestOverviewFragment extends Fragment{
    private String TAG;
    private Request req;
    private Api mApi;

    @BindView(R.id.request_title) TextView requestTitle;
    @BindView(R.id.request_description) TextView requestDescription;
    @BindView(R.id.request_user_name) TextView requestUserName;
    @BindView(R.id.request_rating) TextView requestRating;
    @BindView(R.id.distance) TextView requestDistance;
    @BindView(R.id.due) TextView requestDue;
    @BindView(R.id.amount) TextView requestAmount;
    @BindView(R.id.status) TextView status;
    @BindView(R.id.map) FrameLayout mapContainer;
    @BindView(R.id.action_button) Button actionButton;
    @BindView(R.id.profile_picture) ImageView profilePicture;
    SupportMapFragment mapFragment;

    // Possible onClick listeners for the action button
    Button.OnClickListener listenerAccept = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mApi.acceptRequest(req.id, new Api.ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    ((MainActivity)getActivity())
                            .showInfoDialog(getString(R.string.accept_request_success_title),
                                    getString(R.string.accept_request_success_msg),
                                    new Callable<Void>() {
                                        @Override
                                        public Void call() throws Exception {
                                            ((MainActivity)getActivity()).onBackPressed();
                                            return null;
                                        }
                                    });
                }

                @Override
                public void onFailure(String message) {
                    ((MainActivity)getActivity())
                            .showErrorDialog(getString(R.string.accept_request_error_title),
                                    getString(R.string.accept_request_error_msg));
                }
            });
        }
    };

    Button.OnClickListener listenerPay = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Context context = (MainActivity)getContext();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.payment_preferences) , Context.MODE_PRIVATE);
            if(sharedPref.getString(getString(R.string.card_name), "") != "" ||
                    sharedPref.getString(getString(R.string.card_number), "") == "" ||
                    sharedPref.getString(getString(R.string.card_cvv), "") == "" ||
                    sharedPref.getInt(getString(R.string.card_expiration_month), -1) == -1 ||
                    sharedPref.getInt(getString(R.string.card_expiration_year), -1) == -1){

                DialogFragment paymentFrag = new PaymentInfoFragment();
                paymentFrag.setTargetFragment(RequestOverviewFragment.this, 1);
                ((MainActivity)getActivity()).showFragmentAsDialog(paymentFrag);
            }
        }
    };

    Button.OnClickListener listenerEdit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment editFragment = new RequestFragment();
            Bundle args = new Bundle();
            args.putSerializable(RequestFragment.REQUEST, req);
            editFragment.setArguments(args);
            ((MainActivity)getActivity()).showFragment(editFragment, true, new TransitionParams(getString(R.string.request_overview_fragment_tag), getString(R.string.request_fragment_tag)));
        }
    };

    Button.OnClickListener listenerComplete = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mApi.completeRequest(req.id, new Api.ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    ((MainActivity)getActivity())
                            .showInfoDialog(getString(R.string.complete_request_success_title),
                                    getString(R.string.complete_request_success_msg),
                                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            ((MainActivity)getActivity()).onBackPressed();
                            return null;
                        }
                    });
                }

                @Override
                public void onFailure(String message) {
                    ((MainActivity)getActivity())
                            .showErrorDialog(getString(R.string.complete_request_error_title),
                                    getString(R.string.complete_request_error_msg));
                }
            });
        }
    };

    private void setProfileLinkListener(final int uid)
    {
        requestUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment profileFragment = ProfileFragment.newInstance(uid);
                ((MainActivity)getActivity()).showFragment(profileFragment, true, new TransitionParams("", getString(R.string.profile_fragment_tag)));
            }
        });
    }

    public RequestOverviewFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.request_overview, container, false);
        ButterKnife.bind(this, rootView);

        TAG = getString(R.string.request_overview_fragment_tag);
        mApi = ((MainActivity)getActivity()).api();

        req = (Request)getArguments().getSerializable("request");

        getActivity().setTitle(getString(R.string.request_overview_title));

        int title = R.string.action_bug;
        boolean enabled = true;
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new RuntimeException("No onClick listener set for the action button!");
            }
        };
        if(mApi.getId() == req.user_id) {
            switch(req.status) {
                case Request.Status.ACCEPTED:
                    enabled = false;
                    title = R.string.action_accepted;
                    break;
                case Request.Status.CANCELED:
                    enabled = false;
                    title = R.string.action_canceled;
                    break;
                case Request.Status.COMPLETED:
                    title = R.string.action_pay;
                    listener = listenerPay;
                    break;
                case Request.Status.OPEN:
                    title = R.string.action_edit;
                    listener = listenerEdit;
                    break;
                case Request.Status.PAID:
                    enabled = false;
                    title = R.string.action_paid;
                    break;
                default:
                    enabled = false;
            }
        } else {
            switch (req.status) {
                case Request.Status.ACCEPTED:
                    title = R.string.action_complete;
                    listener = listenerComplete;
                    break;
                case Request.Status.CANCELED:
                    enabled = false;
                    title = R.string.action_canceled;
                    break;
                case Request.Status.COMPLETED:
                    enabled = false;
                    title = R.string.action_completed;
                    break;
                case Request.Status.OPEN:
                    title = R.string.action_accept;
                    listener = listenerAccept;
                    break;
                case Request.Status.PAID:
                    enabled = false;
                    title = R.string.action_paid;
                    break;
                default:
                    enabled = false;
            }
        }
        actionButton.setText(getString(title));
        actionButton.setEnabled(enabled);
        actionButton.setOnClickListener(listener);
        if(!enabled) {
            actionButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disabled));
        }
        status.setText(req.status);
        status.setTextColor(ContextCompat.getColor(getContext(), Request.Status.getColor(req.status)));

        requestTitle.setText(req.title);
        requestDescription.setText("Description: " + req.description);

        // Show the distance from the user's current location to the request
        float distance = req.getDistance(mApi.getLocation());
        requestDistance.setText(String.format("%.1fmi", distance));
        requestDue.setText(req.getDue());
        requestAmount.setText(String.format("$%.2f", req.amount));

        mApi.getUserProfile(req.user_id, new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile returnValue) {
                requestUserName.setText(returnValue.name);
                requestRating.setText(String.format("%.1f", returnValue.avgRating));
                setProfileLinkListener(returnValue.id);
                //setProfileLinkListener(((MainActivity)getActivity()).api().getId());
                Ion.with(profilePicture)
                        .placeholder(R.drawable.loadingpng)
                        .load(returnValue.getProfilePictureUrl());
            }

            @Override
            public void onFailure(String message) {
                ((MainActivity)getActivity()).showErrorDialog(getString(R.string.connection_error),
                        "Unable to load user profile information for user with id: " +
                                Integer.toString(req.user_id));
            }
        });

        FragmentManager manager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) manager.findFragmentByTag("mapFragment");
        if(mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction trans = manager.beginTransaction();
            trans.add(R.id.map, mapFragment, "mapFragment");
            trans.commit();
            manager.executePendingTransactions();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Add marker for request location
                MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(req.lat, req.longitude));
                googleMap.addMarker(marker);
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    Log.w(TAG, "Unable to enable myLocation on google maps fragment");
                }

                CameraUpdate center = CameraUpdateFactory
                        .newLatLng(new LatLng(req.lat, req.longitude));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            }
        });

        return rootView;
    }

    private void rateFulfiller()
    {
        if (req == null)
            return;
        RatingFragment ratingFragment = RatingFragment.newInstance(req.id, req.actor_id);
        ratingFragment.show(getActivity().getSupportFragmentManager(), "Rating Fragment");
    }

    public void completePayment(){
        mApi.payRequest(req.id, new Api.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ((MainActivity)getActivity())
                        .showInfoDialog(getString(R.string.pay_request_success_title),
                                getString(R.string.pay_request_success_msg),
                                new Callable<Void>() {
                                    @Override
                                    public Void call() throws Exception {
                                        ((MainActivity)getActivity()).onBackPressed();
                                        return null;
                                    }
                                });
                // display rating dialog
                rateFulfiller();
            }

            @Override
            public void onFailure(String message) {
                ((MainActivity)getActivity())
                        .showErrorDialog(getString(R.string.pay_request_error_title),
                                getString(R.string.pay_request_error_msg));
            }
        });
    }
}
