package com.nedelu.juntada.dao;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollOptionVote;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.aux.ConfirmedUser;
import com.nedelu.juntada.model.aux.DontKnowUsers;
import com.nedelu.juntada.model.aux.InvitedUser;
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

            if (event != null) {
                event.setConfirmedUsers(new ArrayList<User>());

                List<ConfirmedUser> confirmedUsers = helper.getConfirmedUsersDao().queryBuilder().where().eq("event", id).query();
                for (ConfirmedUser confirmedUser : confirmedUsers) {
                    event.getConfirmedUsers().add(helper.getUserDao().queryForId(confirmedUser.getUserId()));
                }

                List<NotGoingUsers> notGoingUsers = helper.getmNotGoingUsersDao().queryBuilder().where().eq("event", id).query();
                for (NotGoingUsers notGoingUser : notGoingUsers) {
                    event.getNotGoingUsers().add(helper.getUserDao().queryForId(notGoingUser.getUserId()));
                }

                List<DontKnowUsers> doNotKnowUsers = helper.getDontKnowUsersDao().queryBuilder().where().eq("event", id).query();
                for (DontKnowUsers doNotKnowUser : doNotKnowUsers) {
                    event.getDoNotKnowUsers().add(helper.getUserDao().queryForId(doNotKnowUser.getUserId()));
                }
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

    public PollOption getPollOption(Long id) {
        try {
            return helper.getPollOptionDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void savePollOptionVote(PollOptionVote vote) {
        try {
            helper.getmPollOptionVotesDao().createOrUpdate(vote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ConfirmedUser getconfirmedUser(Long userId, Long eventId) {
        try {
            return helper.getConfirmedUsersDao().queryBuilder().where().eq("user",userId).and().eq("event",eventId).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PollOptionVote getPollOptionVote(Long user, Long pollOptionId) {
        try {
            return helper.getmPollOptionVotesDao().queryBuilder().where().eq("poll_option",pollOptionId).and().eq("user", user).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeVotes(Long id) {

        try {
            DeleteBuilder<PollOptionVote, Long> builder = helper.getmPollOptionVotesDao().deleteBuilder();
            builder.where().eq("poll_option", id);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cleanAssistance(Long id) {
        try {
            DeleteBuilder<ConfirmedUser, Long> builder = helper.getConfirmedUsersDao().deleteBuilder();
            builder.where().eq("event", id);
            builder.delete();

            DeleteBuilder<NotGoingUsers, Long> builder2 = helper.getmNotGoingUsersDao().deleteBuilder();
            builder2.where().eq("event", id);
            builder2.delete();

            DeleteBuilder<DontKnowUsers, Long> builder3 = helper.getDontKnowUsersDao().deleteBuilder();
            builder3.where().eq("event", id);
            builder3.delete();

            DeleteBuilder<InvitedUser, Long> builder4 = helper.getInvitedUsersDao().deleteBuilder();
            builder4.where().eq("event", id);
            builder4.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveNotGoingUser(NotGoingUsers notGoingUser) {
        try {
            helper.getmNotGoingUsersDao().create(notGoingUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveDontKnowUser(DontKnowUsers dontKnowUser) {
        try {
            helper.getDontKnowUsersDao().create(dontKnowUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Event> getForUser(Long userId) {
        try {
            List<InvitedUser> result = helper.getInvitedUsersDao().queryBuilder().where().eq("user", userId).query();
            List<Event> events = new ArrayList<>();
            for (InvitedUser invitedUser : result){
                Event event = getEvent(invitedUser.getEventId());
                if (event != null) {
                    events.add(event);
                }
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveInvitedUser(InvitedUser invitedUser) {
        try {
            helper.getInvitedUsersDao().create(invitedUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(Long userId, Event event) {
        try {
            DeleteBuilder<InvitedUser, Long> builder = helper.getInvitedUsersDao().deleteBuilder();
            builder.where().eq("event", event.getId());
            builder.where().eq("user", userId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(Long eventId) {
        try {
            DeleteBuilder<InvitedUser, Long> builder = helper.getInvitedUsersDao().deleteBuilder();
            builder.where().eq("event", eventId);
            builder.delete();

            DeleteBuilder<Event, Long> builder2 = helper.getEventDao().deleteBuilder();
            builder2.where().eq("id", eventId);
            builder2.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

