package com.nedelu.juntada.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.dao.GroupDao;
import com.nedelu.juntada.dao.UserDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.User;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class DataUpdateService {

    private GroupDao groupDao;
    private UserDao userDao;
    private GroupService groupService;
    private NotificationService notificationService;
    private EventService eventService;
    private EventDao eventDao;
    private Long currentUserId;

    public DataUpdateService(Context context){
        this.groupDao = new GroupDao(context);
        this.userDao = new UserDao(context);
        this.eventDao = new EventDao(context);
        this.groupService = GroupService.getInstance(context);
        this.eventService = new EventService(context);
        this.notificationService = new NotificationService(context);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
        currentUserId = userPref.getLong("userId", 0L);
    }

    public void updateGroupName(Long groupId, Long userId){
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        if (group != null && user != null){
            groupService.loadGroup(groupId);
            notificationService.displayNotificationGroupNameUpdated(user, group,group.getName());
        }
    }

    public void updateGroupImage(Long groupId, Long userId){
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        if (group != null && user != null){
            groupService.loadGroup(groupId);
            notificationService.displayNotificationGroupImageUpdated(user, group);
        }
    }

    public void addGroupParticipant(Long groupId, Long userId){
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        if (group != null && user != null){
            groupService.loadGroup(groupId);
            notificationService.displayNotificationNewGroupMember(user, group);
        }
    }

    public void removeGroupParticipant(Long groupId, Long userId){
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        if (group != null && user != null){
            groupService.loadGroup(groupId);
            notificationService.displayNotificationGroupMemberLeft(user, group);
        }
    }

    public void deleteGroup(Long groupId, Long userId){
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        if (group != null && user != null){
            groupService.deleteGroup(groupId);
            notificationService.displayNotificationGroupDeleted(user, group);
        }
    }

    public void deleteEvent(Long eventId, Long userId){
        Event event =eventDao.getEvent(eventId);
        User user = userDao.getUser(userId);
        if (event != null && user != null){
            eventService.deleteEvent(eventId);
            notificationService.displayNotificationEventDeleted(user, event);
        }

    }

    public void updateEvent(Long eventId, Long userId){
        Event event =eventDao.getEvent(eventId);
        User user = userDao.getUser(userId);
        if (event != null && user != null){
            eventService.loadEvent(eventId, null);
            notificationService.displayNotificationEventUpdated(user, event);
        }
    }

    public void updateMemberGoing(Long eventId, Long userId) {
        Event event =eventDao.getEvent(eventId);
        User user = userDao.getUser(userId);
        if (event != null){
            eventService.loadEvent(eventId, null);

            if (event.getCreator().getId().equals(currentUserId)) {
                notificationService.displayNotificationNewParticipant(user, event);
            }
        }
    }

    public void updateMemberNotGoing(Long eventId, Long userId) {
        Event event =eventDao.getEvent(eventId);
        User user = userDao.getUser(userId);
        if (event != null){
            eventService.loadEvent(eventId, null);
            if (event.getCreator().getId().equals(currentUserId)) {
                notificationService.displayNotificationNotGoing(user, event);
            }
        }
    }

    public void confirmEvent(Long eventId, Long pollId, Long group_id, Long userId) {
        Group group = groupDao.getGroup(group_id);
        User user = userDao.getUser(userId);
        Poll poll = eventDao.getPoll(pollId);
        if (group != null && user != null && poll != null){
            eventDao.deletePoll(pollId);
            eventService.loadEvent(eventId,null);
            notificationService.displayNotificationConfirmedEvent(eventId,poll.getTitle(), group, user);
        }
    }

    public void newEvent(Long eventId,Long groupId, Long userId) {
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        if (group != null && user != null){
            eventService.loadEvent(eventId,null);
            notificationService.displayNotificationNewEvent(eventId, group, user);
        }
    }

    public void newPoll(Long pollId,Long groupId, Long userId) {
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        if (group != null && user != null){
            eventService.loadPoll(pollId,null);
            notificationService.displayNotificationNewPoll(pollId, group, user);
        }
    }

    public void updateUserVotedPoll(Long pollId, Long groupId, Long userId) {
        Group group = groupDao.getGroup(groupId);
        User user = userDao.getUser(userId);
        Poll poll = eventDao.getPoll(pollId);
        if (group != null && poll != null && user != null){
            eventService.loadPoll(pollId, null);
            notificationService.displayNotificationUserVotedPoll(poll, group, user);
        }
    }

}
