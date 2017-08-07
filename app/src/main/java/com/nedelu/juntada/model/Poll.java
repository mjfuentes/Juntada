package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class Poll {

    @DatabaseField(columnName = "id")
    private Long id;

    @DatabaseField(columnName = "creator", foreign = true, foreignAutoRefresh = true)
    private User creator;

    @DatabaseField(columnName = "group", foreign = true, foreignAutoRefresh = true)
    private Group group;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "location")
    private String location;

    @ForeignCollectionField(columnName = "options", eager = true)
    private List<PollOption> options;

    public Poll(){

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
