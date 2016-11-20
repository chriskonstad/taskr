package com.taskr.client;

/**
 * Created by Roger on 11/10/2016.
 */

        import android.os.Bundle;

        //import android.app.DialogFragment;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.DialogFragment;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.RatingBar;
        import android.widget.RatingBar.OnRatingBarChangeListener;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.taskr.api.Api;
        import java.util.concurrent.Callable;
        import butterknife.BindView;
        import butterknife.ButterKnife;

public class RatingFragment extends DialogFragment{
    @BindView (R.id.rating_bar) RatingBar ratingBar;
    @BindView (R.id.btn_submit) Button btnSubmit;
    public static final String reqID = "request_id";
    Api mApi;

    public static RatingFragment newInstance(int requestID)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(reqID, requestID);
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
                if (getArguments() != null) {
                    requestID = getArguments().getInt(reqID);
                }
                int rating = (int)ratingBar.getRating();
                mApi.rateCompletedRequest(requestID, rating,
                        new Api.ApiCallback<Boolean>() {
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
