package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class Event {

    @DatabaseField(id=true, unique=true, canBeNull=false, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "owner_group", foreign = true, foreignAutoRefresh = true,foreignAutoCreate = true)
    private Group ownerGroup;

    @DatabaseField(columnName = "creator", foreign = true, foreignAutoRefresh = true,foreignAutoCreate = true)
    private User creator;

    @DatabaseField(columnName = "date")
    private String date;

    @DatabaseField(columnName = "time")
    private String time;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "location")
    private String location;

    private List<User> confirmedUsers = new ArrayList<>();
    private List<User> doNotKnowUsers = new ArrayList<>();
    private List<User> notGoingUsers = new ArrayList<>();

    public Event(){

    }

    public static Event createFromPoll(Poll poll, Long pollOptionId){
        for (PollOption option : poll.getOptions()){
            if (option.getId().equals(pollOptionId)){
                Event event = new Event();
                event.setGroup(poll.getGroup());
                event.setTitle(poll.getTitle());
                event.setCreator(poll.getCreator());
                for (PollOptionVote vote : option.getVotes()){
                    event.getConfirmedUsers().add(vote.getUser());
                }
                return event;
            }
        }
        return null;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }


    public Group getGroup() {
        return ownerGroup;
    }

    public void setGroup(Group group) {
        this.ownerGroup = group;
    }

    public List<User> getConfirmedUsers() {
        return confirmedUsers;
    }

    public void setConfirmedUsers(List<User> confirmedUsers) {
        this.confirmedUsers = confirmedUsers;
    }

    public List<User> getDoNotKnowUsers() {
        return doNotKnowUsers;
    }

    public void setDoNotKnowUsers(List<User> doNotKnowUsers) {
        this.doNotKnowUsers = doNotKnowUsers;
    }

    public List<User> getNotGoingUsers() {
        return notGoingUsers;
    }

    public void setNotGoingUsers(List<User> notGoingUsers) {
        this.notGoingUsers = notGoingUsers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(Group ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }



}
