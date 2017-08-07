package com.nedelu.juntada.service;

import android.content.Context;

import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Poll;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class EventService {

    private Context context;
    private EventDao eventDao;
    public EventService(Context context) {
        this.context = context;
        this.eventDao = new EventDao(context);
    }

    public void saveEvent(Event event){
        eventDao.saveEvent(event);
    }

    public void savePoll(Poll poll){
        eventDao.savePoll(poll);
    }
}
