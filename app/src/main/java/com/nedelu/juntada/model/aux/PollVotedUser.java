package com.nedelu.juntada.model.aux;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollVotedUser {



    @DatabaseField(unique=true, canBeNull=false, columnName = "id",generatedId = true)
    private Long id;

    @DatabaseField(columnName = "poll")
    private Long pollId;

    @DatabaseField(columnName = "user")
    private Long userId;


    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
