package com.nedelu.juntada.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nedelu.juntada.R;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.GroupService;

public class JoinActivity extends AppCompatActivity {


    private GroupService groupService;
    private EventService eventService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        Long userId = userPref.getLong("userId", 0L);
        groupService = GroupService.getInstance(JoinActivity.this);
        eventService = new EventService(JoinActivity.this);
        View blur = findViewById(R.id.blur_background);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();

    }
}
