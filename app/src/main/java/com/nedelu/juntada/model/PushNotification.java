package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;


public class PushNotification {

    @DatabaseField(unique=true, canBeNull=false, columnName = "id",generatedId = true)
    private Long id;

    @DatabaseField(columnName = "title")
    private String mTitle;

    @DatabaseField(columnName = "description")
    private String mDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

}