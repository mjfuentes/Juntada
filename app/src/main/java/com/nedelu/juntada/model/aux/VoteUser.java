package com.nedelu.juntada.model.aux;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class VoteUser {
    private Long pollOptionId;
    private Long userId;

    public Long getPollOptionId() {
        return pollOptionId;
    }

    public void setPollOptionId(Long pollOptionId) {
        this.pollOptionId = pollOptionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
