package com.nedelu.juntada.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.service.interfaces.NotificationContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsPresenter implements NotificationContract.Presenter {
    private final NotificationContract.View mNotificationView;
    private PushNotificationsRepository pushNotificationsRepository;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public NotificationsPresenter(NotificationContract.View view, Context context){
        mNotificationView = view;
        view.setPresenter(this);
        pushNotificationsRepository = new PushNotificationsRepository(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void start() {
        registerAppClient();
        loadNotifications();
    }

    @Override
    public void registerAppClient() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void loadNotifications() {
        pushNotificationsRepository.getPushNotifications(
                new PushNotificationsRepository.LoadCallback() {

                    @Override
                    public void onLoaded(List<PushNotification> notifications) {
                        if (notifications.size() > 0){
                            mNotificationView.showEmptyState(false);
                            Collections.sort(notifications, new Comparator<PushNotification>() {
                                @Override
                                public int compare(PushNotification pushNotification, PushNotification t1) {
                                    return t1.getId().compareTo(pushNotification.getId());
                                }
                            });
                            mNotificationView.showNotifications(notifications);
                        }else {
                            mNotificationView.showEmptyState(true);
                        }
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void savePushMessage(String title, String description) {
        PushNotification notification = new PushNotification();
        notification.setDescription(description);
        notification.setTitle(title);
        pushNotificationsRepository.savePushNotification(notification);
        loadNotifications();
    }
}