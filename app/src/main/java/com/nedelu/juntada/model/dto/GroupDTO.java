package com.nedelu.juntada.model.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com
 */

public class GroupDTO {

    private String name;
    private Long id;
    private UserDTO creator;
    private String imageUrl;
    private List<InvitedEventDTO> events;
    private List<UserDTO> users;
    private List<PollDTO> polls;

    public List<InvitedEventDTO> getEvents() {
		return events;
	}

	public void setEvents(List<InvitedEventDTO> events) {
		this.events = events;
	}

	public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

	public UserDTO getCreator() {
		return creator;
	}

	public void setCreator(UserDTO creator) {
		this.creator = creator;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public List<PollDTO> getPolls() {
		return polls;
	}

	public void setPolls(List<PollDTO> polls) {
		this.polls = polls;
	}

}
