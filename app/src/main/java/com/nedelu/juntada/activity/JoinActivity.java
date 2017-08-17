package com.nedelu.juntada.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nedelu.juntada.R;
import com.nedelu.juntada.service.GroupService;

public class JoinActivity extends AppCompatActivity {


    private GroupService groupService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        SharedPreferences userPref = getSharedPreferences("user", 0);
        Long userId = userPref.getLong("userId", 0L);
        groupService = GroupService.getInstance(JoinActivity.this);

        View blur = findViewById(R.id.blur_background);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();

        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)){
            progressBar.setVisibility(View.VISIBLE);
            blur.setVisibility(View.VISIBLE);

            String[] parts = intent.getDataString().split("/");
            String token = parts[parts.length-1];
            groupService.joinGroup(userId, token, JoinActivity.this);
            finish();
        }
    }
}
