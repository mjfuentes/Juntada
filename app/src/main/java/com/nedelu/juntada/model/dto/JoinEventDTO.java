package com.nedelu.juntada.model.dto;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class JoinEventDTO {

    private Long userId;
    private String eventToken;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEventToken() {
        return eventToken;
    }

    public void setEventToken(String groupToken) {
        this.eventToken = groupToken;
    }

}
