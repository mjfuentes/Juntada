package com.nedelu.juntada.dao;

import android.content.Context;

import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.aux.ConfirmedUser;
import com.nedelu.juntada.model.aux.DontKnowUsers;
import com.nedelu.juntada.model.aux.NotGoingUsers;
import com.nedelu.juntada.util.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class EventDao {

    private Context context;
    private UserDao userDao;
    private DatabaseHelper helper;

    public EventDao(Context context){
        this.context = context;
        this.userDao = new UserDao(context);
        this.helper = new DatabaseHelper(context);

    }

    public void savePoll(Poll poll){
        try {
            helper.getPollDao().createOrUpdate(poll);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveEvent(Event event){
        try {
            helper.getEventDao().createOrUpdate(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Poll getPoll(Long pollId) {
        try {
            return helper.getPollDao().queryForId(pollId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void savePollOption(PollOption option) {
        try {
            helper.getPollOptionDao().createOrUpdate(option);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Event getEvent(Long id) {
        try {
            Event event = helper.getEventDao().queryForId(id);
            event.setConfirmedUsers(new ArrayList<User>());

            List<ConfirmedUser> confirmedUsers = helper.getConfirmedUsersDao().queryBuilder().where().eq("event", id).query();

            for (ConfirmedUser confirmedUser : confirmedUsers){
                event.getConfirmedUsers().add(helper.getUserDao().queryForId(confirmedUser.getUserId()));
            }

            return event;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveConfirmedUser(ConfirmedUser confirmedUser) {
        try {
            helper.getConfirmedUsersDao().createOrUpdate(confirmedUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getConfirmedUsers(Long id) {
        List<User> users = new ArrayList<>();

        try {
            List<ConfirmedUser> confirmedUsers = helper.getConfirmedUsersDao().queryBuilder().where().eq("event", id).query();
            for (ConfirmedUser confirmedUser: confirmedUsers){
                users.add(helper.getUserDao().queryForId(confirmedUser.getUserId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;

    }

    public List<User> getNotGoingUsers(Long id) {
        List<User> users = new ArrayList<>();

        try {
            List<NotGoingUsers> notGoingUsers = helper.getmNotGoingUsersDao().queryBuilder().where().eq("event", id).query();
            for (NotGoingUsers notGoingUser: notGoingUsers){
                users.add(helper.getUserDao().queryForId(notGoingUser.getUserId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;

    }

    public List<User> getDoNotKnowUsers(Long id) {
        List<User> users = new ArrayList<>();
        try {
            List<DontKnowUsers> dontKnowUsers = helper.getDontKnowUsersDao().queryBuilder().where().eq("event", id).query();
            for (DontKnowUsers dontKnowUser: dontKnowUsers){
                users.add(helper.getUserDao().queryForId(dontKnowUser.getUserId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;

    }
}

