package com.nedelu.juntada.model;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class Participant {

    private Long id;
    private Long userId;
    private Long groupId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

}
