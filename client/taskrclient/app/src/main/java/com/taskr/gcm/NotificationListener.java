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
        String status = data.getString("status");
        String requestID = data.getString("request_id");
        String requestTitle = data.getString("request_title");
        sendNotification(status, requestID, requestTitle);
    }

    private void sendNotification(String status, String requestID, String requestTitle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(this.getString(R.string.notification_type), "request");
        intent.putExtra(this.getString(R.string.request_id), requestID);

        PendingIntent pIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder  = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.star)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        if(status.equals("accept")){
            mBuilder.setContentTitle(this.getString(R.string.request_accepted));
            mBuilder.setContentText(String.format(this.getString(R.string.request_accepted_body), requestTitle));
        }
        else if(status.equals("complete")){
            mBuilder.setContentTitle(this.getString(R.string.request_completed));
            mBuilder.setContentText(String.format(this.getString(R.string.request_completed_body), requestTitle));
        }
        else if(status.equals("paid")){
            mBuilder.setContentTitle(this.getString(R.string.request_paid));
            mBuilder.setContentText(String.format(this.getString(R.string.request_paid_body), requestTitle));
        }

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(requestID), mBuilder.build());
    }
}
