package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollRequest {

    @DatabaseField(columnName = "id")
    private Long id;

    @DatabaseField(columnName = "creator_id")
    private Long creatorId;

    @DatabaseField(columnName = "group_id")
    private Long groupId;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "location")
    private String location;

    public PollRequest(){

    }

    private List<PollOption> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
