package com.nedelu.juntada.activity;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.UserAdapter;
import com.nedelu.juntada.fragment.EventMessagingFragment;
import com.nedelu.juntada.fragment.MessagingRootFragment;
import com.nedelu.juntada.fragment.NoMessagingFragment;
import com.nedelu.juntada.model.Assistance;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.GroupService;
import com.nedelu.juntada.service.UserService;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity implements UserAdapter.ClickListener {

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
    private User user;
    private Boolean openMessages;
    private Boolean notification = false;
    private UserService userService;
    private ImageView addMembersButton;
    private EventMessagingFragment eventFragment;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
//    private View buttons;
    private Boolean messagingLoaded = false;
    private Boolean toolbarExpanded = true;
    private AppBarLayout appBarLayout;
    private com.github.clans.fab.FloatingActionMenu fab;
    private com.github.clans.fab.FloatingActionButton going;
    private com.github.clans.fab.FloatingActionButton notGoing;



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

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = userPref.getLong("userId", 0L);
        eventId = userPref.getLong("eventId", 0L);
        openMessages = false;

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            eventId = bundle.getLong("eventId");
            openMessages = bundle.getBoolean("showMessages");
            notification = true;
        }

        userService = new UserService(this);
        user = userService.getUser(userId);
        eventService = new EventService(this);
        event = eventService.getEvent(eventId);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        addMembersButton = (ImageView) findViewById(R.id.add_members_button);
        userList = (RecyclerView) findViewById(R.id.userList);
        userAdapter = new UserAdapter(EventActivity.this, new ArrayList<User>(), userList);
        userAdapter.setOnItemClickListener(this);

        userList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        userList.setAdapter(userAdapter);


        blur = findViewById(R.id.blur_background);

        fab = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.fab);
        going = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.going);
        notGoing = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.not_going);

        going.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                saveAssistance(Assistance.GOING);
                fab.close(true);
            }
        });

        notGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                saveAssistance(Assistance.NOT_GOING);
                fab.close(true);
            }
        });
