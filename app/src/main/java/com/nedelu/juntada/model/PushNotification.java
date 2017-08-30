package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;


public class PushNotification {

    @DatabaseField(unique=true, canBeNull=false, columnName = "id",generatedId = true)
    private Long id;

    @DatabaseField(columnName = "title")
    private String mTitle;

    @DatabaseField(columnName = "description")
    private String mDescription;

    @DatabaseField(columnName = "type")
    private String mType;

    @DatabaseField(columnName = "value")
    private String mValue;

    @DatabaseField(columnName = "creator_id")
    private Long creatorId;

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

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmValue() {
        return mValue;
    }

    public void setmValue(String mValue) {
        this.mValue = mValue;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

}