package com.nedelu.juntada.model.dto;

import java.util.List;

public class PollRequestDTO {
	
	private Long creatorId;
	private Long groupId;
	private String title;
	private String description;
	private String location;
	private List<PollOptionDTO> options;
	
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
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
	public List<PollOptionDTO> getOptions() {
		return options;
	}
	public void setOptions(List<PollOptionDTO> options) {
		this.options = options;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
