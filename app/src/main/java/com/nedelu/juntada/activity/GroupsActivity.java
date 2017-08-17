package com.nedelu.juntada.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.GroupAdapter;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.GroupService;
import com.nedelu.juntada.service.UserService;
import com.nedelu.juntada.util.SpacesItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity implements GroupService.Callbacks, NavigationView.OnNavigationItemSelectedListener, GroupAdapter.ClickListener {

    private GroupAdapter groupAdapter;
    private RecyclerView recyclerView;
    private GroupService groupService;
    private UserService userService;
    private Long userId;
    ImageView firstGroup;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(GroupsActivity.this, NewGroupActivity.class);
                main.putExtra("id", userId);
                startActivity(main);
            }
        });
        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        userService = new UserService(GroupsActivity.this);
        user = userService.getUser(userId);
        firstGroup = (ImageView) findViewById(R.id.first_group);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
        if (groups.size() == 0){
            firstGroup.setVisibility(View.VISIBLE);
        }

        groupAdapter = new GroupAdapter(GroupsActivity.this, groups);
        groupAdapter.setOnItemClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.groups_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(groupAdapter);

        groupService.registerClient(this);
        groupService.loadGroups(userId);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.groups);

    }

    public void updateGroups(){
        List<Group> groups = groupService.getUserGroups(userId);
        if (groupAdapter.getItemCount() != groups.size()) {
            firstGroup.setVisibility(View.INVISIBLE);
            groupAdapter.setData(groups);
            recyclerView.removeAllViews();
            groupAdapter.notifyItemRangeRemoved(0, groupAdapter.getItemCount());
            groupAdapter.notifyItemRangeInserted(0, groupAdapter.getItemCount());
            groupAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Group> groups = groupService.getUserGroups(userId);
        if (groupAdapter.getItemCount() != groups.size()) {
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
            // Handle the camera action
        } else if (id == R.id.groups) {

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
    public void onItemClick(int position, View v) {
        Intent main = new Intent(GroupsActivity.this, GroupActivity.class);
        Group group = groupAdapter.getItem(position);
        SharedPreferences userPref = getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("userId", userId);
        editor.putLong("groupId", group.getId());
        editor.putString("image_url", group.getImageUrl());
        editor.apply();
        startActivity(main);
    }
}
