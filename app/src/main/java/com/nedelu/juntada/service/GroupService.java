package com.nedelu.juntada.service;

import android.app.Activity;
import android.content.Context;
import android.icu.text.MessagePattern;
import android.widget.Toast;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.nedelu.juntada.activity.NewGroupActivity;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.Participant;
import com.nedelu.juntada.service.interfaces.ServerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class GroupService extends Observable {

    private Context context;
    private UserService userService;
    private static GroupService instance;
    private Callbacks activity;

    private GroupService(Context context) {
        this.context = context;
        this.userService = new UserService(context);
    }

    private Context getContext(){
        return this.context;
    }

    public void registerClient(Activity activity){
        this.activity = (Callbacks)activity;
    }

    public static GroupService getInstance(Context context){
        if (instance == null || instance.getContext() != context){
            instance = new GroupService(context);
        }
        return instance;
    }

    // SERVER

    public void createGroup(Long userId, Group group, final NewGroupActivity newGroupActivity){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        final Call<Group> call = server.createGroup(userId, group);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                Group group = response.body();
                saveGroup(group);
                newGroupActivity.groupCreated();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(context,"Group creation failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadGroups(final Long userId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        Call<List<Group>> call = server.getGroups(userId);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                List<Group> groups = response.body();
                updateGroups(userId, groups);
                activity.updateGroups();
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(context,"Registration failed!", Toast.LENGTH_LONG).show();
            }
        });
    }


    // DATABASE

    public List<Group> getUserGroups(Long userId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        List<Group> groups = new ArrayList<>();

        List<Participant> participants = adapter.findAll(Participant.class, "user_id= ? ", new String[]{userId.toString()});
        for (Participant participant : participants){
            Group group = new Group();
            group.setId(participant.getGroupId());
            Group savedGroup = adapter.findFirst(group);
            if (savedGroup != null) groups.add(savedGroup);
        }
        return groups;
    }

    public void updateGroups(Long userId, List<Group> groups){
        for (Group savedGroup : getUserGroups(userId)){
            if (!groups.contains(savedGroup)) {
                deleteGroup(userId, savedGroup);
            }
        }
        for (Group group : groups){
            saveGroup(group);
        }
    }

    public void saveGroup(Group group){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(group);
        for (User user : group.getUsers()){
            userService.saveUser(user);
            userService.saveUserGroup(user.getId(), group.getId());
        }
    }

    public void deleteGroup(Long userId, Group group){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.delete(group);

        Participant participant = new Participant();
        participant.setGroupId(group.getId());
        participant.setUserId(userId);

        adapter.delete(participant);
    }

    public interface Callbacks{
        public void updateGroups();
    }
}
