package com.nedelu.juntada.dao;

import android.content.Context;

import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Poll;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class EventDao {

    private Context context;
    private UserDao userDao;

    public EventDao(Context context){
        this.context = context;
        this.userDao = new UserDao(context);
    }

    public void savePoll(Poll poll){

    }

    public void saveEvent(Event event){
    }
}

