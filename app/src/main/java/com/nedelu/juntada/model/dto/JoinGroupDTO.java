package com.nedelu.juntada.model.dto;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class JoinGroupDTO {

    private Long userId;
    private String groupToken;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupToken() {
        return groupToken;
    }

    public void setGroupToken(String groupToken) {
        this.groupToken = groupToken;
    }

}
