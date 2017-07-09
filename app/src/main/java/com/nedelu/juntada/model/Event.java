package com.nedelu.juntada.model;

import java.util.Date;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class Event {

    private Long id;
    private Group group;
    private User creator;
    private List<User> confirmedUsers;
    private List<User> doNotKnowUsers;
    private List<User> notGoingUsers;
    private Date startingDate;
    private Date endingDate;
    private String title;
    private String description;

    public static Event createFromPoll(Poll poll, Long pollOptionId){
        for (PollOption option : poll.getOptions()){
            if (option.getId().equals(pollOptionId)){
                Event event = new Event();
                event.setGroup(poll.getGroup());
                event.setStartingDate(option.getStartingDate());
                event.setEndingDate(option.getEndingDate());
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
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
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


}
