package com.nedelu.juntada.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class Poll {


    @DatabaseField(id=true, unique=true, canBeNull=false, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "creator", foreign = true, foreignAutoRefresh = true)
    private User creator;

    @DatabaseField(columnName = "group", foreign = true, foreignAutoRefresh = true)
    private Group group;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "location")
    private String location;

    public String getDescription(){
        return "Test description";
    }

    @ForeignCollectionField(columnName = "options", eager = true)
    private ForeignCollection<PollOption> options;

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

    public ForeignCollection<PollOption> getOptions() {
        return options;
    }

    public void setOptions(ForeignCollection<PollOption> options) {
        this.options = options;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
