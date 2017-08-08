package com.nedelu.juntada.model.dto;

import java.util.Date;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOptionVoteDTO {

    private UserDTO user;
    private Date creationDate;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
