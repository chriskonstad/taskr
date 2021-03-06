package com.taskr.client;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;


import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.taskr.api.Api;
import com.taskr.api.Request;
import com.taskr.api.RequestResult;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static android.app.Activity.RESULT_OK;

/**
 * Created by chris on 11/6/16.
 */

// Use this class for both creating and editing fragments
public class RequestFragment extends Fragment {
    private String TAG;
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final String REQUEST = "request";
    private Api mApi;

    @BindString(R.string.create_request) String createRequest;
    @BindString(R.string.edit_request) String editRequest;
    @BindView(R.id.title) EditText title;
    @BindView(R.id.amount) EditText amount;
    @BindView(R.id.due) EditText due;
    @BindView(R.id.description) EditText description;
    @BindView(R.id.location) EditText location;
    @BindView(R.id.button) Button button;
    @BindView(R.id.cancel) Button cancel;

    Calendar calendar = Calendar.getInstance();
    Place place;
    Request request = new Request();
    boolean editing = false;

    public RequestFragment() {
        // Required for fragment subclass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);
        ButterKnife.bind(this, rootView);

        TAG = getString(R.string.request_fragment_tag);

        mApi = ((MainActivity)getActivity()).api();

        String action = createRequest;
        // Load the existing request if editing
        cancel.setVisibility(View.INVISIBLE);
        if(null != getArguments()) {
            Request input = (Request)getArguments().getSerializable(REQUEST);
            if(null != input) {
                editing = true;
                request = input;
                action = editRequest;
                cancel.setVisibility(View.VISIBLE);
                loadRequest();
            }
        }
        button.setText(action);
        getActivity().setTitle(action);

        // Ensure the numeric keyboard appears
        amount.setRawInputType(Configuration.KEYBOARD_12KEY);

        due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptDue();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                // Ensure the request's current location is visible, otherwise use user's location
                if(editing) {
                    LatLngBounds bounds = LatLngBounds.builder()
                            .include(new LatLng(request.lat, request.longitude))
                            .build();

                    builder.setLatLngBounds(bounds);
                }

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                    ((MainActivity)getActivity())
                            .showErrorDialog(getString(R.string.places_error_title),
                                    e.getMessage());
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRequest();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.sure)
                        .setIcon(R.drawable.help_circle)
                        .setMessage(R.string.sure_cancel)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mApi.cancelRequest(request.id,
                                        new Api.ApiCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean returnValue) {
                                        ((MainActivity)getActivity())
                                                .showInfoDialog(getString(R.string.cancel_title),
                                                        getString(R.string.cancel_message),
                                                        new Callable<Void>() {
                                                            @Override
                                                            public Void call() throws Exception {
                                                                ((MainActivity)getActivity())
                                                                        .onBackPressed();
                                                                // TODO Back press twice?
                                                                return null;
                                                            }
                                                        });
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        ((MainActivity)getActivity())
                                                .showErrorDialog(getString(R.string.cancel_error_title),
                                                        message);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        checkIfDone();
        return rootView;
    }

    /**
     * Either create request or update it if it already exists
     */
    private void saveRequest() {
        request.title = title.getText().toString();
        request.amount = Double.parseDouble(amount.getText().toString());
        request.user_id = mApi.getId();
        if(null != place) {
            request.lat = place.getLatLng().latitude;
            request.longitude = place.getLatLng().longitude;
        }
        request.due = calendar.getTime();
        request.description = description.getText().toString();
        // DO NOT MANUALLY UPDATE ID, CREATED_AT, UPDATED_AT, STATUS, or ACTOR_ID

        if(editing) {
            mApi.editRequest(request, new Api.ApiCallback<Void>() {
                @Override
                public void onSuccess(Void v) {
                    ((MainActivity)getActivity())
                            .showInfoDialog(getString(R.string.edit_request_title),
                                    getString(R.string.edit_request_message),
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
                            .showErrorDialog(getString(R.string.edit_request_error_title),
                                    message);
                }
            });
        } else {
            mApi.createRequest(request, new Api.ApiCallback<RequestResult>() {
                @Override
                public void onSuccess(RequestResult returnValue) {
                    Log.i(TAG, "Created request with id: " + returnValue.id);
                    ((MainActivity)getActivity())
                            .showInfoDialog(getString(R.string.create_request_title),
                                    getString(R.string.create_request_message),
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
                            .showErrorDialog(getString(R.string.create_request_error_title),
                                    message);
                }
            });
        }
    }

    /**
     * Load a request into the UI
     */
    private void loadRequest() {
        title.setText(request.title);
        amount.setText(String.format("%.2f", request.amount));
        calendar.setTime(request.due);
        refreshDue();
        description.setText(request.description);
        LatLng latlng = new LatLng(request.lat, request.longitude);

        String text = latlng.toString();
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(request.lat, request.longitude, 1);
            if(null != addresses && !addresses.isEmpty()) {
                Address a = addresses.get(0);
                if(null != a.getAddressLine(0)) {
                    text = a.getAddressLine(0);
                } else if(null != a.getFeatureName()) {
                    text = a.getFeatureName();
                }
            }
        } catch (Exception e) {
            ((MainActivity)getActivity())
                    .showErrorDialog(getString(R.string.geocode_error_title),e.getMessage());
        }
        location.setText(text);
    }

    /**
     * Handle return results from spawned activities.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getContext(), data);
                location.setText(place.getName());
            }
        }
    }

    /**
     * Modify the UI based on if the request is in a valid state
     */
    @OnTextChanged({R.id.title, R.id.amount, R.id.due, R.id.description, R.id.location})
    public void checkIfDone() {
        if(ready()) {
            button.setEnabled(true);
            button.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        } else {
            button.setEnabled(false);
            button.setBackground(getResources().getDrawable(R.drawable.rounded_button_disabled));
        }
    }

    /**
     * Check if the request is in a valid state (everything is there, etc)
     * @return status (ready or not ready)
     */
    private boolean ready() {
        return !title.getText().toString().isEmpty() &&
                !amount.getText().toString().isEmpty() &&
                !due.getText().toString().isEmpty() &&
                !description.getText().toString().isEmpty() &&
                !location.getText().toString().isEmpty();
    }

    /**
     * Prompt the user for a due date and time
     */
    private void promptDue() {
        // Prompt user to select a date and then a time
        DatePickerDialog date = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker,
                                  int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                refreshDue();

                TimePickerDialog time = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        refreshDue();
                    }
                },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false);
                time.show();
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        date.show();
    }

    /**
     * Refresh the due date and time text view
     */
    private void refreshDue() {
        Format formatter = new SimpleDateFormat(Request.DUE_FORMAT);
        due.setText(formatter.format(calendar.getTime()));
    }
}
