package com.nedelu.juntada.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com
 */

public class Group {

    @DatabaseField(id=true, unique=true, canBeNull=false, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "name")
    private String name;

    @ForeignCollectionField(columnName = "polls", eager = true)
    private ForeignCollection<Poll> polls;

    @ForeignCollectionField(columnName = "events", eager = true)
    private ForeignCollection<Event> events;

    @DatabaseField(columnName = "image_url")
    private String imageUrl;

    @DatabaseField(columnName = "creator", foreign = true, foreignAutoRefresh = true,foreignAutoCreate = true)
    private User creator;

    public int unansweredEventsAndPolls;

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

    public ForeignCollection<Event> getEvents() {
        return events;
    }

    public void setEvents(ForeignCollection<Event> events) {
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
    public ForeignCollection<Poll> getPolls() {
        return polls;
    }

    public void setPolls(ForeignCollection<Poll> polls) {
        this.polls = polls;
    }


    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
