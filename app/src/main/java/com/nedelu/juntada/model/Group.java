package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com
 */

public class Group {

    @DatabaseField(columnName = "id")
    private Long id;

    @DatabaseField(columnName = "name")
    private String name;

    @ForeignCollectionField(columnName = "polls", eager = true)
    private List<Poll> polls;

    @ForeignCollectionField(columnName = "events", eager = true)
    private List<Event> events;

    @DatabaseField(columnName = "image_url")
    private String imageUrl;

    private List<User> users = new ArrayList<>();

    public Group(){

    }

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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

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
    public List<Poll> getPolls() {
        return polls;
    }

    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }

}
