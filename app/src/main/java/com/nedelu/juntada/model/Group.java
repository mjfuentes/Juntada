package com.nedelu.juntada.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com
 */

public class Group {

    private Long id;
    private String name;
//    private Date creationDate;
    private List<User> users = new ArrayList<>();
    private List<Poll> activePolls;
    private List<Event> futureEvents;
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Date getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(Date creationDate) {
//        this.creationDate = creationDate;
//    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
