package com.nedelu.juntada.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.UserAdapter;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.GroupService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
        SimpleDateFormat completeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("es", "ES"));

        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        eventId = userPref.getLong("eventId", 0L);

        eventService = new EventService(EventActivity.this);
        event = eventService.getEvent(eventId);
        getSupportActionBar().setTitle(event.getGroup().getName());

        userList = (RecyclerView) findViewById(R.id.userList);
        userAdapter = new UserAdapter(EventActivity.this, event.getConfirmedUsers(), userList);
        userList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        userList.setAdapter(userAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        userList.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        TextView title = (TextView) findViewById(R.id.event_title);
        TextView description = (TextView) findViewById(R.id.event_description);
        TextView location = (TextView) findViewById(R.id.event_location);
        TextView weekday = (TextView) findViewById(R.id.event_weekday);
        TextView date = (TextView) findViewById(R.id.event_date);
        TextView time = (TextView) findViewById(R.id.event_time);
        TextView participants = (TextView) findViewById(R.id.event_participants);

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

    }
}
