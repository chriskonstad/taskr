package com.taskr.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by guillaumelam34 on 11/22/2016.
 */

public class InstanceIDListener extends InstanceIDListenerService {
    private static final String TAG = "InstanceIDListener";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
