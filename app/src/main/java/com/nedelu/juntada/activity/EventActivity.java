package com.nedelu.juntada.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.UserAdapter;
import com.nedelu.juntada.model.Assistance;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.GroupService;
import com.nedelu.juntada.service.UserService;
import com.nedelu.juntada.util.SpacesItemDecoration;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    private Long userId;
    private Long eventId;
    private Group group;
    private Event event;
    private GroupService groupService;
    private EventService eventService;
    private RecyclerView userList;
    private UserAdapter userAdapter;
    private View blur;
    private ProgressBar progressBar;
    private View yesButton;
    private View noButton;
    private View maybeButton;
    private User user;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        eventId = userPref.getLong("eventId", 0L);

        userService = new UserService(this);
        user = userService.getUser(userId);
        eventService = new EventService(this);
        event = eventService.getEvent(eventId);

        userList = (RecyclerView) findViewById(R.id.userList);
        userAdapter = new UserAdapter(EventActivity.this, event.getConfirmedUsers(), userList);
        userList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        userList.setAdapter(userAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        userList.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        blur = findViewById(R.id.blur_background);

        yesButton = findViewById(R.id.yes);
        noButton = findViewById(R.id.no);
        maybeButton = findViewById(R.id.maybe);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                blur.setVisibility(View.VISIBLE);
                saveAssistance(Assistance.GOING);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                blur.setVisibility(View.VISIBLE);
                saveAssistance(Assistance.NOT_GOING);
            }
        });

        maybeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                blur.setVisibility(View.VISIBLE);
                saveAssistance(Assistance.MAYBE);
            }
        });

        refreshInfo();

    }

    private void refreshInfo(){
        TextView title = (TextView) findViewById(R.id.event_title);
        TextView description = (TextView) findViewById(R.id.event_description);
        TextView location = (TextView) findViewById(R.id.event_location);
        TextView weekday = (TextView) findViewById(R.id.event_weekday);
        TextView date = (TextView) findViewById(R.id.event_date);
        TextView time = (TextView) findViewById(R.id.event_time);
        TextView participants = (TextView) findViewById(R.id.event_participants);
        View locationButton = findViewById(R.id.location_button);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.co.in/maps?q=" + event.getLocation();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });

        SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
        SimpleDateFormat completeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("es", "ES"));


        Date optionDate = null;
        try {
            optionDate = completeFormat.parse(event.getDate());
            title.setText(event.getTitle());
            description.setText(event.getDescription());
            location.setText(event.getLocation());
            weekday.setText(StringUtils.upperCase(dayFormat.format(optionDate).substring(0,3)));
            date.setText(dayMonthFormat.format(optionDate));
            time.setText(event.getTime());
            String users = event.getConfirmedUsers().size() >= 10 ? String.valueOf(event.getConfirmedUsers().size()) : 0 + String.valueOf(event.getConfirmedUsers().size());
            participants.setText(users);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            yesButton.setBackgroundColor(getColor(R.color.confirmButton));
            noButton.setBackgroundColor(getColor(R.color.confirmButton));
            maybeButton.setBackgroundColor(getColor(R.color.confirmButton));
        }

        yesButton.setClickable(true);
        noButton.setClickable(true);
        maybeButton.setClickable(true);

        if (event.getConfirmedUsers().contains(user)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                yesButton.setBackgroundColor(getColor(R.color.colorPrimary));
                yesButton.setClickable(false);
            }
        } else if (event.getNotGoingUsers().contains(user)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                noButton.setBackgroundColor(getColor(R.color.colorPrimary));
                noButton.setClickable(false);
            }
        } else if (event.getDoNotKnowUsers().contains(user)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                maybeButton.setBackgroundColor(getColor(R.color.colorPrimary));
                maybeButton.setClickable(false);
            }
        }

        userAdapter.setItems(event.getConfirmedUsers());
        userList.removeAllViews();
        userAdapter.notifyItemRangeRemoved(0, userAdapter.getItemCount());
        userAdapter.notifyItemRangeInserted(0, userAdapter.getItemCount());
        userAdapter.notifyDataSetChanged();

    }

    void saveAssistance(Assistance assistance){
        eventService.saveAssistance(userId, eventId, assistance, this);
    }

    public void assistanceSaved(boolean b) {
        progressBar.setVisibility(View.INVISIBLE);
        blur.setVisibility(View.INVISIBLE);
        if (b) {
            event = eventService.getEvent(eventId);
            refreshInfo();
        }
    }
}
