package com.nedelu.juntada.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOption {

    @DatabaseField(id=true, unique=true, canBeNull=false, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "date")
    private String date;

    @DatabaseField(columnName = "time")
    private String time;

    @DatabaseField(columnName = "poll", foreign = true, foreignAutoRefresh = true)
    private Poll poll;

    @ForeignCollectionField(columnName = "votes", eager = true)
    private ForeignCollection<PollOptionVote> votes;

    public boolean selected;

    public PollOption(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ForeignCollection<PollOptionVote> getVotes() {
        return votes;
    }

    public void setVotes(ForeignCollection<PollOptionVote> votes) {
        this.votes = votes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public boolean isVotedByUser(Long userId) {
        for (PollOptionVote vote : new ArrayList<>(votes)) {
            if (vote.getUser().getId().equals(userId)) return true;
        }
        return false;
    }
}
