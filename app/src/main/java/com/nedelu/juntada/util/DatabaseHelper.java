package com.nedelu.juntada.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollOptionVote;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.aux.ConfirmedUser;
import com.nedelu.juntada.model.aux.DontKnowUsers;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.aux.InvitedUser;
import com.nedelu.juntada.model.aux.NotGoingUsers;
import com.nedelu.juntada.model.aux.PollVotedUser;
import com.nedelu.juntada.model.aux.VotedUser;

import java.sql.SQLException;

/**
 * Created by matiasj.fuentes@gmail.com.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "juntada2.db";
    private static final int DATABASE_VERSION = 43;

    private Dao<User, Long> mUserDao = null;
    private Dao<Group, Long> mGroupDao = null;
    private Dao<GroupMember, Long> mGroupMemberDao = null;
    private Dao<Event, Long> mEventDao = null;
    private Dao<Poll, Long> mPollDao = null;
    private Dao<PollOption, Long> mPollOptionDao = null;
    private Dao<DontKnowUsers, Long> mDontKnowUsersDao = null;
    private Dao<ConfirmedUser, Long> mConfirmedUsersDao = null;
    private Dao<NotGoingUsers, Long> mNotGoingUsersDao = null;
    private Dao<InvitedUser, Long> mInvitedUsersDao = null;
    private Dao<PollOptionVote, Long> mPollOptionVotesDao = null;
    private Dao<PushNotification, Long> mPushNotificationDao = null;
    private Dao<VotedUser, Long> mVotedUsersDao = null;
    private Dao<PollVotedUser, Long> mPollVoteduserDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, Group.class);
            TableUtils.createTableIfNotExists(connectionSource, GroupMember.class);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            TableUtils.createTableIfNotExists(connectionSource, Poll.class);
            TableUtils.createTableIfNotExists(connectionSource, PollOption.class);
            TableUtils.createTableIfNotExists(connectionSource, PollOptionVote.class);
            TableUtils.createTableIfNotExists(connectionSource,NotGoingUsers.class);
            TableUtils.createTableIfNotExists(connectionSource,ConfirmedUser.class);
            TableUtils.createTableIfNotExists(connectionSource,DontKnowUsers.class);
            TableUtils.createTableIfNotExists(connectionSource,PollOptionVote.class);
            TableUtils.createTableIfNotExists(connectionSource,InvitedUser.class);
            TableUtils.createTableIfNotExists(connectionSource, PushNotification.class);
            TableUtils.createTableIfNotExists(connectionSource, VotedUser.class);
            TableUtils.createTableIfNotExists(connectionSource, PollVotedUser.class);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Group.class, true);
            TableUtils.dropTable(connectionSource, GroupMember.class, true);
            TableUtils.dropTable(connectionSource, Event.class, true);
            TableUtils.dropTable(connectionSource, Poll.class, true);
            TableUtils.dropTable(connectionSource, PollOption.class, true);
            TableUtils.dropTable(connectionSource, PollOptionVote.class, true);
            TableUtils.dropTable(connectionSource, NotGoingUsers.class, true);
            TableUtils.dropTable(connectionSource, ConfirmedUser.class, true);
            TableUtils.dropTable(connectionSource, DontKnowUsers.class, true);
            TableUtils.dropTable(connectionSource,PollOptionVote.class, true);
            TableUtils.dropTable(connectionSource,InvitedUser.class, true);
            TableUtils.dropTable(connectionSource,PushNotification.class, true);
            TableUtils.dropTable(connectionSource,VotedUser.class, true);
            TableUtils.dropTable(connectionSource,PollVotedUser.class, true);

            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* User */

    public Dao<User, Long> getUserDao() throws SQLException {
        if (mUserDao == null) {
            mUserDao = getDao(User.class);
        }

        return mUserDao;
    }

    public Dao<Group, Long> getGroupDao() throws SQLException {
        if (mGroupDao == null) {
            mGroupDao = getDao(Group.class);
        }

        return mGroupDao;
    }

    public Dao<GroupMember, Long> getGroupMemberDao() throws SQLException {
        if (mGroupMemberDao == null) {
            mGroupMemberDao  = getDao(GroupMember.class);
        }

        return mGroupMemberDao ;
    }

    public Dao<Event, Long> getEventDao() throws SQLException {
        if (mEventDao == null) {
            mEventDao  = getDao(Event.class);
        }

        return mEventDao ;
    }

    public Dao<Poll, Long> getPollDao() throws SQLException {
        if (mPollDao == null) {
            mPollDao  = getDao(Poll.class);
        }

        return mPollDao ;
    }

    public Dao<PollOption, Long> getPollOptionDao() throws SQLException {
        if (mPollOptionDao == null) {
            mPollOptionDao  = getDao(PollOption.class);
        }

        return mPollOptionDao ;
    }

    public Dao<ConfirmedUser, Long> getConfirmedUsersDao() throws SQLException {
        if (mConfirmedUsersDao == null) {
            mConfirmedUsersDao  = getDao(ConfirmedUser.class);
        }

        return mConfirmedUsersDao ;
    }

    public Dao<NotGoingUsers, Long> getmNotGoingUsersDao() throws SQLException {
        if (mNotGoingUsersDao == null) {
            mNotGoingUsersDao  = getDao(NotGoingUsers.class);
        }

        return mNotGoingUsersDao ;
    }

    public Dao<DontKnowUsers, Long> getDontKnowUsersDao() throws SQLException {
        if (mDontKnowUsersDao == null) {
            mDontKnowUsersDao  = getDao(DontKnowUsers.class);
        }

        return mDontKnowUsersDao ;
    }

    public Dao<InvitedUser, Long> getInvitedUsersDao() throws SQLException {
        if (mInvitedUsersDao == null) {
            mInvitedUsersDao  = getDao(InvitedUser.class);
        }

        return mInvitedUsersDao;
    }

    public Dao<VotedUser, Long> getVotedUsersDao() throws SQLException {
        if (mVotedUsersDao == null) {
            mVotedUsersDao  = getDao(VotedUser.class);
        }

        return mVotedUsersDao;
    }

    public Dao<PollOptionVote, Long> getmPollOptionVotesDao() throws SQLException {
        if (mPollOptionVotesDao == null) {
            mPollOptionVotesDao  = getDao(PollOptionVote.class);
        }

        return mPollOptionVotesDao ;
    }

    public Dao<PushNotification, Long> getPushNotificationDao() throws SQLException {
        if (mPushNotificationDao == null) {
            mPushNotificationDao  = getDao(PushNotification.class);
        }

        return mPushNotificationDao;
    }

    public Dao<PollVotedUser, Long> getPollVotedUserDao() throws SQLException {
        if (mPollVoteduserDao == null) {
            mPollVoteduserDao  = getDao(PollVotedUser.class);
        }

        return mPollVoteduserDao;
    }

    @Override
    public void close() {
        mUserDao = null;

        super.close();
    }


}