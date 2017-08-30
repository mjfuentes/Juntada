package com.nedelu.juntada.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nedelu.juntada.R;
import com.nedelu.juntada.activity.NotificationsActivity;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.util.PushNotificationsRepository;

import java.util.Map;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private PushNotificationsRepository pushNotificationsRepository;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
        displayNotification(remoteMessage.getNotification(), remoteMessage.getData());

    }

    private void displayNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        for (String key : data.keySet()){
            intent.putExtra(key, data.get(key));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent("NEW_NOTIFICATION");
        intent.putExtra("title", remoteMessage.getNotification().getTitle());
        intent.putExtra("description", remoteMessage.getNotification().getBody());
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);

        PushNotification notification = new PushNotification();
        notification.setTitle(remoteMessage.getNotification().getTitle());
        notification.setDescription(remoteMessage.getNotification().getBody());
        notification.setmType(remoteMessage.getData().get("type"));
        notification.setmValue(remoteMessage.getData().get("id"));
        notification.setCreatorId(Long.valueOf(remoteMessage.getData().get("creator")));

        pushNotificationsRepository.savePushNotification(notification);
    }

    @Override
    public void onCreate() {
        pushNotificationsRepository = new PushNotificationsRepository(getBaseContext());
        super.onCreate();
    }
}
