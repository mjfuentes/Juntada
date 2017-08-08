package com.nedelu.juntada.dao;

import android.content.Context;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.util.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class GroupDao {

    private Context context;
    private UserDao userDao;
    private EventDao eventDao;
    private DatabaseHelper helper;

    public GroupDao(Context context){
        this.context = context;
        this.userDao = new UserDao(context);
        this.helper = new DatabaseHelper(context);
    }

    public List<Group> getUserGroups(Long userId){
        try {
            List<Group> groups = new ArrayList<>();
            List<GroupMember> groupsMember = helper.getGroupMemberDao().queryBuilder()
                    .where()
                    .eq("user_id", userId)
                    .query();

            for (GroupMember groupMember: groupsMember){
                groups.add(helper.getGroupDao().queryForId(groupMember.getGroupId()));
            }
            return groups;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void updateGroups(Long userId, List<Group> groups){
        for (Group savedGroup : getUserGroups(userId)){
            if (!groups.contains(savedGroup)) {
                deleteGroup(userId, savedGroup);
            }
        }
        for (Group group : groups){
            saveGroup(group);
        }
    }

    public void saveGroup(Group group){

        try {
            helper.getGroupDao().createOrUpdate(group);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveGroupMember(Long groupId, Long userId){
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        try {
            helper.getGroupMemberDao().create(member);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getEvents(Long groupId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        return adapter.findAll(Event.class);
    }

    public List<Poll> getPolls(Long groupId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        return adapter.findAll(Poll.class);
    }

    public PollRequest savePollRequest(PollRequest request){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(request);
        return adapter.findFirst(request);
    }

    public void deleteGroup(Long userId, Group group){
        try {
            DeleteBuilder<GroupMember, Long> deleteBuilder = helper.getGroupMemberDao().deleteBuilder();
            deleteBuilder.where()
                    .eq("user_id",userId)
                    .and()
                    .eq("group_id", group.getId());
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Group loadGroupData(Long groupId){

        try {
            return helper.getGroupDao().queryForId(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PollRequest getPollRequest(Long pollRequestId) {
        SqlAdapter adapter = Persistence.getAdapter(context);
        PollRequest request = new PollRequest();
        request.setId(pollRequestId);
        return adapter.findFirst(request);
    }

    public Group getGroup(Long groupId) {
        try {
            return helper.getGroupDao().queryForId(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}