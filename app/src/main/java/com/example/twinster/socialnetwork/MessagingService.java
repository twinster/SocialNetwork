package com.example.twinster.socialnetwork;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by user on 5/21/2018.
 */

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationMessage = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();

        String from_user_id = remoteMessage.getData().get("from_user_id");

        Notification.Builder builder = new Notification.Builder(MessagingService.this)
                .setSmallIcon(R.mipmap.notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSound(sound)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_id",from_user_id);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        int notificationId = (int) System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId,builder.build());


    }
}
