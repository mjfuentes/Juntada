package com.nedelu.juntada.activity;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.lang3.StringEscapeUtils;

public class GroupTabbedActivity extends AppCompatActivity
        implements UserAdapter.ClickListener, TokenResultActivity, EventFragment.OnListFragmentInteractionListener, PollFragment.OnListFragmentInteractionListener {


    private Long userId;
    private Long groupId;
    private String image_url;
    private CollapsingToolbarLayout collapsingToolbarLayout;
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
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageView addMember;
    private EventFragment eventFragment;
    private PollFragment pollFragment;
    private ImageView groupImage;

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
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
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

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = userPref.getLong("userId", 0L);
        groupId =  userPref.getLong("groupId", 0L);
        image_url =  userPref.getString("image_url", "");

        userService = new UserService(GroupTabbedActivity.this);
        user = userService.getUser(userId);

        groupService = GroupService.getInstance(GroupTabbedActivity.this);
        group = groupService.getGroup(groupId);
        getSupportActionBar().setTitle(StringEscapeUtils.unescapeJava(group.getName()));

        if (group.getCreator().getId().equals(userId)){
            ImageView editName = (ImageView) findViewById(R.id.edit_group_name);
            editName.setVisibility(View.VISIBLE);
            editName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupTabbedActivity.this, R.style.DialogTheme);
                    builder.setTitle("Editar nombre");

                    final EditText input = new EditText(GroupTabbedActivity.this);
                    input.setText(StringEscapeUtils.unescapeJava(group.getName()));
                    input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    input.setLines(1);
                    input.setMaxLines(1);

                    FrameLayout container = new FrameLayout(GroupTabbedActivity.this);
                    FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 30;
                    params.rightMargin = 30;
                    input.setLayoutParams(params);

                    int maxLength = 20;
                    input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                    container.addView(input);
                    builder.setView(container);

                    builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            progressBar.setVisibility(View.VISIBLE);
                            groupService.updateGroupName(groupId, StringEscapeUtils.escapeJava(input.getText().toString()), GroupTabbedActivity.this);
                        }
                    });

                    builder.setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }

        groupImage = (ImageView) findViewById(R.id.collapsing_group_image);
        Picasso.with(GroupTabbedActivity.this).load(group.getImageUrl()).into(groupImage);

        userList = (RecyclerView) findViewById(R.id.userList);
        userAdapter = new UserAdapter(GroupTabbedActivity.this, group.getUsers(), userList);
        userAdapter.setOnItemClickListener(this);

        userList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userList.setAdapter(userAdapter);
        mEventPagerAdapter = new GroupTabbedActivity.SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        addEventButton = (FloatingActionButton) findViewById(R.id.add_event);
        mViewPager.setAdapter(mEventPagerAdapter);

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

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(4,3)
                        .setRequestedSize(800,600)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(GroupTabbedActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        groupService.loadGroupData(groupId, this);
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                Uri imageUri = result.getUri();
                Picasso.with(GroupTabbedActivity.this).load(imageUri).into(groupImage);
                groupService.updateGroupImage(groupId,imageUri, GroupTabbedActivity.this);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("eventId", item.getId());
        editor.apply();
        startActivity(event);
    }

    @Override
    public void onListFragmentInteraction(Poll item) {
        Intent vote = new Intent(GroupTabbedActivity.this, VoteActivity.class);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
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

    public void onRefresh() {
        groupService.loadGroup(groupId,GroupTabbedActivity.this);
    }

    @Override
    public void onUserClicked(int position, View v) {
        Intent profile = new Intent(GroupTabbedActivity.this, ProfileActivity.class);
        profile.putExtra("id", userAdapter.getItemId(position));
        startActivity(profile);
    }

    public void updateName(String name) {
        progressBar.setVisibility(View.GONE);
        if (name  !=null){
            group.setName(name);
            collapsingToolbarLayout.setTitle(StringEscapeUtils.unescapeJava(group.getName()));;
        }
    }

    public void imageUpdated(String imageUrl) {
        progressBar.setVisibility(View.GONE);
        if (imageUrl != null){

        } else {
            Picasso.with(GroupTabbedActivity.this).load(group.getImageUrl()).into(groupImage);
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    eventFragment = EventFragment.newInstance(GroupTabbedActivity.this);
                    return eventFragment;
                case 1:
                    pollFragment = PollFragment.newInstance(GroupTabbedActivity.this);
                    return pollFragment;
                default:
                    return EventFragment.newInstance(GroupTabbedActivity.this);
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

        public void refresh() {
            pollFragment.refresh();
            eventFragment.refresh();
        }
    }

    public void refreshGroup(Boolean result){
//        groupService.loadGroup(groupId,GroupTabbedActivity.this);

        if (result) {
            this.group = groupService.getGroup(groupId);

            userAdapter.setItems(group.getUsers());
            userList.removeAllViews();
            userAdapter.notifyItemRangeRemoved(0, userAdapter.getItemCount());
            userAdapter.notifyItemRangeInserted(0, userAdapter.getItemCount());
            userAdapter.notifyDataSetChanged();

            mSectionsPagerAdapter.refresh();
        }
    }
}
