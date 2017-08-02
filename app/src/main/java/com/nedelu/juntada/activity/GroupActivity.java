package com.nedelu.juntada.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.GroupAdapter;
import com.nedelu.juntada.adapter.UserAdapter;
import com.nedelu.juntada.fragment.EventFragment;
import com.nedelu.juntada.fragment.EventUnconfirmedFragment;
import com.nedelu.juntada.fragment.dummy.DummyContent;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.pager.EventPager;
import com.nedelu.juntada.service.GroupService;
import com.nedelu.juntada.service.UserService;
import com.nedelu.juntada.util.SpacesItemDecoration;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ObservableScrollViewCallbacks, EventFragment.OnListFragmentInteractionListener, EventUnconfirmedFragment.OnListFragmentInteractionListener {
    private Long userId;
    private Long groupId;
    private String image_url;
    private UserService userService;
    private UserAdapter userAdapter;
    private RecyclerView userList;
    private GroupService groupService;
    private PagerAdapter mEventPagerAdapter;
    private EventPager mEventPager;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        groupId =  userPref.getLong("groupId", 0L);
        image_url =  userPref.getString("image_url", "");

        groupService = GroupService.getInstance(GroupActivity.this);
        group = groupService.getGroupData(groupId);

        ImageView groupImageView = (ImageView) findViewById(R.id.group_image);
        Picasso.with(GroupActivity.this).load(group.getImageUrl()).into(groupImageView);

        TextView groupNameText = (TextView) findViewById(R.id.group_name);
        groupNameText.setText(StringEscapeUtils.unescapeJava(group.getName()));

        userList = (RecyclerView) findViewById(R.id.userList);
        userAdapter = new UserAdapter(GroupActivity.this, group.getUsers(), userList);

        userList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userList.setAdapter(userAdapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        userList.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mEventPagerAdapter = new EventPagerAdapter(getSupportFragmentManager(), GroupActivity.this);

        mEventPager = (EventPager) findViewById(R.id.pager);
        mEventPager.setAdapter(mEventPagerAdapter);
        mEventPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {

                if (position == 0) {
                    PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                    pagerTabStrip.setDrawFullUnderline(true);
                    pagerTabStrip.setTextColor(getResources().getColor(R.color.colorPrimary));
                    pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                    pagerTabStrip.setDrawFullUnderline(true);
                    pagerTabStrip.setTextColor(getResources().getColor(R.color.colorAccent));
                    pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

        ImageButton addMember = (ImageButton) findViewById(R.id.add_members_button);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo get token from server
                String GROUP_TOKEN = "ABCD1234";

                String url = "http://10.1.1.16:8080/joinGroup/" + GROUP_TOKEN;

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Juntada");
                String sAux = "\nTe invite a mi Grupo de Juntada! Para ingresar usa el siguiente link:\n\n";
                sAux += "\n"+ url + "\n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Elegir aplicacion"));
            }
        });
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
        getMenuInflater().inflate(R.menu.group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_event) {
            Intent main = new Intent(GroupActivity.this, NewEventActivity.class);
            main.putExtra("userId", userId);
            main.putExtra("groupId", groupId);
            startActivity(main);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    @Override
    public void onListFragmentInteraction(Event item) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    public class EventPagerAdapter extends FragmentPagerAdapter {

        private Context context;
        public EventPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return EventFragment.newInstance(groupService.getEvents(groupId));
                case 1:
                    return new EventUnconfirmedFragment();
                default:
                    return new EventFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Proximas juntadas";
                case 1:
                    return "Juntadas sin confirmar";
                default:
                    return "ERROR";
            }
        }
    }
}
