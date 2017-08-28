package com.nedelu.juntada.firebase;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.util.PushNotificationsRepository;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private PushNotificationsRepository pushNotificationsRepository;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
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

        pushNotificationsRepository.savePushNotification(notification);
    }

    @Override
    public void onCreate() {
        pushNotificationsRepository = new PushNotificationsRepository(getBaseContext());
        super.onCreate();
    }
}
