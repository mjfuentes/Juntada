package com.nedelu.juntada.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class UserDTO {

	private Long id;
    private String firstName;
    private String lastName;
    private String facebookId;
    private String imageUrl;
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
