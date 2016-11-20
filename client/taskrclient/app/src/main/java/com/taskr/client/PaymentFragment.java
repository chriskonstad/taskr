package com.taskr.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.taskr.api.Api;

import java.util.Calendar;
import java.util.concurrent.Callable;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

/**
 * Created by guillaumelam34 on 11/19/2016.
 */

public class PaymentFragment extends Fragment {
    private String TAG;
    private SharedPreferences sharedPref;

    private String origNumber, origCVV;
    private int origExpMonth, origExpYear;

    @BindString(R.string.payment_info) String paymentInfo;
    @BindString(R.string.save_payment_info) String saveInfo;
    @BindView(R.id.card_number) EditText cardNumber;
    @BindView(R.id.card_cvv) EditText cardCVV;
    @BindView(R.id.card_datepicker) DatePicker cardDatepicker;
    @BindView(R.id.button) Button button;

    public PaymentFragment() {
        // Required for fragment subclass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, rootView);

        TAG = getString(R.string.payment_fragment_tag);
        getActivity().setTitle(paymentInfo);
        button.setText(saveInfo);

        LinearLayout ll = (LinearLayout)cardDatepicker.getChildAt(0);
        LinearLayout ll2 = (LinearLayout)ll.getChildAt(0);
        ll2.getChildAt(1).setVisibility(View.GONE);

        Context context = (MainActivity)getContext();
        sharedPref = context.getSharedPreferences(getString(R.string.payment_preferences) ,Context.MODE_PRIVATE);
        String card_number = origNumber = sharedPref.getString(getString(R.string.card_number),"");
        String card_cvv = origCVV = sharedPref.getString(getString(R.string.card_cvv),"");
        int card_expiration_month = origExpMonth = sharedPref.getInt(getString(R.string.card_expiration_month),-1);
        int card_expiration_year = origExpYear = sharedPref.getInt(getString(R.string.card_expiration_year),-1);

        if(card_number != ""){
            cardNumber.setText(card_number);
        }
        if(card_cvv != ""){
            cardCVV.setText(card_cvv);
        }

        Calendar calendar = Calendar.getInstance();
        card_expiration_month = card_expiration_month != -1 ? card_expiration_month : calendar.get(Calendar.MONTH);
        card_expiration_year = card_expiration_year != -1 ? card_expiration_year : calendar.get(Calendar.YEAR);

        cardDatepicker.init(card_expiration_year, card_expiration_month, 1, new DatePicker.OnDateChangedListener(){
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth){
                checkIfDone();
            }
        });

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                saveInfo();
            }
        });

        checkIfDone();
        return rootView;
    }

    @OnTextChanged({R.id.card_number, R.id.card_cvv})
    public void checkIfDone() {
        if(ready()) {
            button.setEnabled(true);
            button.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        } else {
            button.setEnabled(false);
            button.setBackground(getResources().getDrawable(R.drawable.rounded_button_disabled));
        }
    }

    private boolean ready(){
        return((!cardNumber.getText().toString().isEmpty() && !cardCVV.getText().toString().isEmpty()) &&
                (!(cardNumber.getText().toString()).equals(origNumber) || !(cardCVV.getText().toString()).equals(origCVV) || cardDatepicker.getMonth() != origExpMonth || cardDatepicker.getYear() != origExpYear));
    }

    public void saveInfo(){
        try {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.card_number), cardNumber.getText().toString());
            editor.putString(getString(R.string.card_cvv), cardCVV.getText().toString());
            editor.putInt(getString(R.string.card_expiration_month), cardDatepicker.getMonth());
            editor.putInt(getString(R.string.card_expiration_year), cardDatepicker.getYear());
            editor.commit();

            ((MainActivity)getActivity())
                    .showInfoDialog(getString(R.string.payment_update_success_title),
                            getString(R.string.payment_update_success_body),
                            new Callable<Void>() {
                                @Override
                                public Void call() throws Exception {
                                    ((MainActivity)getActivity()).onBackPressed();
                                    return null;
                                }
                            });

        }catch(Exception e){
            ((MainActivity)getActivity())
                    .showErrorDialog(getString(R.string.payment_update_error_title),
                            getString(R.string.payment_update_error_body));
        }
    }
}
