package com.nedelu.juntada.model;

import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOption {

    private Long id;
    private Date startingDate;
    private Date endingDate;
    private List<PollOptionVote> votes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }


    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public List<PollOptionVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PollOptionVote> votes) {
        this.votes = votes;
    }

}
