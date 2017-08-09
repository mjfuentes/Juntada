package com.nedelu.juntada.model.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class EventDTO {

	private Long id;
    private Long ownerGroup;
    private Long creator;
    private List<Long> confirmedUsers;
    private List<Long> doNotKnowUsers;
    private List<Long> notGoingUsers;
    private String date;
    private String time;
    private String title;
    private String description;
    private String location;
    

    public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }


    public List<Long> getConfirmedUsers() {
        return confirmedUsers;
    }

    public void setConfirmedUsers(List<Long> confirmedUsers) {
        this.confirmedUsers = confirmedUsers;
    }

    public List<Long> getDoNotKnowUsers() {
        return doNotKnowUsers;
    }

    public void setDoNotKnowUsers(List<Long> doNotKnowUsers) {
        this.doNotKnowUsers = doNotKnowUsers;
    }

    public List<Long> getNotGoingUsers() {
        return notGoingUsers;
    }

    public void setNotGoingUsers(List<Long> notGoingUsers) {
        this.notGoingUsers = notGoingUsers;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Long getOwnerGroup() {
		return ownerGroup;
	}

	public void setOwnerGroup(Long ownerGroup) {
		this.ownerGroup = ownerGroup;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


}