//        fab.add
//        yesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                blur.setVisibility(View.VISIBLE);
//                saveAssistance(Assistance.GOING);
//            }
//        });
//
//        noButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                blur.setVisibility(View.VISIBLE);
//                saveAssistance(Assistance.NOT_GOING);
//            }
//        });
//
//        maybeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                blur.setVisibility(View.VISIBLE);
//                saveAssistance(Assistance.MAYBE);
//            }
//        });

        addMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMembersButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                eventService.generateToken(eventId, EventActivity.this);
            }
        });

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/sketch_font.ttf");
//        collapsingToolbarLayout.setExpandedTitleTypeface(typeface);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Event event = eventService.getEvent(eventId);
        if (event != null){
            refreshInfo(event);
        }
    }


    public void refreshInfo(final Event event){
        invalidateOptionsMenu();
        progressBar.setVisibility(View.GONE);
//        TextView title = (TextView) findViewById(R.id.event_title);
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
//            title.setText(StringEscapeUtils.unescapeJava(event.getTitle()));
            description.setText(StringEscapeUtils.unescapeJava(event.getDescription()));
            location.setText(event.getLocation());
            weekday.setText(StringUtils.upperCase(dayFormat.format(optionDate).substring(0,3)));
            date.setText(dayMonthFormat.format(optionDate));
            time.setText(event.getTime());
            String users = event.getConfirmedUsers().size() >= 10 ? String.valueOf(event.getConfirmedUsers().size()) : 0 + String.valueOf(event.getConfirmedUsers().size());
            participants.setText(users);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        userAdapter.setItems(event.getConfirmedUsers());
        userList.removeAllViews();
        userAdapter.notifyItemRangeRemoved(0, userAdapter.getItemCount());
        userAdapter.notifyItemRangeInserted(0, userAdapter.getItemCount());
        userAdapter.notifyDataSetChanged();

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout.setTitle(StringEscapeUtils.unescapeJava(event.getTitle()));

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (collapsingToolbarLayout != null) {
                        toolbarExpanded = false;
                        isShow = true;
                        if (!messagingLoaded) {
                           mSectionsPagerAdapter.showMessages();
                            messagingLoaded = true;
                        }
                    }
                } else if (isShow) {
                    if (collapsingToolbarLayout != null) {
//                        collapsingToolbarLayout.setTitle(" ");
                        isShow = false;
                    }
                }

                if (verticalOffset == 0){
                    fab.setVisibility(View.VISIBLE);
                    if (mSectionsPagerAdapter != null) {
                        mSectionsPagerAdapter.hideMessages();
                    }
                    toolbarExpanded = true;
                    messagingLoaded = false;
                } else {
                    fab.setVisibility(View.GONE);
                }
            }
        });

        if (openMessages){
            appBarLayout.setExpanded(false);
            openMessages = false;
        }
    }

    void saveAssistance(Assistance assistance){
        eventService.saveAssistance(userId, eventId, assistance, this);
    }

    public void assistanceSaved(final Event e, Assistance assistance) {
        progressBar.setVisibility(View.INVISIBLE);
        blur.setVisibility(View.INVISIBLE);
        if (e != null) {
            if (assistance.equals(Assistance.GOING)){

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    saveToCalendar(e);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this, R.style.DialogTheme);
                builder.setMessage("Guardar evento en calendario?")
                        .setPositiveButton("SI", dialogClickListener)
                        .setNegativeButton("NO", dialogClickListener).show();

            }
            refreshInfo(e);

        }
    }

    private void saveToCalendar(Event e){
        if (e != null) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getTimeInMillis(e.getDate(), e.getTime()));
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getTimeInMillis(e.getDate(), e.getTime()) + (60 * 60 * 1000));
            intent.putExtra(CalendarContract.Events.TITLE, StringEscapeUtils.unescapeJava(e.getTitle()));
            intent.putExtra(CalendarContract.Events.DESCRIPTION, StringEscapeUtils.unescapeJava(e.getDescription()));
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, e.getLocation());
            startActivity(intent);
        }
    }

    public long getTimeInMillis(String date, String time){
        String completeDateString = date + " " + time;
        SimpleDateFormat completeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        try {
            Date completeDate = completeFormat.parse(completeDateString);
            return completeDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void tokenGenerated(String token) {
        progressBar.setVisibility(View.INVISIBLE);
        addMembersButton.setClickable(true);
        if (token != null) {
            String url = "http://www.juntada.nedelu.com/joinEvent/" + token;

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Juntada");
            String sAux = "\nTe invite a mi evento de Juntada! Para ingresar usa el siguiente link:\n\n";
            sAux += "\n" + url;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Elegir aplicacion"));
        }
    }


    @Override
    public void onUserClicked(int position, View v) {
        Intent profile = new Intent(EventActivity.this, VisitProfileActivity.class);
        profile.putExtra("id", userAdapter.getItemId(position));
        startActivity(profile);
    }

    @Override
    public void onBackPressed() {
        if (toolbarExpanded){
            this.finish();
        } else {
            if (appBarLayout != null){
                appBarLayout.setExpanded(true);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete){
            addMembersButton.setClickable(false);
//            yesButton.setClickable(false);
//            noButton.setClickable(false);
//            maybeButton.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            eventService.deleteEvent(eventId, this);
        }

        if (id == R.id.save) {
            saveToCalendar(event);
        }

        if (id ==android.R.id.home) {
            if (toolbarExpanded){
                this.finish();
            } else {
                if (appBarLayout != null){
                    appBarLayout.setExpanded(true);
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (event != null && event.getCreator()!= null && event.getCreator().getId().equals(userId)) {
            getMenuInflater().inflate(R.menu.event_admin, menu);
            menu.findItem(R.id.edit).getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EventActivity.this, NewEventActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                }
            });
        } else {
            getMenuInflater().inflate(R.menu.event, menu);
        }

        return true;
    }

    @Override
    public void onResume(){
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && !notification) {
            eventId = bundle.getLong("eventId");
            openMessages = bundle.getBoolean("showMessages");
        }

        Event event = eventService.getEvent(eventId);
        refreshInfo(event);
        notification = false;
        super.onResume();
    }

    public void eventDeleted(boolean b) {
        if (b){
            this.finish();
        }

        addMembersButton.setClickable(true);
//        yesButton.setClickable(true);
//        noButton.setClickable(true);
//        maybeButton.setClickable(true);
        progressBar.setVisibility(View.GONE);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        MessagingRootFragment messagingRootFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    messagingRootFragment = new MessagingRootFragment();
                    return messagingRootFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mensajes";
                case 1:
                    return "Gastos";
            }
            return null;
        }

        public void refresh() {
            eventFragment.refresh();
        }

        public void showMessages() {
            messagingRootFragment.showMessages(eventId, userId);
        }

        public void hideMessages() {
            messagingRootFragment.hideMessages();
        }
    }
}
