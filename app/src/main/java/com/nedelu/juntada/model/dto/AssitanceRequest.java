package com.nedelu.juntada.model.dto;

import com.nedelu.juntada.model.Assistance;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class AssitanceRequest {


    private Assistance assistance;
    private Long userId;
    private Long eventId;
    public Assistance getAssistance() {
        return assistance;
    }

    public void setAssistance(Assistance assistance) {
        this.assistance = assistance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
