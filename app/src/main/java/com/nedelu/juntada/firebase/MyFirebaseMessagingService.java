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
import com.nedelu.juntada.dao.MessageDao;
import com.nedelu.juntada.model.Message;
import com.nedelu.juntada.model.MessageType;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.util.PushNotificationsRepository;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Map;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private PushNotificationsRepository pushNotificationsRepository;
    private MessageDao messageDao;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData());
    }

    private void displayNotification(Map<String, String> data) {
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
                .setContentTitle(StringEscapeUtils.unescapeJava(data.get("title")))
                .setContentText(StringEscapeUtils.unescapeJava(data.get("description")))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotification(Map<String, String> data) {
        if (data.get("notification").equals("true")) {
            Intent intent = new Intent("NEW_NOTIFICATION");
            intent.putExtra("title", data.get("title"));
            intent.putExtra("description", data.get("description"));
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(intent);

            PushNotification notification = new PushNotification();
            notification.setTitle(data.get("title"));
            notification.setDescription(data.get("description"));
            notification.setmType(data.get("type"));
            notification.setmValue(data.get("id"));
            notification.setCreatorId(Long.valueOf(data.get("creator")));

            pushNotificationsRepository.savePushNotification(notification);
            displayNotification(data);

        } else {
            Intent intent = new Intent("MESSAGE");
            intent.putExtra("type", data.get("type"));
            intent.putExtra("type_id", Long.valueOf(data.get("type_id")));
            intent.putExtra("message", data.get("message"));
            intent.putExtra("creator_id", Long.valueOf(data.get("creator_id")));
            intent.putExtra("message_id", Long.valueOf(data.get("message_id")));

            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(intent);

            Message message = new Message();
            message.setId(Long.valueOf(data.get("message_id")));
            message.setCreatorId(Long.valueOf(data.get("creator_id")));
            message.setMessage(data.get("message"));
            message.setTypeId(Long.valueOf(data.get("type_id")));
            message.setType(MessageType.valueOf(data.get("type")));
            messageDao.saveMessage(message);

        }
    }

    @Override
    public void onCreate() {
        pushNotificationsRepository = new PushNotificationsRepository(getBaseContext());
        messageDao = new MessageDao(getBaseContext());
        super.onCreate();
    }
}
