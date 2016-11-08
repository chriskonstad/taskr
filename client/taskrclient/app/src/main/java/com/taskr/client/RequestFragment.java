package com.taskr.client;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private static final String TAG = "RequestFragment";
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final String REQUEST = "request";

    @BindString(R.string.create_request) String createRequest;
    @BindString(R.string.edit_request) String editRequest;
    @BindView(R.id.title) EditText title;
    @BindView(R.id.amount) EditText amount;
    @BindView(R.id.due) EditText due;
    @BindView(R.id.description) EditText description;
    @BindView(R.id.location) EditText location;
    @BindView(R.id.button) Button button;

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

        String action = createRequest;
        // Load the existing request if editing
        if(null != getArguments()) {
            Request input = (Request)getArguments().getSerializable(REQUEST);
            if(null != input) {
                editing = true;
                request = input;
                action = editRequest;
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

        checkIfDone();
        return rootView;
    }

    // Either create request or update it if it already exists
    private void saveRequest() {
        request.title = title.getText().toString();
        request.amount = Double.parseDouble(amount.getText().toString());
        request.user_id = Api.getInstance().getId();
        if(null != place) {
            request.lat = place.getLatLng().latitude;
            request.longitude = place.getLatLng().longitude;
        }
        request.due = calendar.getTime();
        request.description = description.getText().toString();
        // DO NOT MANUALLY UPDATE ID, CREATED_AT, UPDATED_AT, STATUS, or ACTOR_ID

        if(editing) {
            Api.getInstance().editRequest(request, new Api.ApiCallback<Void>() {
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
            Api.getInstance().createRequest(request, new Api.ApiCallback<RequestResult>() {
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

    private void loadRequest() {
        title.setText(request.title);
        amount.setText(String.format("%.2f", request.amount));
        calendar.setTime(request.due);
        refreshDue();
        description.setText(request.description);
        LatLng latlng = new LatLng(request.lat, request.longitude);

        // TODO Use reverse geocoder to find name for latlong pair
        location.setText(latlng.toString());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getContext(), data);
                location.setText(place.getName());
            }
        }
    }

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

    private boolean ready() {
        return !title.getText().toString().isEmpty() &&
                !amount.getText().toString().isEmpty() &&
                !due.getText().toString().isEmpty() &&
                !description.getText().toString().isEmpty() &&
                !location.getText().toString().isEmpty();
    }

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

    private void refreshDue() {
        Format formatter = new SimpleDateFormat(Request.DUE_FORMAT);
        due.setText(formatter.format(calendar.getTime()));
    }
}
