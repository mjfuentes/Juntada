package com.nedelu.juntada.model;

import java.util.Date;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOptionVote {

    private Long id;
    private User user;
    private Date creationDate;

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

}
