package com.nedelu.juntada.dao;

import android.content.Context;
import android.provider.ContactsContract;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.util.DatabaseHelper;

import java.sql.SQLException;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class UserDao {

    private Context context;
    private DatabaseHelper helper;

    public UserDao(Context context){
        this.context = context;
        this.helper = new DatabaseHelper(context);
    }

    public User getUser(Long id) {
        try {
            return helper.getUserDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByFacebookId(String facebookId) {
        try {
            return helper.getUserDao().queryBuilder()
                    .where()
                    .eq("facebook_id",facebookId).query().get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveUser(User user) {
        try {
            helper.getUserDao().createOrUpdate(user);
        } catch (Exception e){
            e.printStackTrace();
        }
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
