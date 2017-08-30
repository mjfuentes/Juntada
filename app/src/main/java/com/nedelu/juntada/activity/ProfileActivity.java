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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.nedelu.juntada.R;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.UserService;
import com.nedelu.juntada.transform.CircleTransform;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private UserService userService;
    private User user;
    private Long userId;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userService = new UserService(ProfileActivity.this);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = userPref.getLong("userId", 0L);
        user = userService.getUser(userId);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }

        TextView name = (TextView) findViewById(R.id.user_name);
        ImageView imageView = (ImageView) findViewById(R.id.user_profile_photo);
        name.setText(user.getFirstName() + " " + user.getLastName());
        Picasso.with(this).load(user.getImageUrl()).transform(new CircleTransform()).into(imageView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView user_name = (TextView) headerView.findViewById(R.id.user_name);
        user_name.setText(user.getFirstName() + " " + user.getLastName());

        ImageView user_image = (ImageView) headerView.findViewById(R.id.user_image);
        Picasso.with(ProfileActivity.this).load(user.getImageUrl()).into(user_image);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.profile);
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
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
            /*Intent events = new Intent(this, SettingsActivity.class);
            events.putExtra("id", userId);
            startActivity(events);*/
            Toast.makeText(getApplicationContext(), "Proximamente!", Toast.LENGTH_SHORT).show();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;

        } else if (id == R.id.share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Juntada");
                String sAux = "\nProbá Juntada para Android, disponible en: \n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.nedelu.juntada \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Elegir aplicacion"));
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
}
