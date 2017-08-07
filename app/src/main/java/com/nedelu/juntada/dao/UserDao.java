package com.nedelu.juntada.dao;

import android.content.Context;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.User;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class UserDao {

    private Context context;
    public UserDao(Context context){
        this.context = context;
    }

    public User getUser(String facebookId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        return adapter.findFirst(User.class, "facebook_id = ?", new String[]{facebookId});
    }

    public void saveUser(User user){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(user);
    }

    public void saveUserGroup(Long userId, Long groupId) {
        SqlAdapter adapter = Persistence.getAdapter(context);
        GroupMember GroupMember = adapter.findFirst(GroupMember.class, "user_id = ? AND group_id = ? ", new String[]{userId.toString(),groupId.toString()});
        if (GroupMember == null){
            GroupMember groupMember = new GroupMember();
            groupMember.setGroupId(groupId);
            groupMember.setUserId(userId);
            adapter.store(groupMember);
        }
    }
}
