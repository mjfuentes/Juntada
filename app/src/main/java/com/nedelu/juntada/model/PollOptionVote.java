package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;
import com.nedelu.juntada.model.User;

import java.util.Date;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOptionVote {

    @DatabaseField(id=true, unique=true, canBeNull=false, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "user", foreign = true, foreignAutoRefresh = true)
    private User user;

    @DatabaseField(columnName = "creation_date")
    private Date creationDate;

    @DatabaseField(columnName = "poll_option", foreign = true, foreignAutoRefresh = true)
    private PollOption pollOption;

    public PollOptionVote(){

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PollOption getPollOption() {
        return pollOption;
    }

    public void setPollOption(PollOption pollOption) {
        this.pollOption = pollOption;
    }

}
