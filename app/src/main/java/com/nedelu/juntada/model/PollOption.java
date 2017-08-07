package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOption {

    @DatabaseField(columnName = "id")
    private Long id;

    @DatabaseField(columnName = "date")
    private Date date;

    @DatabaseField(columnName = "time")
    private String time;

    @DatabaseField(columnName = "poll", foreign = true, foreignAutoRefresh = true)
    private Poll poll;

    @ForeignCollectionField(columnName = "votes", eager = true)
    private List<PollOptionVote> votes;

    public PollOption(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PollOptionVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PollOptionVote> votes) {
        this.votes = votes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

}
