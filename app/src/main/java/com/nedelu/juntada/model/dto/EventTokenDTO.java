package com.nedelu.juntada.model.dto;

public class EventTokenDTO {
    private Long eventId;
	private String token;

	
	
    public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
