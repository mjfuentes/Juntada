package com.nedelu.juntada.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nedelu.juntada.activity.EventActivity;
import com.nedelu.juntada.activity.EventsActivity;
import com.nedelu.juntada.activity.GroupTabbedActivity;
import com.nedelu.juntada.activity.GroupsActivity;
import com.nedelu.juntada.activity.JoinActivity;
import com.nedelu.juntada.activity.VoteActivity;
import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.dao.GroupDao;
import com.nedelu.juntada.dao.UserDao;
import com.nedelu.juntada.model.Assistance;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollOptionVote;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.aux.ConfirmedUser;
import com.nedelu.juntada.model.aux.DontKnowUsers;
import com.nedelu.juntada.model.aux.InvitedUser;
import com.nedelu.juntada.model.aux.NotGoingUsers;
import com.nedelu.juntada.model.dto.AssitanceRequest;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.EventTokenDTO;
import com.nedelu.juntada.model.dto.GroupDTO;
import com.nedelu.juntada.model.dto.GroupTokenDTO;
import com.nedelu.juntada.model.dto.InvitedEventDTO;
import com.nedelu.juntada.model.dto.JoinEventDTO;
import com.nedelu.juntada.model.dto.JoinGroupDTO;
import com.nedelu.juntada.model.dto.PollConfirmDTO;
import com.nedelu.juntada.model.dto.PollDTO;
import com.nedelu.juntada.model.dto.PollOptionDTO;
import com.nedelu.juntada.model.dto.PollVoteRequest;
import com.nedelu.juntada.model.dto.UserDTO;
import com.nedelu.juntada.service.interfaces.ServerInterface;

