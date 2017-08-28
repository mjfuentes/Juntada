package com.nedelu.juntada.service.interfaces;

import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.view.BaseView;

import java.util.List;

public interface NotificationContract {

    interface View extends BaseView<Presenter> {

        void showNotifications(List<PushNotification> notifications);

        void showNoMessagesView();

        void popPushNotification(PushNotification message);

        void showEmptyState(boolean ok);
    }

    interface Presenter extends BasePresenter{

        void registerAppClient();

        void loadNotifications();

        void savePushMessage(String title, String description);
    }
}