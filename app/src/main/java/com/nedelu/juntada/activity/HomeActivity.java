package com.nedelu.juntada.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        Long userId = userPref.getLong("userId",0L);

        if (userId != 0L){
            Intent intent = new Intent(this, GroupsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        finish();
    }



}
