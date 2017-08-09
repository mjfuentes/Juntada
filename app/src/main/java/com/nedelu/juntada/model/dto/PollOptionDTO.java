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
    private Long poll;
    private List<Long> votingUsers;
    
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

	public List<Long> getVotingUsers() {
		return votingUsers;
	}

	public void setVotingUsers(List<Long> votingUsers) {
		this.votingUsers = votingUsers;
	}

	public Long getPoll() {
		return poll;
	}

	public void setPoll(Long poll) {
		this.poll = poll;
	}

}
