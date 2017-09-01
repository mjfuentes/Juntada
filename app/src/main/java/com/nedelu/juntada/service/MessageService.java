package com.nedelu.juntada.service;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nedelu.juntada.dao.MessageDao;
import com.nedelu.juntada.model.Message;
import com.nedelu.juntada.model.MessageType;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.dto.UserDTO;
import com.nedelu.juntada.service.interfaces.MessagingInterface;
import com.nedelu.juntada.service.interfaces.ServerInterface;

import org.threeten.bp.Instant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageService {
    private Context context;
    private MessageDao messageDao;
    private String baseUrl;

    public MessageService(Context context){
        this.context = context;
        this.messageDao = new MessageDao(context);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = userPref.getString("server_url", "http://www.juntada.nedelu.com");
    }

    public void sendMessage(User user, String messageString, MessageType type, Long typeId, final MessageSentListener listener){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MessagingInterface server = retrofit.create(MessagingInterface.class);

        final Message message = new Message();
        message.setCreatorId(user.getId());
        message.setMessage(messageString);
        message.setType(type);
        message.setTypeId(typeId);
        message.setTime(Instant.now());

        Call<Message> call = server.createMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.code() == 200) {
                    listener.messageSent(response.body());
                    saveMessage(message);
                } else {
                    listener.messageFailed();
                }

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                listener.messageFailed();
            }
        });

    }

    public List<Message> getMessages(Long eventId) {
        return messageDao.getMessagesForEvent(eventId);
    }

    public void saveMessage(Message message){
        messageDao.saveMessage(message);
    }

    public interface MessageSentListener{
        void messageSent(Message message);
        void messageFailed();
    }
}
