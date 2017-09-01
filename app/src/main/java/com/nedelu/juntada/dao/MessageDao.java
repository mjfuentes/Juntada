package com.nedelu.juntada.dao;

import android.content.Context;

import com.nedelu.juntada.model.Message;
import com.nedelu.juntada.util.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class MessageDao {
    private Context context;
    private DatabaseHelper helper;

    public MessageDao(Context context) {
        this.context = context;
        this.helper = new DatabaseHelper(context);
    }

    public List<Message> getMessagesForEvent(Long eventId) {
        try {
            List<Message> messages = helper.getMessageDao().queryBuilder().where().eq("type","event").and().eq("type_id",eventId).query();
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void saveMessage(Message message){
        try {
            helper.getMessageDao().createOrUpdate(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
