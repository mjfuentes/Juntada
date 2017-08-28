package com.nedelu.juntada.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.PushNotificationsAdapter;
import com.nedelu.juntada.firebase.MyFirebaseMessagingService;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.service.interfaces.NotificationContract;
import com.nedelu.juntada.util.NotificationsPresenter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements NotificationContract.View{

    private RecyclerView mRecyclerView;
    private LinearLayout mNoMessagesView;
    private PushNotificationsAdapter mNotificatiosAdapter;
    private NotificationsPresenter mPresenter;
    private BroadcastReceiver mNotificationsReceiver;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNotificatiosAdapter = new PushNotificationsAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_notifications_list);
        mNoMessagesView = (LinearLayout) findViewById(R.id.noMessages);
        mRecyclerView.setAdapter(mNotificatiosAdapter);
        mPresenter = new NotificationsPresenter(this, this);
        mNotificationsReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadNotifications();
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mNotificationsReceiver, new IntentFilter("NEW_NOTIFICATION"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mNotificationsReceiver);
    }


    @Override
    public void setPresenter(NotificationContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = (NotificationsPresenter) presenter;
        } else {
            throw new RuntimeException("El presenter de notificaciones no puede ser null");
        }
    }

    @Override
    public void showNotifications(List<PushNotification> notifications) {
        mNotificatiosAdapter.replaceData(notifications);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showNoMessagesView() {

    }

    @Override
    public void showEmptyState(boolean ok) {
        mRecyclerView.setVisibility(ok ? View.GONE : View.VISIBLE);
        mNoMessagesView.setVisibility(ok ? View.VISIBLE : View.GONE);
    }

    @Override
    public void popPushNotification(PushNotification pushMessage) {
        mNotificatiosAdapter.addItem(pushMessage);
    }
}
