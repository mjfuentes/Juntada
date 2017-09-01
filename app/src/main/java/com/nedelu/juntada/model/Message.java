package com.nedelu.juntada.model;

import com.j256.ormlite.field.DatabaseField;

import org.threeten.bp.Instant;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class Message {

    @DatabaseField(id=true, unique=true, canBeNull=false, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "message")
    private String message;

    @DatabaseField(columnName = "type")
    private MessageType type;

    @DatabaseField(columnName = "type_id")
    private Long typeId;

    @DatabaseField(columnName = "creator_id")
    private Long creatorId;

    @DatabaseField(columnName = "time")
    private Instant time;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
