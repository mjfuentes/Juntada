package com.nedelu.juntada.dao;

import android.content.Context;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class GroupDao {

    private Context context;
    private UserDao userDao;
    private EventDao eventDao;

    public GroupDao(Context context){
        this.context = context;
        this.userDao = new UserDao(context);
    }

    public List<Group> getUserGroups(Long userId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        List<Group> groups = new ArrayList<>();

        List<GroupMember> groupMembers = adapter.findAll(GroupMember.class, "user_id= ? ", new String[]{userId.toString()});
        for (GroupMember groupMember : groupMembers){
            Group group = new Group();
            group.setId(groupMember.getGroupId());
            Group savedGroup = adapter.findFirst(group);
            if (savedGroup != null) groups.add(savedGroup);
        }
        return groups;
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
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(group);
        try {
            for (User user : group.getUsers()) {
                userDao.saveUser(user);
                userDao.saveUserGroup(user.getId(), group.getId());
            }
            for (Poll poll : group.getPolls()){
                eventDao.savePoll(poll);
            }
            for (Event event: group.getEvents()){
                eventDao.saveEvent(event);
            }
        } catch (Exception e){
            //todo // FIXME: 17/07/17
        }
    }

    public void savePoll(Poll poll){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(poll);
        try {
            for (PollOption option : poll.getOptions()) {
                adapter.store(option);
            }
        } catch (Exception e){
        }
    }

    public void saveEvent(Event event){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(event);
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
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.delete(group);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(group.getId());
        groupMember.setUserId(userId);

        adapter.delete(groupMember);
    }

    public Group loadGroupData(Long groupId){

        // Load from DB and get updated data from server
        SqlAdapter adapter = Persistence.getAdapter(context);
        Group group = new Group();
        group.setId(groupId);
        group = adapter.findFirst(group);

        List<GroupMember> groupMembers = adapter.findAll(GroupMember.class, "group_id= ? ", new String[]{groupId.toString()});
        for (GroupMember groupMember : groupMembers){
            User user = new User();
            user.setId(groupMember.getUserId());
            group.getUsers().add(adapter.findFirst(user));
        }

        return group;
    }

    public PollRequest getPollRequest(Long pollRequestId) {
        SqlAdapter adapter = Persistence.getAdapter(context);
        PollRequest request = new PollRequest();
        request.setId(pollRequestId);
        return adapter.findFirst(request);
    }

}
