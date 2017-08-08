package com.nedelu.juntada.service;

import android.content.Context;

import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.dao.GroupDao;
import com.nedelu.juntada.dao.UserDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.PollDTO;
import com.nedelu.juntada.model.dto.PollOptionDTO;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class EventService {

    private Context context;
    private EventDao eventDao;
    private GroupDao groupDao;
    private UserDao userDao;
    private UserService userService;
    private GroupService groupService;

    public EventService(Context context) {
        this.context = context;
        this.userService = new UserService(context);
        this.eventDao = new EventDao(context);
        this.groupDao = new GroupDao(context);
        this.userDao = new UserDao(context);
    }

    public void saveEvent(Event event){
        eventDao.saveEvent(event);
    }

    public void savePoll(Poll poll){
        eventDao.savePoll(poll);
    }

    public void savePoll(PollDTO pollDTO) {
        Poll poll = fromDTO(pollDTO);
        savePoll(poll);

        for(PollOptionDTO optionDTO : pollDTO.getOptions()){
            PollOption option = new PollOption();
            option.setId(optionDTO.getId());
            option.setDate(optionDTO.getDate());
            option.setTime(optionDTO.getTime());
            option.setPoll(getPoll(optionDTO.getPoll().getId()));

            eventDao.savePollOption(option);
//
//            for (PollOptionVote vote : optionDTO.getVotingUsers()){
//
//            }
        }
    }

    private Poll fromDTO(PollDTO pollDTO) {

        Poll poll = new Poll();
        poll.setLocation(pollDTO.getLocation());
        poll.setTitle(pollDTO.getTitle());
        poll.setCreator(userDao.getUser(pollDTO.getCreator()));
        poll.setGroup(groupDao.getGroup(pollDTO.getGroup()));

        return poll;
    }

    public void saveEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setId(eventDTO.getId());
        event.setLocation(eventDTO.getLocation());
        event.setGroup(groupDao.getGroup(eventDTO.getOwnerGroup()));
        event.setTitle(eventDTO.getTitle());
        event.setDate(eventDTO.getDate());
        event.setTime(eventDTO.getTime());
        event.setDescription(eventDTO.getDescription());
        event.setCreator(userDao.getUser(eventDTO.getCreator()));

        eventDao.saveEvent(event);
    }

    public Poll getPoll(Long pollId){
        return eventDao.getPoll(pollId);
    }
}
