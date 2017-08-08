package com.nedelu.juntada.model.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOptionDTO {

	private Long id;
	private String date;
    private String time;
    private PollDTO poll;
    private List<UserDTO> votingUsers;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<UserDTO> getVotingUsers() {
		return votingUsers;
	}

	public void setVotingUsers(List<UserDTO> votingUsers) {
		this.votingUsers = votingUsers;
	}

	public PollDTO getPoll() {
		return poll;
	}

	public void setPoll(PollDTO poll) {
		this.poll = poll;
	}

}