import java.util.ArrayList;
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
    private GroupService groupService;
    private String baseUrl = "http://10.1.1.16:8080";
    private Long userId;

    public EventService(Context context) {
        this.context = context;
        this.eventDao = new EventDao(context);
        this.groupDao = new GroupDao(context);
        this.userDao = new UserDao(context);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = userPref.getString("server_url", "http://10.1.1.16:8080");
        userId = userPref.getLong("userId",0L);
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
                if (response.code() == 200) {
                    savePoll(response.body());
                    listener.pollVoted(true, 0l);
                } else {
                    Toast.makeText(context,"No se pudo realizar la operacion", Toast.LENGTH_LONG).show();
                    listener.pollVoted(false, 0l);
                }
            }

            @Override
            public void onFailure(Call<PollDTO> call, Throwable t) {
                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                listener.pollVoted(false, 0l);
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
            eventDao.removeVotes(optionDTO.getId());
            for (Long user : optionDTO.getVotingUsers()){
//                PollOptionVote vote = eventDao.getPollOptionVote(user, optionDTO.getId());
//                if (vote == null) {
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
        poll.setDescription(pollDTO.getDescription());

        return poll;
    }

    public Event saveEvent(EventDTO eventDTO) {
        if (eventDTO.getId() != null) {
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


            eventDao.cleanAssistance(event.getId());
            for (Long userId : eventDTO.getConfirmedUsers()) {
                ConfirmedUser confirmedUser = new ConfirmedUser();
                confirmedUser.setEventId(newEvent.getId());
                confirmedUser.setUserId(userId);
                eventDao.saveConfirmedUser(confirmedUser);
            }

            if (eventDTO.getNotGoingUsers() != null) {
                for (Long userId : eventDTO.getNotGoingUsers()) {
                    NotGoingUsers notGoingUser = new NotGoingUsers();
                    notGoingUser.setEventId(newEvent.getId());
                    notGoingUser.setUserId(userId);
                    eventDao.saveNotGoingUser(notGoingUser);
                }
            }

            if (eventDTO.getDoNotKnowUsers() != null) {

                for (Long userId : eventDTO.getDoNotKnowUsers()) {
                    DontKnowUsers dontKnowUser = new DontKnowUsers();
                    dontKnowUser.setEventId(newEvent.getId());
                    dontKnowUser.setUserId(userId);
                    eventDao.saveDontKnowUser(dontKnowUser);
                }
            }

            if (eventDTO.getInvitedUsers() != null) {

                for (Long userId : eventDTO.getInvitedUsers()) {
                    InvitedUser invitedUser = new InvitedUser();
                    invitedUser.setEventId(newEvent.getId());
                    invitedUser.setUserId(userId);
                    eventDao.saveInvitedUser(invitedUser);
                }
            }
            populateUsers(newEvent);
            return newEvent;
        }

        return null;
    }

    public Event saveEvent(InvitedEventDTO eventDTO) {
        if (eventDTO.getId() != null) {
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


            eventDao.cleanAssistance(event.getId());
            for (UserDTO user : eventDTO.getConfirmedUsers()) {
                saveUser(user);
                ConfirmedUser confirmedUser = new ConfirmedUser();
                confirmedUser.setEventId(newEvent.getId());
                confirmedUser.setUserId(user.getId());
                eventDao.saveConfirmedUser(confirmedUser);
            }

            if (eventDTO.getNotGoingUsers() != null) {
                for (Long user : eventDTO.getNotGoingUsers()) {
                    NotGoingUsers notGoingUser = new NotGoingUsers();
                    notGoingUser.setEventId(newEvent.getId());
                    notGoingUser.setUserId(user);
                    eventDao.saveNotGoingUser(notGoingUser);
                }
            }

            if (eventDTO.getDoNotKnowUsers() != null) {

                for (Long user : eventDTO.getDoNotKnowUsers()) {
                    DontKnowUsers dontKnowUser = new DontKnowUsers();
                    dontKnowUser.setEventId(newEvent.getId());
                    dontKnowUser.setUserId(user);
                    eventDao.saveDontKnowUser(dontKnowUser);
                }
            }

            if (eventDTO.getInvitedUsers() != null) {

                for (Long user : eventDTO.getInvitedUsers()) {
                    InvitedUser invitedUser = new InvitedUser();
                    invitedUser.setEventId(newEvent.getId());
                    invitedUser.setUserId(user);
                    eventDao.saveInvitedUser(invitedUser);
                }
            }
            populateUsers(newEvent);
            return newEvent;
        }

        return null;
    }

    public User saveUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setFacebookId(userDTO.getFacebookId());
        user.setImageUrl(userDTO.getImageUrl());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        userDao.saveUser(user);

        return user;
    }

    public Poll getPoll(Long pollId){
        return eventDao.getPoll(pollId);
    }


    public void populateUsers(Event event) {
        event.setConfirmedUsers(eventDao.getConfirmedUsers(event.getId()));
        event.setNotGoingUsers(eventDao.getNotGoingUsers(event.getId()));
        event.setDoNotKnowUsers(eventDao.getDoNotKnowUsers(event.getId()));

    }

    public Event getEvent(Long eventId) {
        return eventDao.getEvent(eventId);
    }

    public void saveAssistance(Long userId, Long eventId, Assistance assistance, final EventActivity eventActivity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);
        AssitanceRequest request = new AssitanceRequest();
        request.setUserId(userId);
        request.setEventId(eventId);
        request.setAssistance(assistance);

        final Call<EventDTO> call = server.saveAssistance(eventId, request);
        call.enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.code() == 200) {
                    saveEvent(response.body());
                    eventActivity.assistanceSaved(true);
                } else {
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                    eventActivity.assistanceSaved(false);
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                eventActivity.assistanceSaved(false);
                Toast.makeText(context,"Sin conexion", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void confirmEvent(Poll poll, PollOption option, final VoteActivity voteActivity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);
        PollConfirmDTO confirm = new PollConfirmDTO();
        confirm.setSelectedOptionId(option.getId());

        final Call<EventDTO> call = server.confirmPoll(poll.getId(), confirm);
        call.enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.code() == 200) {
                    saveEvent(response.body());
                    voteActivity.pollVoted(true, response.body().getId());
                } else {
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                    voteActivity.pollVoted(false, 0l);
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                voteActivity.pollVoted(false, 0l);
                Toast.makeText(context,"Sin conexion", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void generateToken(Long eventId, final EventActivity eventActivity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        Call<EventTokenDTO> call = server.getEventToken(eventId);

        call.enqueue(new Callback<EventTokenDTO>() {
            @Override
            public void onResponse(Call<EventTokenDTO> call, Response<EventTokenDTO> response) {
                if (response.code() == 200) {
                    eventActivity.tokenGenerated(response.body().getToken());
                } else {
                    eventActivity.tokenGenerated(null);
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EventTokenDTO> call, Throwable t) {
                eventActivity.tokenGenerated(null);
                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    public List<Event> loadEventsForUser(Long userId, final EventsActivity eventsActivity) {
        List<Event> events = eventDao.getForUser(userId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        Call<List<InvitedEventDTO>> call = server.getEventsForUser(userId);

        call.enqueue(new Callback<List<InvitedEventDTO>>() {
            @Override
            public void onResponse(Call<List<InvitedEventDTO>> call, Response<List<InvitedEventDTO>> response) {
                if (response.code() == 200) {
                    new SaveEventsTask().execute(response.body(),eventsActivity);
                } else {
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                    eventsActivity.refreshEvents(null);
                }
            }

            @Override
            public void onFailure(Call<List<InvitedEventDTO>> call, Throwable t) {
                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                eventsActivity.refreshEvents(null);
            }
        });

        return events;
    }


    public List<Event> getEventsForUser(Long userId) {
        return eventDao.getForUser(userId);
    }

    public void joinEvent(final Long userId, String token, final GroupsActivity joinActivity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        JoinEventDTO joinEvent = new JoinEventDTO();
        joinEvent.setUserId(userId);
        joinEvent.setEventToken(token);

        Call<EventDTO> call = server.joinEvent(joinEvent);

        call.enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.code() == 200) {
                    Event event = saveEvent(response.body());

                    Intent main = new Intent(joinActivity, EventActivity.class);
                    SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putLong("userId", userId);
                    editor.putLong("eventId", event.getId());
                    editor.apply();
                    joinActivity.startActivity(main);
                } else {
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                    joinActivity.finish();
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                joinActivity.finish();
            }
        });
    }

    public void loadEvent(Long eventId, final EventActivity eventActivity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        Call<EventDTO> call = server.getEvent(eventId);

        call.enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.code() == 200) {
                    Event event = saveEvent(response.body());
                    eventActivity.refreshInfo(event);
                } else {
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                    eventActivity.finish();
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                eventActivity.finish();
            }
        });
    }

    public void loadPoll(Long pollId, final VoteActivity voteActivity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        Call<PollDTO> call = server.getPoll(pollId);

        call.enqueue(new Callback<PollDTO>() {
            @Override
            public void onResponse(Call<PollDTO> call, Response<PollDTO> response) {
                if (response.code() == 200) {
                    Poll poll = savePoll(response.body());
                    voteActivity.refreshPoll(poll);
                } else {
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                    voteActivity.finish();
                }
            }

            @Override
            public void onFailure(Call<PollDTO> call, Throwable t) {
                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                voteActivity.finish();
            }
        });
    }

    private class SaveEventsTask extends AsyncTask<Object, Void, Void> {
        private EventsActivity eventsActivity;
        private List<Event> savedEvents = new ArrayList<>();
        protected Void doInBackground(Object... lists) {
            List<InvitedEventDTO> events = (List<InvitedEventDTO>) lists[0];
            eventsActivity = (EventsActivity) lists[1];
            List<Long> eventsId = new ArrayList<>();
            for (InvitedEventDTO eventDTO : events){
                System.out.println("running task");
                savedEvents.add(saveEvent(eventDTO));
                eventsId.add(eventDTO.getId());
            }

            for (Event event : getEventsForUser(userId)){
                if (!eventsId.contains(event.getId())){
                    eventDao.deleteEvent(userId, event);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            eventsActivity.refreshEvents(savedEvents);
            super.onPostExecute(aVoid);
        }


    }

    public interface ResultListener{

        void pollVoted(Boolean result, Long eventId);
    }
}
