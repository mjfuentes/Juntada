package com.nedelu.juntada.model.dto;

import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollDTO {

	private Long id;
	private Long creator;
    private Long group;
    private String title;
    private String location;
    private List<PollOptionDTO> options;

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<PollOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<PollOptionDTO> options) {
        this.options = options;
    }
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
