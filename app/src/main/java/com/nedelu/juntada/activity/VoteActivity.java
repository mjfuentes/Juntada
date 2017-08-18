package com.nedelu.juntada.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.PollOptionAdapter;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollOptionVote;
import com.nedelu.juntada.model.VotingItem;
import com.nedelu.juntada.model.dto.PollVoteRequest;
import com.nedelu.juntada.model.dto.VoteRequestDTO;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.util.SimpleDividerItemDecoration;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VoteActivity extends AppCompatActivity implements PollOptionAdapter.VoteListener, EventService.ResultListener {

    private RecyclerView optionsList;
    private PollOptionAdapter adapter;
    private Poll poll;
    private List<VotingItem> votingItems;
    private EventService eventService;
    private ProgressBar progressBar;
    private RelativeLayout blur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        SharedPreferences userPref = getSharedPreferences("user", 0);
        Long pollId = userPref.getLong("pollId", 0L);
        final Long userId = userPref.getLong("userId", 0L);
        final EventService eventService = new EventService(VoteActivity.this);
        poll = eventService.getPoll(pollId);
        blur = (RelativeLayout) findViewById(R.id.blur_background);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        optionsList = (RecyclerView) findViewById(R.id.options_list);
        optionsList.setLayoutManager(new LinearLayoutManager(VoteActivity.this));
        optionsList.addItemDecoration(new SimpleDividerItemDecoration(this));
        List<PollOption> options = new ArrayList<>(poll.getOptions());
        List<VotingItem> items = new ArrayList<>();
        TextView button = (TextView) findViewById(R.id.voting_button);

        for (PollOption option : options){
            VotingItem item = new VotingItem(option);
            if (option.isVotedByUser(userId)){
                item.setVoted(true);
                button.setText("VOTAR");
            }
            items.add(item);
        }

        TextView pollTitle = (TextView) findViewById(R.id.poll_title);
        pollTitle.setText(poll.getTitle());

        TextView pollDescription = (TextView) findViewById(R.id.poll_description);
        pollDescription.setText(poll.getDescription());

        TextView pollLocation = (TextView) findViewById(R.id.poll_location);
        pollLocation.setText(poll.getLocation());

        TextView pollVotes = (TextView) findViewById(R.id.votes);
        Set<Long> users = new HashSet<>();
        for (PollOption option : poll.getOptions()){
            for (PollOptionVote vote : option.getVotes()){
                users.add(vote.getUser().getId());
            }
        }
        pollVotes.setText(String.valueOf(users.size()));

        adapter = new PollOptionAdapter(VoteActivity.this, items, optionsList, this);
        optionsList.setAdapter(adapter);

        View view = findViewById(R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Long> options = new ArrayList<Long>();
                for (VotingItem item : adapter.getItems()){
                    if (item.getVoted()) {
                        options.add(item.getOption().getId());
                    }
                }
                if (options.size()>0){
                    PollVoteRequest request = new PollVoteRequest();
                    request.setOptions(options);
                    request.setUserId(userId);
                    eventService.votePoll(poll.getId(), request, VoteActivity.this);
                    progressBar.setVisibility(View.VISIBLE);
                    blur.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccion guardada!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    @Override
    public void itemVoted(VotingItem item) {
        TextView button = (TextView) findViewById(R.id.voting_button);
        if (item.getVoted()) {
            button.setText("VOTAR");
        } else {
            boolean anyvotes = false;
            for (VotingItem vItem : adapter.getItems()){
                if (vItem.getVoted()) anyvotes = true;
            }
            if (!anyvotes){
                button.setText("NO PUEDO NINGÚN DÍA");
            }
        }
    }

    public void pollVoted(){
        Toast.makeText(getApplicationContext(), "Votos guardados!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
