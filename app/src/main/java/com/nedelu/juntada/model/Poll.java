package com.nedelu.juntada.model;

import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class Poll {

    private Long id;
    private User creator;
    private Group group;
    private String title;
    private String location;
    private List<PollOption> options;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

}
