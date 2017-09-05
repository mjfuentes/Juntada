package com.nedelu.juntada.dao;

import android.content.Context;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.aux.ConfirmedUser;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.aux.VotedUser;
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

            QueryBuilder<GroupMember, Long> qb = helper.getGroupMemberDao().queryBuilder();
            qb.where().eq("user_id", userId);
            List<Group> groups = helper.getGroupDao().queryBuilder().join("id", "group_id", qb).query();
            for (Group group : groups) {
                group.unansweredEventsAndPolls =  getUnansweredEventsAndPolls(userId, group.getId());
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
        GroupMember member = null;
        try {
            member = helper.getGroupMemberDao().queryBuilder().where().eq("group_id",groupId).and().eq("user_id",userId).queryForFirst();
            if (member == null) {
                member = new GroupMember();
                member.setGroupId(groupId);
                member.setUserId(userId);
                helper.getGroupMemberDao().createOrUpdate(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getEvents(Long groupId){
        try {
            return helper.getEventDao().queryBuilder().where().eq("owner_group", groupId).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Poll> getPolls(Long groupId){
        try {
            return helper.getPollDao().queryBuilder().where().eq("group", groupId).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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

    public List<GroupMember> getGroupMembers(Long groupId) {
        try {

            return helper.getGroupMemberDao().queryBuilder()
                    .where()
                    .eq("group_id", groupId)
                    .query();
        } catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void clearMembers(Long id) {
        try {
            DeleteBuilder<GroupMember, Long> deleteBuilder = helper.getGroupMemberDao().deleteBuilder();
            deleteBuilder.where().eq("group_id", id);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearPolls(Long id) {
        try {
            DeleteBuilder<Poll, Long> deleteBuilder = helper.getPollDao().deleteBuilder();
            deleteBuilder.where().eq("group", id);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearEvents(Long id) {
        try {
            DeleteBuilder<Event, Long> deleteBuilder = helper.getEventDao().deleteBuilder();
            deleteBuilder.where().eq("owner_group", id);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUnansweredEventsAndPolls(Long userId, Long groupId) {
        try {
            List<String[]> result = helper.getVotedUsersDao().queryRaw("select event from votedUser where user = " + userId).getResults();
            List<String> eventIds = new ArrayList<>();
            for (String[] string : result) {
                eventIds.add(string[0]);
            }

            QueryBuilder<Event, Long> queryBuilder = helper.getEventDao().queryBuilder();
            Long unvotedEvents = queryBuilder.where().eq("owner_group", groupId).and().ne("creator", userId).and().notIn("id", eventIds).countOf();


            List<String[]> result2 = helper.getPollVotedUserDao().queryRaw("select poll from pollVotedUser where user = " + userId).getResults();
            List<String> pollIds = new ArrayList<>();
            for (String[] string : result2) {
                pollIds.add(string[0]);
            }

            QueryBuilder<Poll, Long> queryBuilder2 = helper.getPollDao().queryBuilder();
            Long unvotedPolls = queryBuilder2.where().eq("group", groupId).and().ne("creator", userId).and().notIn("id", pollIds).countOf();

            return unvotedEvents.intValue() + unvotedPolls.intValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
