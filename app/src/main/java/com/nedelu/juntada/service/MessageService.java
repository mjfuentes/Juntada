package com.nedelu.juntada.service;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nedelu.juntada.dao.MessageDao;
import com.nedelu.juntada.dao.UserDao;
import com.nedelu.juntada.model.Message;
import com.nedelu.juntada.model.MessageType;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.dto.UserDTO;
import com.nedelu.juntada.service.interfaces.MessagingInterface;
import com.nedelu.juntada.service.interfaces.ServerInterface;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageService {
    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter dateTimeFormatter2;
    private Context context;
    private MessageDao messageDao;
    private String baseUrl;
    private UserDao userDao;

    public MessageService(Context context){
        this.context = context;
        this.messageDao = new MessageDao(context);
        this.userDao = new UserDao(context);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = userPref.getString("server_url", "http://www.juntada.nedelu.com");


        dateTimeFormatter= DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                .withLocale( Locale.ENGLISH );

        dateTimeFormatter2 = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                .withLocale( Locale.ENGLISH ).withZone(ZoneId.systemDefault());
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
        message.setTime(Instant.now().toString());

        Call<Message> call = server.createMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.code() == 200) {
                    Message message = response.body();
                    message.mine = true;
                    listener.messageSent(message);
                    saveMessage(response.body());
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

    public void loadMessages(final Long eventId, final Long userId, final MessagesLoadedListener listener){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MessagingInterface server = retrofit.create(MessagingInterface.class);

        Call<List<Message>> call = server.getMessages(MessageType.EVENT.toString(), eventId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.code() == 200) {
                    List<Message> messages = response.body();
                    saveMessages(messages);
                    listener.messagesLoaded(getMessages(eventId, userId));
                } else {
                    listener.messagesNotLoaded();
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                listener.messagesNotLoaded();
            }
        });
    }

    private void saveMessages(List<Message> messages) {
        for (Message message : messages){
            saveMessage(message);
        }
    }

    public List<Message> getMessages(Long eventId, Long currentUser) {
        List<Message> messages = messageDao.getMessagesForEvent(eventId);
        Map<Long,List<Message>> map = new HashMap<>();
        for (Message message : messages){
            if (message.getCreatorId().equals(currentUser)){
                message.mine = true;
            }
            else if (map.containsKey(message.getCreatorId())){
                map.get(message.getCreatorId()).add(message);
            } else {
                List<Message> list = new ArrayList<>();
                list.add(message);
                map.put(message.getCreatorId(), list);
            }
        }
        for (Long userId : map.keySet()){
            User user = userDao.getUser(userId);
            for (Message message : map.get(userId)){
                try {
                    message.userImage = user.getImageUrl();
                    message.userName = user.getFirstName() + " " + user.getLastName();
                } catch (Exception e){
                    e.printStackTrace();
                }
                message.mine=false;
            }
        }
        return messages;
    }

    public void saveMessage(Message message){
        messageDao.saveMessage(message);
    }

    public interface MessageSentListener{
        void messageSent(Message message);
        void messageFailed();
    }

    public interface  MessagesLoadedListener{
        void messagesLoaded(List<Message> messages);
        void messagesNotLoaded();
    }
}
