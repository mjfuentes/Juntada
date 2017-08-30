package com.nedelu.juntada.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.widget.Toast;

import com.nedelu.juntada.activity.LoginActivity;
import com.nedelu.juntada.dao.UserDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.FirebaseRegistration;
import com.nedelu.juntada.model.dto.UserDTO;
import com.nedelu.juntada.service.interfaces.ServerInterface;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class UserService {

    private Context context;
    private UserDao userDao;
    private String baseUrl;
    private EventService eventService;

    public UserService(Context context)
    {
        this.context = context;
        this.userDao = new UserDao(context);
        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = userPref.getString("server_url", "http://www.juntada.nedelu.com");
        eventService = new EventService(context);
    }

    public User createUser(final LoginActivity activity, String facebookId, String name, String lastName, String imageUrl) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        final User user = new User();
        user.setFirstName(name);
        user.setLastName(lastName);
        user.setFacebookId(facebookId);
        user.setImageUrl(imageUrl);
        Call<UserDTO> call = server.createUser(user);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {

                if (response.code() == 200) {
                    UserDTO userDTO = response.body();
                    User newUser = saveUser(userDTO);
                    activity.nextActivity(newUser, true);
                } else {
                    Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
            }
        });

        return user;
    }

    public User registerUserToken(User user, String token) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        FirebaseRegistration registration = new FirebaseRegistration();
        registration.setUserId(user.getId());
        registration.setFirebaseId(token);
        Call<UserDTO> call = server.registerFirebase(user.getId(), registration);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() == 200) {
                    UserDTO userDTO = response.body();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
//                Toast.makeText(context,"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
            }
        });

        return user;
    }

    public void saveUser(User user){
        try {
            userDao.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUserByFacebookId(String facebookId){
        try {
            return userDao.getUserByFacebookId(facebookId);
        } catch (Exception e){
            return null;
        }
    }

    public User getUser(Long id){
        try {
            return userDao.getUser(id);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void saveUserGroup(Long userId, Long groupId) {
        userDao.saveUserGroup(userId, groupId);
    }

    public User saveUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setFacebookId(userDTO.getFacebookId());
        user.setImageUrl(userDTO.getImageUrl());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        saveUser(user);

        return getUser(user.getId());
    }
}
