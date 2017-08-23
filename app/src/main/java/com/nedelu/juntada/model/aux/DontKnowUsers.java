package com.nedelu.juntada.model.aux;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class DontKnowUsers {

    @DatabaseField(unique=true, canBeNull=false, columnName = "id",generatedId = true)
    private Long id;

    @DatabaseField(columnName = "event")
    private Long eventId;

    @DatabaseField(columnName = "user")
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
