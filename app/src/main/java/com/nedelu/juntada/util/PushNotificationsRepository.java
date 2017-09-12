package com.nedelu.juntada.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;

import com.nedelu.juntada.model.PushNotification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class PushNotificationsRepository {

    private DatabaseHelper helper;

    public PushNotificationsRepository(Context context) {
        this.helper = new DatabaseHelper(context);
    }


    public void getPushNotifications(LoadCallback callback) {
        try {
            callback.onLoaded(helper.getPushNotificationDao().queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PushNotification savePushNotification(PushNotification notification) {
        try {
            return helper.getPushNotificationDao().createIfNotExists(notification);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface LoadCallback {
        void onLoaded(List<PushNotification> notifications);
    }

}