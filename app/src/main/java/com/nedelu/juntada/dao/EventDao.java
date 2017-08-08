package com.nedelu.juntada.dao;

import android.content.Context;

import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.util.DatabaseHelper;

import java.sql.SQLException;

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
    }
}

