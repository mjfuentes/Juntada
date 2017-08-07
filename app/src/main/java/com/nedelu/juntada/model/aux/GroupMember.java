package com.nedelu.juntada.model.aux;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class GroupMember {

    @DatabaseField(columnName = "id", generatedId = true)
    private Long id;

    @DatabaseField(columnName = "user_id")
    private Long userId;

    @DatabaseField(columnName = "group_id")
    private Long groupId;

    public GroupMember(){

    }

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
