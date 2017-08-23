package com.nedelu.juntada.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class User {


    @DatabaseField(id=true, unique=true, canBeNull=false, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "first_name")
    private String firstName;

    @DatabaseField(columnName = "last_name")
    private String lastName;

    @DatabaseField(columnName = "facebook_id")
    private String facebookId;

    @DatabaseField(columnName = "image_url")
    private String imageUrl;

    @ForeignCollectionField(columnName = "owned_events", eager = true)
    private ForeignCollection<Event> ownedEvents;

    public User(){

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ForeignCollection<Event> getOwnedEvents() {
        return ownedEvents;
    }

    public void setOwnedEvents(ForeignCollection<Event> ownedEvents) {
        this.ownedEvents = ownedEvents;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(User.class)){
            User user = (User) obj;
            return user.getId().equals(this.getId());
        }
        return super.equals(obj);
    }

}
