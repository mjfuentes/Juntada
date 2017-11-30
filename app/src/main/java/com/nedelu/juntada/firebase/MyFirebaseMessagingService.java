package com.nedelu.juntada.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nedelu.juntada.R;
import com.nedelu.juntada.activity.EventActivity;
import com.nedelu.juntada.activity.NotificationsActivity;
import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.dao.GroupDao;
import com.nedelu.juntada.dao.MessageDao;
import com.nedelu.juntada.dao.UserDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Message;
import com.nedelu.juntada.model.MessageType;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.DataUpdateService;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.GroupService;
import com.nedelu.juntada.util.PushNotificationsRepository;

import org.apache.commons.lang3.StringEscapeUtils;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private PushNotificationsRepository pushNotificationsRepository;
    private MessageDao messageDao;
    private UserDao userDao;
    private EventDao eventDao;
    private Long userId;
    private DateTimeFormatter formatter;
    private GroupService groupService;
    private EventService eventService;
    private DataUpdateService dataUpdateService;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        processMessage(remoteMessage.getData());
    }

    private void displayMessageNotification(Map<String, String> data, Message message) {

        Intent intent;
        if (message.getType().equals(MessageType.EVENT)) {
            intent = new Intent(this, EventActivity.class);
        } else {
            intent = new Intent(this, NotificationsActivity.class);
        }
        for (String key : data.keySet()) {
            intent.putExtra(key, data.get(key));
        }
        intent.putExtra("eventId", message.getTypeId());
        intent.putExtra("showMessages", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);



        if (message.getType().equals(MessageType.EVENT)) {
            Event event = eventDao.getEvent(message.getTypeId());
            User user = userDao.getUser(message.getCreatorId());
            if (user == null || event == null) {
                eventService.loadEvent(message.getTypeId(), null);
            } else {
                int id = Integer.valueOf(String.valueOf(message.getType().ordinal()) + message.getTypeId());
                String title = "";
                String description = "";
                String groupName;
                Integer unread = activeNotificationsForId(id);
                if (event.getGroup() != null){
                    groupName = event.getGroup().getName();
                } else {
                    groupName = getString(R.string.invitations);
                }
                if (unread > 0) {
                    title = StringEscapeUtils.unescapeJava(event.getTitle()) + " @ " + groupName;
                    description = "Nuevos mensajes sin leer";
                } else {
                    title = StringEscapeUtils.unescapeJava(user.getFirstName() + " " + user.getLastName() + " @ " + event.getTitle());
                    description = StringEscapeUtils.unescapeJava(message.getMessage());
                }

                SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
                Boolean notifications = userPref.getBoolean("notifications", true);
                Boolean messagesNotifications = notifications && userPref.getBoolean("messages_notifications", true);

                if (messagesNotifications) {

                    String ringtoneUri = userPref.getString("messages_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(title)
                            .setAutoCancel(true)
                            .setSound(Uri.parse(ringtoneUri))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(id, notificationBuilder.build());
                }
            }

        }

    }

    private int activeNotificationsForId(Integer id) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = new StatusBarNotification[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifications = manager.getActiveNotifications();
        }
        int count = 0;
        for (int i = 0; i < notifications.length; i++) {
            if (notifications[i].getPackageName().equals(getApplicationContext().getPackageName()) && (notifications[i].getId() == id)) {
                count++;
            }
        }
        return count;
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
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(StringEscapeUtils.unescapeJava(data.get("description"))))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void processMessage(Map<String, String> data) {
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
            String updateType = data.get("update_type");
            
            switch (updateType){
                case "message":
                    handleMessage(data);
                    break;
                case "update":
                    handleUpdate(data);
                    break;
                default:
                    handleMessage(data);
                    break;
            }
        }
    }

    private void handleUpdate(Map<String, String> data) {

        String type = data.get("type");
        String action = data.get("action");
        Long id = Long.valueOf(data.get("type_id"));
        Long user;

        switch (type) {
            case "group":
                switch (action) {
                    case "delete":
                        user = Long.valueOf(data.get("user"));
                        dataUpdateService.deleteGroup(id, user);
                        break;
                    case "update":
                        user = Long.valueOf(data.get("user"));
                        String field = data.get("field");
                        switch (field) {
                            case "name":
                                dataUpdateService.updateGroupName(id, user);
                                break;
                            case "image":
                                dataUpdateService.updateGroupImage(id, user);
                                break;
                            case "new_member":
                                dataUpdateService.addGroupParticipant(id, user);
                                break;
                            case "member_left":
                                dataUpdateService.removeGroupParticipant(id, user);
                                break;
                        }
                        break;
                    case "new":
                        groupService.loadGroup(id);
                        break;
                }
                break;
            case "event":
                Long group_id;
                switch (action) {
                    case "delete":
                        user = Long.valueOf(data.get("user"));
                        dataUpdateService.deleteEvent(id, user);
                        break;
                    case "update":
                        user = Long.valueOf(data.get("user"));
                        String field = data.get("field");
                        switch (field) {
                            case "name":
                                dataUpdateService.updateEvent(id, user);
                                break;
                            case "location":
                                dataUpdateService.updateEvent(id, user);
                                break;
                            case "description":
                                dataUpdateService.updateEvent(id, user);
                                break;
                            case "going":
                                dataUpdateService.updateMemberGoing(id, user);
                                break;
                            case "not_going":
                                dataUpdateService.updateMemberNotGoing(id, user);
                                break;
                        }
                        break;
                    case "new":
                        group_id = Long.valueOf(data.get("group_id"));
                        user = Long.valueOf(data.get("user"));
                        dataUpdateService.newEvent(id,group_id, user);
                        break;
                    case "confirmed":
                        group_id = Long.valueOf(data.get("group_id"));
                        Long pollId = Long.valueOf(data.get("poll_id"));
                        user = Long.valueOf(data.get("user"));
                        dataUpdateService.confirmEvent(id, pollId,group_id, user);
                        break;
                    }
                    break;
            case "poll":
                switch (action) {
                    case "new":
                        group_id = Long.valueOf(data.get("group_id"));
                        user = Long.valueOf(data.get("user"));
                        dataUpdateService.newPoll(id,group_id, user);
                        break;
                    case "update":
                        group_id = Long.valueOf(data.get("group_id"));
                        user = Long.valueOf(data.get("user"));
                        String field = data.get("field");
                        switch (field) {
                            case "voted":
                                dataUpdateService.updateUserVotedPoll(id,group_id, user);
                                break;
                        }
                        break;
//                    case "delete":
//                        eventService.deletePoll(id);
//                        break;
                }

            }
    }


    private void handleMessage(Map<String, String> data) {
        Message message = new Message();
        message.setId(Long.valueOf(data.get("message_id")));
        message.setCreatorId(Long.valueOf(data.get("creator_id")));
        message.setMessage(data.get("message"));
        message.setTypeId(Long.valueOf(data.get("type_id")));
        message.setType(MessageType.valueOf(data.get("type")));
        messageDao.saveMessage(message);

        if (userId != message.getCreatorId()) {
            displayMessageNotification(data, message);
        }

        Intent intent = new Intent("MESSAGE");
        intent.putExtra("type", data.get("type"));
        intent.putExtra("type_id", Long.valueOf(data.get("type_id")));
        intent.putExtra("message", data.get("message"));
        intent.putExtra("creator_id", Long.valueOf(data.get("creator_id")));
        intent.putExtra("message_id", Long.valueOf(data.get("message_id")));
        intent.putExtra("time", data.get("time"));

        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    private NotificationCompat.InboxStyle getStyleForNotification(String messageBody) {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        SharedPreferences sharedPref = getSharedPreferences("NotificationData", 0);
        Map<String, String> notificationMessages = (Map<String, String>) sharedPref.getAll();
        Map<String, String> myNewHashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : notificationMessages.entrySet()) {
        myNewHashMap.put(entry.getKey(), entry.getValue());
        }
        inbox.addLine(messageBody);
        for (Map.Entry<String, String> message : myNewHashMap.entrySet()) {
        inbox.addLine(message.getValue());
        }
        inbox.setBigContentTitle(this.getResources().getString(R.string.app_name))
        .setSummaryText("Tap to open");
        return inbox;
        }



    @Override
    public void onCreate() {
        pushNotificationsRepository = new PushNotificationsRepository(getBaseContext());
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = userPref.getLong("userId", 0L);
        messageDao = new MessageDao(getBaseContext());
        userDao = new UserDao(getBaseContext());
        eventDao = new EventDao(getBaseContext());
        groupService = GroupService.getInstance(getBaseContext());
        eventService = new EventService(getBaseContext());
        dataUpdateService = new DataUpdateService(getBaseContext());
        formatter= DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                        .withLocale( Locale.ENGLISH )
                        .withZone(ZoneId.systemDefault());
        super.onCreate();
    }
}
