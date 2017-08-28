package com.nedelu.juntada.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.UserAdapter;
import com.nedelu.juntada.fragment.EventFragment;
import com.nedelu.juntada.fragment.PollFragment;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.GroupService;
import com.nedelu.juntada.service.UserService;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

public class GroupTabbedActivity extends AppCompatActivity
        implements UserAdapter.ClickListener, TokenResultActivity, EventFragment.OnListFragmentInteractionListener, PollFragment.OnListFragmentInteractionListener {


    private Long userId;
    private Long groupId;
    private String image_url;
    private UserService userService;
    private UserAdapter userAdapter;
    private RecyclerView userList;
    private GroupService groupService;
    private PagerAdapter mEventPagerAdapter;
    private Group group;
    private FloatingActionButton addEventButton;
    private User user;
    private NestedScrollView scrollView;
    private ProgressBar progressBar;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageView addMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }
//
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
//        swipeRefreshLayout.setOnRefreshListener(this);

        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        groupId =  userPref.getLong("groupId", 0L);
        image_url =  userPref.getString("image_url", "");

        userService = new UserService(GroupTabbedActivity.this);
        user = userService.getUser(userId);

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        View headerView = navigationView.getHeaderView(0);
//        TextView user_name = (TextView) headerView.findViewById(R.id.user_name);
//        user_name.setText(user.getFirstName() + " " + user.getLastName());

//        ImageView user_image = (ImageView) headerView.findViewById(R.id.user_image);
//        Picasso.with(GroupTabbedActivity.this).load(user.getImageUrl()).into(user_image);
//
        groupService = GroupService.getInstance(GroupTabbedActivity.this);
        group = groupService.getGroup(groupId);
        getSupportActionBar().setTitle(StringEscapeUtils.unescapeJava(group.getName()));
//
//        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        ImageView groupImageView = (ImageView) findViewById(R.id.collapsing_group_image);
        Picasso.with(GroupTabbedActivity.this).load(group.getImageUrl()).into(groupImageView);

        userList = (RecyclerView) findViewById(R.id.userList);
        userAdapter = new UserAdapter(GroupTabbedActivity.this, group.getUsers(), userList);
        userAdapter.setOnItemClickListener(this);

        userList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userList.setAdapter(userAdapter);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();


        mEventPagerAdapter = new GroupTabbedActivity.SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        addEventButton = (FloatingActionButton) findViewById(R.id.add_event);
        mViewPager.setAdapter(mEventPagerAdapter);

//        blur = findViewById(R.id.blur_background);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(GroupTabbedActivity.this, NewEventActivity.class);
                main.putExtra("userId", userId);
                main.putExtra("groupId", groupId);
                startActivity(main);
            }
        });

        addMember = (ImageButton) findViewById(R.id.add_members_button);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMember.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                groupService.getGroupToken(groupId, GroupTabbedActivity.this);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (group.getCreator().getId().equals(userId)) {
            getMenuInflater().inflate(R.menu.group_admin, menu);
        } else {
            getMenuInflater().inflate(R.menu.group, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.leave_group) {
            groupService.deleteGroup(userId, group, GroupTabbedActivity.this);
            addEventButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        if (id == R.id.delete_group) {
            progressBar.setVisibility(View.VISIBLE);
            addEventButton.setVisibility(View.INVISIBLE);
            groupService.deleteGroup(userId, group, GroupTabbedActivity.this);
        }

        if (id == R.id.notifications) {
            Intent notifications = new Intent(this, NotificationsActivity.class);
            startActivity(notifications);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListFragmentInteraction(Event item) {
        Intent event = new Intent(GroupTabbedActivity.this, EventActivity.class);
        SharedPreferences userPref = getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("eventId", item.getId());
        editor.apply();
        startActivity(event);
    }

    @Override
    public void onListFragmentInteraction(Poll item) {
        Intent vote = new Intent(GroupTabbedActivity.this, VoteActivity.class);
        SharedPreferences userPref = getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("pollId", item.getId());
        editor.apply();
        startActivity(vote);
    }



    public void groupDeleted(Boolean deleted){
        if (deleted) {
            finish();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            addEventButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void tokenGenerated(String token) {
        progressBar.setVisibility(View.INVISIBLE);
        addMember.setClickable(true);
        if (token != null) {
            String url = "http://www.juntada.nedelu.com/joinGroup/" + token;

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Juntada");
            String sAux = "\nTe invite a mi grupo de Juntada! Para ingresar usa el siguiente link:\n\n";
            sAux += "\n" + url;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Elegir aplicacion"));
        }
    }

//    @Override
//    public void onRefresh() {
//        groupService.loadGroup(groupId,GroupTabbedActivity.this);
//    }

    @Override
    public void onUserClicked(int position, View v) {
        Intent profile = new Intent(GroupTabbedActivity.this, ProfileActivity.class);
        profile.putExtra("id", userAdapter.getItemId(position));
        startActivity(profile);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return EventFragment.newInstance(GroupTabbedActivity.this);
                case 1:
                    return PollFragment.newInstance(GroupTabbedActivity.this);
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
            switch (position) {
                case 0:
                    return "Juntadas";
                case 1:
                    return "Encuestas";
            }
            return null;
        }
    }

    public void refreshGroup(Boolean result){
        groupService.loadGroup(groupId,GroupTabbedActivity.this);

        if (result) {
            this.group = groupService.getGroup(groupId);

            userAdapter.setItems(group.getUsers());
            userList.removeAllViews();
            userAdapter.notifyItemRangeRemoved(0, userAdapter.getItemCount());
            userAdapter.notifyItemRangeInserted(0, userAdapter.getItemCount());
            userAdapter.notifyDataSetChanged();

            mEventPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            try {
                mViewPager.setAdapter(mEventPagerAdapter);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
