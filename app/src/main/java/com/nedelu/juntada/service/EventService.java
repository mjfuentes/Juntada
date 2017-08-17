package com.nedelu.juntada.service;

import android.content.Context;
import android.widget.Toast;

import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.dao.GroupDao;
import com.nedelu.juntada.dao.UserDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollOptionVote;
import com.nedelu.juntada.model.aux.ConfirmedUser;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.GroupDTO;
import com.nedelu.juntada.model.dto.PollDTO;
import com.nedelu.juntada.model.dto.PollOptionDTO;
import com.nedelu.juntada.model.dto.PollVoteRequest;
import com.nedelu.juntada.model.dto.UserDTO;
import com.nedelu.juntada.service.interfaces.ServerInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private String baseUrl = "http://10.1.1.16:8080";

    public EventService(Context context) {
        this.context = context;
        this.userService = new UserService(context);
        this.eventDao = new EventDao(context);
        this.groupDao = new GroupDao(context);
        this.userDao = new UserDao(context);
    }

    public void votePoll(Long pollId, final PollVoteRequest voteRequest, final ResultListener listener){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        final Call<PollDTO> call = server.votePoll(pollId, voteRequest);
        call.enqueue(new Callback<PollDTO>() {
            @Override
            public void onResponse(Call<PollDTO> call, Response<PollDTO> response) {
                if (response.code() != 404) {
                    savePollVotes(voteRequest);
                    listener.pollVoted();
                } else {
                    Toast.makeText(context,"Error connecting to server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PollDTO> call, Throwable t) {
                Toast.makeText(context,"Sin conexion", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void savePollVotes(PollVoteRequest voteRequest) {
        for (Long optionId : voteRequest.getOptions()){
            PollOptionVote vote = new PollOptionVote();
            vote.setPollOption(eventDao.getPollOption(optionId));
            vote.setUser(userDao.getUser(voteRequest.getUserId()));
            eventDao.savePollOptionVote(vote);
        }
    }

    public void saveEvent(Event event){
        eventDao.saveEvent(event);
    }

    public void savePoll(Poll poll){
        eventDao.savePoll(poll);
    }

    public Poll savePoll(PollDTO pollDTO) {
        Poll poll = fromDTO(pollDTO);
        savePoll(poll);

        for(PollOptionDTO optionDTO : pollDTO.getOptions()){
            PollOption option = new PollOption();
            option.setId(optionDTO.getId());
            option.setDate(optionDTO.getDate());
            option.setTime(optionDTO.getTime());
            option.setPoll(getPoll(optionDTO.getPoll()));

            eventDao.savePollOption(option);

            for (Long user : optionDTO.getVotingUsers()){
                PollOptionVote vote = new PollOptionVote();
                vote.setPollOption(eventDao.getPollOption(option.getId()));
                vote.setUser(userDao.getUser(user));
                eventDao.savePollOptionVote(vote);
            }
        }

        return eventDao.getPoll(poll.getId());
    }

    private Poll fromDTO(PollDTO pollDTO) {

        Poll poll = new Poll();
        poll.setId(pollDTO.getId());
        poll.setLocation(pollDTO.getLocation());
        poll.setTitle(pollDTO.getTitle());
        poll.setCreator(userDao.getUser(pollDTO.getCreator()));
        poll.setGroup(groupDao.getGroup(pollDTO.getGroup()));

        return poll;
    }

    public Event saveEvent(EventDTO eventDTO) {
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

        Event newEvent = eventDao.getEvent(event.getId());

        for (Long userId : eventDTO.getConfirmedUsers()){
            ConfirmedUser confirmedUser = new ConfirmedUser();
            confirmedUser.setEventId(newEvent.getId());
            confirmedUser.setUserId(userId);
            eventDao.saveConfirmedUser(confirmedUser);
        }

        return newEvent;
    }

    public Poll getPoll(Long pollId){
        return eventDao.getPoll(pollId);
    }


    public void populateUsers(Event event) {
        event.setConfirmedUsers(eventDao.getConfirmedUsers(event.getId()));
        event.setNotGoingUsers(eventDao.getNotGoingUsers(event.getId()));
        event.setDoNotKnowUsers(eventDao.getDoNotKnowUsers(event.getId()));

    }

    public interface ResultListener{
        void pollVoted();
    }
}
