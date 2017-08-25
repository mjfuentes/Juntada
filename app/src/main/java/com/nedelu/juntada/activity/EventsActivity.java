package com.nedelu.juntada.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.nedelu.juntada.R;
import com.nedelu.juntada.fragment.EventFragment;
import com.nedelu.juntada.fragment.MyEventRecyclerViewAdapter;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.EventService;

public class EventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventFragment.OnListFragmentInteractionListener {

    private EventService eventService;
    private RecyclerView eventsList;
    private MyEventRecyclerViewAdapter eventsAdapter;
    private Long userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invitaciones");
        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        eventService = new EventService(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.events);

        eventsList = (RecyclerView) findViewById(R.id.events_list);
        eventsList.setAdapter(new MyEventRecyclerViewAdapter(eventService.getEventsForUser(userId), EventsActivity.this));
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent profile = new Intent(EventsActivity.this, ProfileActivity.class);
            profile.putExtra("id", userId);
            startActivity(profile);
            finish();
        } else if (id == R.id.groups) {
            Intent groups = new Intent(EventsActivity.this, GroupsActivity.class);
            groups.putExtra("id", userId);
            startActivity(groups);
            finish();
        } else if (id == R.id.events) {
//            Intent events = new Intent(GroupsActivity.this, EventsActivity.class);
//            events.putExtra("id", userId);
//            startActivity(events);
        } else if (id == R.id.configuration) {

        } else if (id == R.id.share) {
//            Intent i = new Intent(Intent.ACTION_SEND);
//            i.setType("text/plain");
//            i.putExtra(Intent.EXTRA_SUBJECT, "Juntada");
//            String sAux = "\nTe invite a mi Grupo de Juntada! Para ingresar usa el siguiente link:\n\n";
//            sAux += "\n"+ url + "\n\n";
//            i.putExtra(Intent.EXTRA_TEXT, sAux);
//            startActivity(Intent.createChooser(i, "Elegir aplicacion"));
        } else if (id == R.id.about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Event item) {
        Intent event = new Intent(EventsActivity.this, EventActivity.class);
        SharedPreferences userPref = getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("eventId", item.getId());
        editor.apply();
        startActivity(event);
    }
}
