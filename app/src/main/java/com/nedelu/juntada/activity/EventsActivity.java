package com.nedelu.juntada.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.nedelu.juntada.R;
import com.nedelu.juntada.fragment.EventFragment;
import com.nedelu.juntada.fragment.MyEventRecyclerViewAdapter;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.UserService;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventFragment.OnListFragmentInteractionListener {

    private EventService eventService;
    private RecyclerView eventsList;
    private View noEvents;
    private MyEventRecyclerViewAdapter eventsAdapter;
    private Long userId;
    private User user;
    private UserService userService;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.invitations);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = userPref.getLong("userId", 0L);
        eventService = new EventService(this);
        userService = new UserService(this);
        user = userService.getUser(userId);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.events);
        View headerView = navigationView.getHeaderView(0);
        TextView user_name = (TextView) headerView.findViewById(R.id.user_name);
        user_name.setText(user.getFirstName() + " " + user.getLastName());

        ImageView user_image = (ImageView) headerView.findViewById(R.id.user_image);
        Picasso.with(EventsActivity.this).load(user.getImageUrl()).into(user_image);

        List<Event> events = eventService.loadEventsForUser(userId,this);
        eventsAdapter = new MyEventRecyclerViewAdapter(events, EventsActivity.this);
        eventsList = (RecyclerView) findViewById(R.id.events_list);
        noEvents = findViewById(R.id.noInvitations);
        eventsList.setAdapter(eventsAdapter);

        if (events.size() == 0){
            eventsList.setVisibility(View.GONE);
            noEvents.setVisibility(View.VISIBLE);
        }

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }
    }

    public void refreshEvents(List<Event> events){
        if (events != null){
            if (events.size() > 0) {
                eventsList.setVisibility(View.VISIBLE);
                noEvents.setVisibility(View.GONE);
                eventsAdapter.setItems(events);
                eventsAdapter.notifyDataSetChanged();
            } else {
                eventsList.setVisibility(View.GONE);
                noEvents.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events, menu);
        MenuItem notificationsMenuItem = menu.findItem(R.id.notifications);
        notificationsMenuItem.setActionView(R.layout.notifications_item);
        notificationsMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifications = new Intent(EventsActivity.this, NotificationsActivity.class);
                startActivity(notifications);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notifications) {
            Intent notifications = new Intent(this, NotificationsActivity.class);
            startActivity(notifications);
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent profile = new Intent(this, ProfileActivity.class);
            profile.putExtra("id", userId);
            startActivity(profile);
            finish();
        } else if (id == R.id.groups) {
            Intent groups = new Intent(this, GroupsActivity.class);
            groups.putExtra("id", userId);
            startActivity(groups);
            finish();
        } else if (id == R.id.events) {
            Intent events = new Intent(this, EventsActivity.class);
            events.putExtra("id", userId);
            startActivity(events);
            finish();
        } else if (id == R.id.configuration) {
            Intent events = new Intent(this, SettingsActivity.class);
            events.putExtra("id", userId);
            startActivity(events);
            return false;
        } else if (id == R.id.share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String sAux = getString(R.string.try_juntada);
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.nedelu.juntada \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, getString(R.string.choose_app)));
            } catch(Exception e) {
                //e.toString();
            }
        } else if (id == R.id.exit) {
            LoginManager.getInstance().logOut();
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Event item) {
        Intent event = new Intent(EventsActivity.this, EventActivity.class);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("eventId", item.getId());
        editor.apply();
        startActivity(event);
    }
}
