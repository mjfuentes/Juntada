package com.nedelu.juntada.model;

import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOption {

    private Long id;
    private Date date;
    private String time;
    private List<PollOptionVote> votes;

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

}
