package com.nedelu.juntada.activity;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.PollOptionAdapter;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollOptionVote;
import com.nedelu.juntada.model.VotingItem;
import com.nedelu.juntada.model.dto.PollVoteRequest;
import com.nedelu.juntada.service.EventService;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class VoteActivity extends AppCompatActivity implements PollOptionAdapter.VoteListener, EventService.ResultListener {

    private RecyclerView optionsList;
    private PollOptionAdapter adapter;
    private Poll poll;
    private Long userId;
    private List<VotingItem> votingItems;
    private EventService eventService;
    private ProgressBar progressBar;
    private Boolean isCreator;
    private TextView buttonText;
    private View button;
    private TextView selectText;
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        Long pollId = userPref.getLong("pollId", 0L);
        userId = userPref.getLong("userId", 0L);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            pollId = bundle.getLong("pollId");
        }

        eventService = new EventService(VoteActivity.this);
        poll = eventService.getPoll(pollId);
        buttonText = (TextView) findViewById(R.id.voting_button);
        button = findViewById(R.id.button);
        selectText = (TextView) findViewById(R.id.select_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        optionsList = (RecyclerView) findViewById(R.id.options_list);
        optionsList.setLayoutManager(new LinearLayoutManager(VoteActivity.this));

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (toolbarLayout != null) {
                        isShow = true;
                    }
                } else if (isShow) {
                    if (toolbarLayout != null) {
                        isShow = false;
                    }
                }
            }
        });
        poll = eventService.getPoll(pollId);

        if (poll != null){
            refreshPoll(poll);
        } else {
            eventService.loadPoll(pollId, VoteActivity.this);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void refreshPoll(final Poll poll){
        progressBar.setVisibility(View.GONE);

        if (poll != null) {
            List<PollOption> options = new ArrayList<>(poll.getOptions());
            List<VotingItem> items = new ArrayList<>();

            View locationButton = findViewById(R.id.location_button);

            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String map = "http://maps.google.co.in/maps?q=" + poll.getLocation();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(i);
                }
            });

            toolbarLayout.setTitle(StringEscapeUtils.unescapeJava(poll.getTitle()));

            isCreator = poll.getCreator().getId().equals(userId);
            if (isCreator) {
                selectText.setText(R.string.choose_an_option);
                button.setVisibility(View.INVISIBLE);
                buttonText.setText(R.string.confirm_event);
            }
            for (PollOption option : options) {
                VotingItem item = new VotingItem(option);
                if (!isCreator) {
                    if (option.isVotedByUser(userId)) {
                        item.setVoted(true);
                        buttonText.setText(R.string.vote);
                    }
                }
                items.add(item);
            }

            TextView pollDescription = (TextView) findViewById(R.id.poll_description);
            pollDescription.setText(StringEscapeUtils.unescapeJava(poll.getDescription()));

            TextView pollLocation = (TextView) findViewById(R.id.poll_location);
            pollLocation.setText(poll.getLocation());

            TextView pollVotes = (TextView) findViewById(R.id.votes);
            Set<Long> users = new HashSet<>();
            for (PollOption option : poll.getOptions()) {
                for (PollOptionVote vote : option.getVotes()) {
                    users.add(vote.getUser().getId());
                }
            }
            pollVotes.setText(String.valueOf(users.size()));

            adapter = new PollOptionAdapter(VoteActivity.this, items, optionsList, this, isCreator);
            optionsList.setAdapter(adapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button.setClickable(false);
                    if (isCreator) {
                        for (VotingItem item : adapter.getItems()) {
                            if (item.getVoted()) {
                                showLocationDialog(item);
                                break;
                            }
                        }
                    } else {
                        List<Long> options = new ArrayList<Long>();
                        for (VotingItem item : adapter.getItems()) {
                            if (item.getVoted()) {
                                options.add(item.getOption().getId());
                            }
                        }
                        if (options.size() > 0) {
                            PollVoteRequest request = new PollVoteRequest();
                            request.setOptions(options);
                            request.setUserId(userId);
                            eventService.votePoll(poll.getId(), request, VoteActivity.this);
                            progressBar.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.votes_saved, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void itemVoted(VotingItem item) {
        if (isCreator) {
            if (item.getVoted()) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.INVISIBLE);
            }
        } else {
            if (item.getVoted()) {
                buttonText.setText(R.string.vote);
            } else {
                boolean anyvotes = false;
                for (VotingItem vItem : adapter.getItems()) {
                    if (vItem.getVoted()) anyvotes = true;
                }
                if (!anyvotes) {
                    buttonText.setText(R.string.no_date);
                }
            }
        }
    }

    @Override
    public void pollVoted(Boolean result, Long eventId) {
        button.setClickable(true);
        progressBar.setVisibility(View.INVISIBLE);
        if (result) {
            if (isCreator) {
                Intent event = new Intent(VoteActivity.this, EventActivity.class);
                SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = userPref.edit();
                editor.putLong("eventId", eventId);
                editor.apply();
                startActivity(event);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.votes_saved, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showLocationDialog(final VotingItem item) {

        try {
            SimpleDateFormat dayMonthFormat = new SimpleDateFormat(getString(R.string.day_month), Locale.ENGLISH);
            SimpleDateFormat completeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale(getString(R.string.locale), getString(R.string.country)));
            Date optionDate = completeFormat.parse(item.getOption().getDate());

            AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
            builder.setTitle(R.string.confirm_event_no_caps);
            builder.setMessage(getString(R.string.confirm_for_day) + " " + dayMonthFormat.format(optionDate) + " " + getString(R.string.and_time) + " " + item.getOption().getTime() + "?");
            builder.setPositiveButton(R.string.confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE);
                            eventService = new EventService(VoteActivity.this);
                            eventService.confirmEvent(poll, item.getOption(), VoteActivity.this);
                        }
                    });

            String negativeText = getString(R.string.cancel);
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            button.setClickable(true);
                        }
                    });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    button.setClickable(true);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
