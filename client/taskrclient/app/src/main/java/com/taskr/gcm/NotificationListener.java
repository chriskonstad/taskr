package com.taskr.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.taskr.client.MainActivity;
import com.taskr.client.R;

/**
 * Created by guillaumelam34 on 11/22/2016.
 */

public class NotificationListener extends GcmListenerService {
    private static final String TAG = "NotificationListener";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        //TODO: process message
        
        sendNotification(message);
    }

    private void sendNotification(String message) {
        //TODO: implement send notification method
        Log.i(TAG, message);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(this.getString(R.string.notification_type), "request");

        PendingIntent pIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder  = new NotificationCompat.Builder(this)
                .setContentTitle("GCM Notification")
                .setContentText(message)
                .setSmallIcon(R.drawable.star)
                .setContentIntent(pIntent)
                .setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder.build());
    }
}
