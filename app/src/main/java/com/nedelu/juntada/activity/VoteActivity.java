package com.nedelu.juntada.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.adapter.PollOptionAdapter;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.util.SimpleDividerItemDecoration;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.util.ArrayList;

public class VoteActivity extends AppCompatActivity {

    private RecyclerView optionsList;
    private PollOptionAdapter adapter;
    private Poll poll;
    private EventService eventService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        SharedPreferences userPref = getSharedPreferences("user", 0);
        Long pollId = userPref.getLong("pollId", 0L);
        EventService eventService = new EventService(VoteActivity.this);
        poll = eventService.getPoll(pollId);
        optionsList = (RecyclerView) findViewById(R.id.options_list);
        optionsList.setLayoutManager(new LinearLayoutManager(VoteActivity.this));
        optionsList.addItemDecoration(new SimpleDividerItemDecoration(this));
        adapter = new PollOptionAdapter(VoteActivity.this, new ArrayList<>(poll.getOptions()), optionsList);
        optionsList.setAdapter(adapter);

    }
}
