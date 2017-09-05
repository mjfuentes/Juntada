package com.nedelu.juntada.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.GroupAdapter;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.GroupService;
import com.nedelu.juntada.service.UserService;
import com.nedelu.juntada.util.SpacesItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, GroupService.GroupsLoadedListener, NavigationView.OnNavigationItemSelectedListener, GroupAdapter.ClickListener {

    private GroupAdapter groupAdapter;
    private RecyclerView recyclerView;
    private GroupService groupService;
    private UserService userService;
    private EventService eventService;
    private Long userId;
    private NavigationView navigationView;
    private ImageView firstGroup;
    private User user;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Boolean pendingIntroAnimation = false;
    private FloatingActionButton fab;
    private MenuItem notificationsMenuItem;
    private Toolbar toolbar;
    private ImageView logo;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        logo = (ImageView) findViewById(R.id.logo);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(GroupsActivity.this, NewGroupActivity.class);
                main.putExtra("id", userId);
                startActivity(main);
            }
        });


        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = userPref.getLong("userId", 0L);
        userService = new UserService(GroupsActivity.this);
        eventService = new EventService(GroupsActivity.this);
        user = userService.getUser(userId);
        firstGroup = (ImageView) findViewById(R.id.first_group);
        recyclerView = (RecyclerView) findViewById(R.id.groups_view);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView user_name = (TextView) headerView.findViewById(R.id.user_name);
        user_name.setText(user.getFirstName() + " " + user.getLastName());

        ImageView user_image = (ImageView) headerView.findViewById(R.id.user_image);
        Picasso.with(GroupsActivity.this).load(user.getImageUrl()).into(user_image);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        groupService = GroupService.getInstance(GroupsActivity.this);

        List<Group> groups = groupService.getUserGroups(userId);
        Collections.sort(groups, new Comparator<Group>() {
            @Override
            public int compare(Group group, Group t1) {
                return t1.getId().compareTo(group.getId());
            }
        });

        if (groups.size() == 0){
            firstGroup.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.groups);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        setupGroups();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)){
            String[] parts = intent.getDataString().split("/");
            String path =  parts[parts.length-2];
            String token = parts[parts.length-1];
            if (path.equals("joinGroup")) {
                groupService.joinGroup(userId, token, GroupsActivity.this);
             } else if (path.equals("joinEvent")){
                eventService.joinEvent(userId, token, GroupsActivity.this);
            }
        } else {
            if (intent.getExtras() != null){
                Boolean load = intent.getBooleanExtra("reload",false);
                if (load){
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                }
            }
        }
    }

    public void updateGroups(Boolean result){
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

        if (result) {
            List<Group> groups = groupService.getUserGroups(userId);
            if (groups.size() == 0){
                firstGroup.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                firstGroup.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Collections.sort(groups, new Comparator<Group>() {
                    @Override
                    public int compare(Group group, Group t1) {
                        return t1.getId().compareTo(group.getId());
                    }
                });
                firstGroup.setVisibility(View.INVISIBLE);
                recyclerView.removeAllViews();
                groupAdapter.notifyItemRangeRemoved(0, groupAdapter.getItemCount());
                groupAdapter.notifyItemRangeInserted(0, groupAdapter.getItemCount());
                groupAdapter.notifyDataSetChanged();
            }

        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups, menu);
        notificationsMenuItem = menu.findItem(R.id.notifications);
        notificationsMenuItem.setActionView(R.layout.notifications_item);
        notificationsMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifications = new Intent(GroupsActivity.this, NotificationsActivity.class);
                startActivity(notifications);
            }
        });
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        
        return true;
    }

    private void startIntroAnimation() {
        fab.setTranslationY(2 * 50);

        int actionbarSize = (int) (56 * getResources().getDisplayMetrics().density);
        toolbar.setTranslationY(-actionbarSize);
        logo.setTranslationY(-actionbarSize);
        notificationsMenuItem.getActionView().setTranslationY(-actionbarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        logo.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);

        notificationsMenuItem.getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fab.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        recyclerView.setAdapter(groupAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.groups);
        List<Group> groups = groupService.getUserGroups(userId);
        if (groups.size() == 0){
            groupService.loadGroups(userId, this);
            firstGroup.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            firstGroup.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Collections.sort(groups, new Comparator<Group>() {
                @Override
                public int compare(Group group, Group t1) {
                    return t1.getId().compareTo(group.getId());
                }
            });
            firstGroup.setVisibility(View.INVISIBLE);
            groupAdapter.setData(groups);
            recyclerView.removeAllViews();
            groupAdapter.notifyItemRangeRemoved(0, groupAdapter.getItemCount());
            groupAdapter.notifyItemRangeInserted(0, groupAdapter.getItemCount());
            groupAdapter.notifyDataSetChanged();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent profile = new Intent(this, ProfileActivity.class);
            profile.putExtra("id", userId);
            startActivity(profile);
        } else if (id == R.id.groups) {

        } else if (id == R.id.events) {
            Intent events = new Intent(GroupsActivity.this, EventsActivity.class);
            events.putExtra("id", userId);
            startActivity(events);
        } else if (id == R.id.configuration) {
            Intent config = new Intent(this, SettingsActivity.class);
            config.putExtra("id", userId);
            startActivity(config);

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

    @Override
    public void onItemClick(int position, View v) {
        Intent main = new Intent(GroupsActivity.this, GroupTabbedActivity.class);
        Group group = groupAdapter.getItem(position);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("userId", userId);
        editor.putLong("groupId", group.getId());
        editor.putString("image_url", group.getImageUrl());
        editor.apply();
        startActivity(main);
    }

    @Override
    public void onRefresh() {
        groupService.loadGroups(userId, this);
    }

    private void setupGroups() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        groupAdapter = new GroupAdapter(GroupsActivity.this, groupService.getUserGroups(userId));
        groupAdapter.setOnItemClickListener(this);
    }
}
