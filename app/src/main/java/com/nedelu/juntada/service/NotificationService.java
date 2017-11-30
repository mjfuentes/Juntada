package com.nedelu.juntada.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.nedelu.juntada.R;
import com.nedelu.juntada.activity.EventActivity;
import com.nedelu.juntada.activity.GroupTabbedActivity;
import com.nedelu.juntada.activity.GroupsActivity;
import com.nedelu.juntada.activity.VoteActivity;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.util.PushNotificationsRepository;

import org.apache.commons.lang3.StringEscapeUtils;

import static com.nedelu.juntada.R.menu.event;
import static com.nedelu.juntada.R.menu.group;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class NotificationService {

    private Context mContext;
    private PushNotificationsRepository pushNotificationsRepository;
    private SharedPreferences preferences;
    private Boolean notifications;
    private Boolean groupNotifications;
    private Boolean messagesNotifications;
    private Boolean eventNotifications;
    private Boolean pollNotifications;

    public NotificationService(Context context) {
        this.mContext = context;
        pushNotificationsRepository = new PushNotificationsRepository(context);
    }

    private void readPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        notifications = preferences.getBoolean("notifications", true);
        messagesNotifications = preferences.getBoolean("messages_notifications", true);
        eventNotifications = preferences.getBoolean("event_notifications", true);
        pollNotifications = preferences.getBoolean("poll_notifications", true);
        groupNotifications = preferences.getBoolean("group_notifications", true);
    }

    public void displayNotificationGroupNameUpdated(User user, Group group, String oldName) {
        readPreferences();

        Intent intent = new Intent(mContext, GroupTabbedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("groupId", group.getId());
        editor.putString("image_url", group.getImageUrl());
        editor.apply();

        String title = mContext.getString(R.string.group_name_updated_title);
        String description = user.getFirstName() + mContext.getString(R.string.group_name_updated) + StringEscapeUtils.unescapeJava(oldName);
        Long notificationId = saveNotification(title, description, "group", group.getId(), user.getId());

        if (notifications && groupNotifications) {
            String ringtoneUri = preferences.getString("group_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }

    public void displayNotificationGroupImageUpdated(User user, Group group) {
        readPreferences();

        Intent intent = new Intent(mContext, GroupTabbedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("groupId", group.getId());
        editor.putString("image_url", group.getImageUrl());
        editor.apply();

        String title = mContext.getString(R.string.group_image_updated_title);
        String description = user.getFirstName() + mContext.getString(R.string.group_image_updated) + StringEscapeUtils.unescapeJava(group.getName());
        Long notificationId = saveNotification(title, description, "group", group.getId(), user.getId());

        if (notifications && groupNotifications) {

            String ringtoneUri = preferences.getString("group_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }


    public void displayNotificationGroupDeleted(User user, Group group) {
        readPreferences();

        Intent intent = new Intent(mContext, GroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String title = mContext.getString(R.string.group_deleted_title);
        String description = user.getFirstName() + mContext.getString(R.string.deleted_group) + StringEscapeUtils.unescapeJava(group.getName());
        Long notificationId = saveNotification(title, description, "groups", 0L, user.getId());

        if (notifications && groupNotifications) {
            String ringtoneUri = preferences.getString("group_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }

    }

    public void displayNotificationNewGroupMember(User user, Group group) {
        readPreferences();

        Intent intent = new Intent(mContext, GroupTabbedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("groupId", group.getId());
        editor.putString("image_url", group.getImageUrl());
        editor.apply();

        String title = mContext.getString(R.string.new_member);
        String description = user.getFirstName() + mContext.getString(R.string.joined_group) + StringEscapeUtils.unescapeJava(group.getName());
        Long notificationId = saveNotification(title, description, "group", group.getId(), user.getId());

        if (notifications && eventNotifications) {
            String ringtoneUri = preferences.getString("group_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }

    public void displayNotificationGroupMemberLeft(User user, Group group) {
        readPreferences();

        Intent intent = new Intent(mContext, GroupTabbedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("groupId", group.getId());
        editor.putString("image_url", group.getImageUrl());
        editor.apply();

        String title = mContext.getString(R.string.group_member_left);
        String description = user.getFirstName() + mContext.getString(R.string.left_group) + StringEscapeUtils.unescapeJava(group.getName());
        Long notificationId = saveNotification(title, description, "group", group.getId(), user.getId());

        if (notifications && eventNotifications) {
            String ringtoneUri = preferences.getString("group_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());

        }
    }

    public void displayNotificationEventDeleted(User user, Event event) {
        readPreferences();

        Intent intent = new Intent(mContext, GroupTabbedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("groupId", event.getGroup().getId());
        editor.putString("image_url", event.getGroup().getImageUrl());
        editor.apply();

        String title = mContext.getString(R.string.event_deleted);
        String description = user.getFirstName() + mContext.getString(R.string.user_deleted_event) + StringEscapeUtils.unescapeJava(event.getTitle()) + mContext.getString(R.string.in_the_group) + StringEscapeUtils.unescapeJava(event.getGroup().getName());
        Long notificationId = saveNotification(title, description, "group", event.getGroup().getId(), user.getId());

        if (notifications && eventNotifications) {

            String ringtoneUri = preferences.getString("event_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }

    public void displayNotificationEventUpdated(User user, Event event) {
        readPreferences();

        Intent intent = new Intent(mContext, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("showMessages", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);



        String title = mContext.getString(R.string.event_updated);
        String description = user.getFirstName() + mContext.getString(R.string.user_updated_event) + StringEscapeUtils.unescapeJava(event.getTitle()) + mContext.getString(R.string.in_the_group) + StringEscapeUtils.unescapeJava(event.getGroup().getName());
        Long notificationId = saveNotification(title, description, "event", event.getId(), user.getId());

        if (notifications && eventNotifications) {

            String ringtoneUri = preferences.getString("event_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }

    public void displayNotificationNewParticipant(User user, Event event) {
        readPreferences();

        Intent intent = new Intent(mContext, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("eventId", event.getId());
        intent.putExtra("showMessages", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String name = (user != null) ? user.getFirstName() : mContext.getString(R.string.a_new_member);
        String title = mContext.getString(R.string.event_updated);
        String description = name + mContext.getString(R.string.user_going) + StringEscapeUtils.unescapeJava(event.getTitle()) + mContext.getString(R.string.in_the_group) + StringEscapeUtils.unescapeJava(event.getGroup().getName());
        Long notificationId = saveNotification(title, description, "event", event.getId(), user.getId());

        if (notifications && eventNotifications) {

            String ringtoneUri = preferences.getString("event_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }

    }

    public void displayNotificationNotGoing(User user, Event event) {
        readPreferences();

        Intent intent = new Intent(mContext, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("eventId", event.getId());
        intent.putExtra("showMessages", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String name = (user != null) ? user.getFirstName() : mContext.getString(R.string.a_new_member);
        String title =mContext.getString(R.string.user_not_going_event);
        String description = name + mContext.getString(R.string.user_not_going) + StringEscapeUtils.unescapeJava(event.getTitle()) + mContext.getString(R.string.in_the_group) + StringEscapeUtils.unescapeJava(event.getGroup().getName());
        Long notificationId = saveNotification(title, description, "event", event.getId(), user.getId());

        if (notifications && eventNotifications) {
            String ringtoneUri = preferences.getString("event_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }

    public void displayNotificationNewEvent(Long eventId, Group group, User user) {
        readPreferences();

        Intent intent = new Intent(mContext, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("eventId", eventId);
        intent.putExtra("showMessages", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String name = (user != null) ? user.getFirstName() : mContext.getString(R.string.a_new_member);
        String title = mContext.getString(R.string.new_event);
        String description = name + mContext.getString(R.string.created_event) + StringEscapeUtils.unescapeJava(group.getName());
        Long notificationId = saveNotification(title, description, "event", eventId, user.getId());

        if (notifications && eventNotifications) {
            String ringtoneUri = preferences.getString("event_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }


    public void displayNotificationConfirmedEvent(Long eventId, String title, Group group, User user) {
        readPreferences();

        Intent intent = new Intent(mContext, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("eventId", eventId);
        intent.putExtra("showMessages", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String name = (user != null) ? user.getFirstName() : mContext.getString(R.string.a_new_member);
        String notTitle = mContext.getString(R.string.event_confirmed);
        String description = name + mContext.getString(R.string.confirmedEvent) + StringEscapeUtils.unescapeJava(title) + mContext.getString(R.string.in_the_group) + StringEscapeUtils.unescapeJava(group.getName());
        Long notificationId = saveNotification(notTitle, description, "event", eventId, user.getId());

        if (notifications && eventNotifications) {
            String ringtoneUri = preferences.getString("event_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(notTitle)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }

    public void displayNotificationNewPoll(Long pollId, Group group, User user) {
        readPreferences();

        Intent intent = new Intent(mContext, VoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pollId", pollId);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String name = (user != null) ? user.getFirstName() : mContext.getString(R.string.a_new_member);
        String title = mContext.getString(R.string.new_poll);
        String description = name + mContext.getString(R.string.created_poll) + StringEscapeUtils.unescapeJava(group.getName());
        Long notificationId = saveNotification(title, description, "poll",pollId, user.getId());

        if (notifications && pollNotifications) {
            String ringtoneUri = preferences.getString("poll_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }
    }

    public void displayNotificationUserVotedPoll(Poll poll, Group group, User user) {
        readPreferences();

        Intent intent = new Intent(mContext, VoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pollId", poll.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String name = (user != null) ? user.getFirstName() : mContext.getString(R.string.a_new_member);
        String title = mContext.getString(R.string.new_vote);
        String description = name + mContext.getString(R.string.user_voted_poll) + StringEscapeUtils.unescapeJava(poll.getTitle()) + mContext.getString(R.string.in_the_group) + StringEscapeUtils.unescapeJava(poll.getGroup().getName());
        Long notificationId = saveNotification(title, description, "poll",poll.getId(), user.getId());

        if (notifications && pollNotifications) {

            String ringtoneUri = preferences.getString("poll_notifications_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(Uri.parse(ringtoneUri))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId.intValue(), notificationBuilder.build());
        }

    }

    private Long saveNotification(String title, String description, String type, Long id, Long creator) {

        PushNotification notification = new PushNotification();
        notification.setTitle(title);
        notification.setDescription(description);
        notification.setmType(type);
        notification.setmValue(String.valueOf(id));
        notification.setCreatorId(creator);

        PushNotification saved = pushNotificationsRepository.savePushNotification(notification);
        if (saved != null) {
            return saved.getId();
        } else {
            return 0L;
        }

    }

}
