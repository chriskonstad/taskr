package com.taskr.client;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import com.taskr.api.Request;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chris on 10/28/16.
 */

// Use this class for both creating and editing fragments
public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";

    @BindString(R.string.request_title) String mTitle;
    @BindView(R.id.amount) EditText amount;
    @BindView(R.id.due) EditText due;
    @BindView(R.id.button) Button button;

    Calendar calendar = Calendar.getInstance();

    public RequestFragment() {
        // Required for fragment subclass
    }

    // TODO: Make it possible to load up an existing request, allowing users to edit requests
    // with this too
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle(mTitle);

        // Ensure the numeric keyboard appears
        amount.setRawInputType(Configuration.KEYBOARD_12KEY);

        due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptDue();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Eventually replace with a map view? Not sure how to handle that...
                /*
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setTitle("Amount");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for OK button here
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                alert.show();
                */
            }
        });

        return rootView;
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
