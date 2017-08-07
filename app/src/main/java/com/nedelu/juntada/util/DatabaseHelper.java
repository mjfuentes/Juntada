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
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.aux.GroupMember;

import java.sql.SQLException;

/**
 * Created by matiasj.fuentes@gmail.com.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "juntada2.db";
    private static final int    DATABASE_VERSION = 22;

    private Dao<User, Long> mUserDao = null;
    private Dao<Group, Long> mGroupDao = null;
    private Dao<GroupMember, Long> mGroupMemberDao = null;

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

    @Override
    public void close() {
        mUserDao = null;

        super.close();
    }
}