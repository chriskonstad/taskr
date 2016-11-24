package com.taskr.client;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

/**
 * Created by guillaumelam34 on 11/13/2016.
 */

public class NotificationHandler {
    private int mInterval = 5000;

    private Handler mHandler;
    private Context mContext;
    private int mUid;

    private Runnable mNotificationChecker = new Runnable(){
        @Override
        public void run(){
            try{
                //API stuff here
                //TODO: send notifications based on server resposne
                sendRequestNotification();
                sendReviewNotification();
            }finally{
                mHandler.postDelayed(mNotificationChecker, mInterval);
            }
        }
    };

    public NotificationHandler(Context context, int uid){
        mHandler = new Handler();
        mUid = uid;
        mContext = context;
    }

    public void startNotificationCheck(){
        mNotificationChecker.run();
    }

    public void stopNotificationCheck(){
        mHandler.removeCallbacks(mNotificationChecker);
    }

    //Notification redirects to user's requests currently
    //TODO: modify parameters to take a Notification object returned from API call to server
    //TODO: modify code so that the notification ID is the ID of the completed request
    //TODO: notification redirects to request overview fragment
    public void sendRequestNotification(){
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(mContext.getString(R.string.notification_type), "request");

        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder  = new NotificationCompat.Builder(mContext)
                .setContentTitle("Request Notification Title")
                .setContentText("Text relevant to the request")
                .setSmallIcon(R.drawable.star)
                .setContentIntent(pIntent)
                .setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(2, mBuilder.build());
    }

    //Notification redirects to user's profile currently
    //TODO: redirect to fragment for specific review?
    public void sendReviewNotification(){
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(mContext.getString(R.string.notification_type), "review");

        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder  = new NotificationCompat.Builder(mContext)
                .setContentTitle("Review Notification Title")
                .setContentText("Text relevant to the review")
                .setSmallIcon(R.drawable.star)
                .setContentIntent(pIntent)
                .setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, mBuilder.build());
    }
}
