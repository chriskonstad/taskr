package com.taskr.client;

/**
 * Created by Roger on 11/10/2016.
 */

import android.os.Bundle;

import android.support.v4.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.taskr.api.Api;
import com.taskr.api.Review;

import java.util.concurrent.Callable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RatingFragment extends DialogFragment{
    @BindView (R.id.rating_bar) RatingBar ratingBar;
    @BindView (R.id.btn_submit) Button btnSubmit;
    @BindView (R.id.comment) EditText commentView;
    public static final String reqID = "request_id";
    public static final String revID = "reviewee_id";
    Api mApi;

    public static RatingFragment newInstance(int requestID, int reviewee_id)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(reqID, requestID);
        bundle.putInt(revID, reviewee_id);
        RatingFragment ratingFragment = new RatingFragment();
        ratingFragment.setArguments(bundle);
        return ratingFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);
        ButterKnife.bind(this, rootView);
        getDialog().setTitle(R.string.rate_task);
        mApi = ((MainActivity)getActivity()).api();
        addButtonListener();
        return rootView;
    }

    private void addButtonListener() {
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int requestID = -1;
                int revieweeID = -1;
                if (getArguments() != null) {
                    requestID = getArguments().getInt(reqID);
                    revieweeID = getArguments().getInt(revID);
                } else {
                    throw new RuntimeException("No arguments set for RatingFragment");
                }
                int rating = (int)ratingBar.getRating();
                String comment = commentView.getText().toString();
                if(comment.equals("")) {
                    comment = null;
                }
                Review review = new Review();
                review.reviewer_id = mApi.getId();
                review.reviewee_id = revieweeID;
                review.rating = rating;
                review.request_id = requestID;
                review.comment = comment;
                mApi.rateCompletedRequest(review, new Api.ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean b) {
                        ((MainActivity)getActivity())
                                .showInfoDialog(getString(R.string.rating_success),
                                        getString(R.string.rating_submitted),
                                        new Callable<Void>() {
                                            @Override
                                            public Void call() throws Exception {
                                                ((MainActivity)getActivity()).onBackPressed();
                                                return null;
                                            }
                                        });
                        getDialog().dismiss();
                    }

                    @Override
                    public void onFailure(String message) {
                        ((MainActivity)getActivity())
                                .showErrorDialog(getString(R.string.rating_failure),
                                        message);
                        getDialog().dismiss();
                    }
                });
            }
        });
    }
}
