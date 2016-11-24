package com.taskr.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.taskr.client.R;

/**
 * Created by guillaumelam34 on 11/22/2016.
 */

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "GCMRegistrationService";

    public RegistrationIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent registrationComplete = new Intent(getString(R.string.gcm_registration_complete));

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            registrationComplete.putExtra(getString(R.string.gcm_device_token), token);

            sharedPreferences.edit().putBoolean(getString(R.string.token_sent_to_server), true).apply();
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }catch(Exception e){
            Log.i(TAG, "Failed to complete token refresh");
            sharedPreferences.edit().putBoolean(getString(R.string.token_sent_to_server), false).apply();
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }
}
