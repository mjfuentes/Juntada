package com.nedelu.juntada.model.dto;

import java.util.List;

public class VoteRequestDTO {
	
	private List<Long> options;
	private Long userId;
	
	public List<Long> getOptions() {
		return options;
	}
	public void setOptions(List<Long> options) {
		this.options = options;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
