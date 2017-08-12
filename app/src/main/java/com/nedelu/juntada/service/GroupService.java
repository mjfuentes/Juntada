package com.nedelu.juntada.service;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nedelu.juntada.activity.GroupActivity;
import com.nedelu.juntada.activity.NewEventActivity;
import com.nedelu.juntada.activity.NewGroupActivity;
import com.nedelu.juntada.activity.NewPollActivity;
import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.dao.GroupDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.GroupDTO;
import com.nedelu.juntada.model.dto.PollDTO;
import com.nedelu.juntada.model.dto.UserDTO;
import com.nedelu.juntada.service.interfaces.ServerInterface;


import java.io.File;
import java.util.List;
import java.util.Observable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class GroupService extends Observable {

    private static GroupService instance;
    private Context context;
    private UserService userService;
    private Callbacks activity;
    private GroupDao groupDao;
    private EventDao eventDao;
    private EventService eventService;

    private GroupService(Context context) {
        this.context = context;
        this.userService = new UserService(context);
        this.eventService = new EventService(context);
        this.groupDao = new GroupDao(context);
        this.eventDao = new EventDao(context);
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
    public void createGroup(final Context context, Long userId, Group group, Uri fileUri, final NewGroupActivity newGroupActivity){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        File file = FileUtils.getFile(context, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getMimeType(fileUri.toString())),
                        file
                );
        RequestBody groupName =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, group.getName());

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        final Call<GroupDTO> call = server.createGroup(userId, groupName, body);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                GroupDTO groupDTO = response.body();
                Group group =saveGroup(groupDTO);
                newGroupActivity.groupCreated(group.getId());
            }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(context,"Group creation failed!", Toast.LENGTH_LONG).show();
                newGroupActivity.groupCreationFailed();
            }
        });
    }

    public void createEvent(PollRequest request, final NewEventActivity newEventActivity){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        final Call<EventDTO> call = server.createEvent(request);
        call.enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                EventDTO eventDTO = response.body();
                Event event = eventService.saveEvent(eventDTO);
                newEventActivity.eventCreated(event);
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                Toast.makeText(context,"Event creation failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createPoll(PollRequest request, final NewPollActivity newPollActivity){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        final Call<PollDTO> call = server.createPoll(request);
        call.enqueue(new Callback<PollDTO>() {
            @Override
            public void onResponse(Call<PollDTO> call, Response<PollDTO> response) {
                PollDTO pollDTO = response.body();
                Poll poll = eventService.savePoll(pollDTO);
                newPollActivity.pollCreated(poll);
            }

            @Override
            public void onFailure(Call<PollDTO> call, Throwable t) {
                Toast.makeText(context,"Poll creation failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void loadGroups(final Long userId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        Call<List<GroupDTO>> call = server.getGroups(userId);
        call.enqueue(new Callback<List<GroupDTO>>() {
            @Override
            public void onResponse(Call<List<GroupDTO>> call, Response<List<GroupDTO>> response) {
                if (response.code() != 404) {
                    List<GroupDTO> groups = response.body();
                    for (GroupDTO groupDTO:groups){
                        saveGroup(groupDTO);
                    }
                    activity.updateGroups();
                } else {
                    Toast.makeText(context,"Error connecting to server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupDTO>> call, Throwable t) {
                Toast.makeText(context,"Sin conexion", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadGroup(final Long groupId, final GroupActivity groupActivity){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        Call<GroupDTO> call = server.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.code() != 404) {
                    GroupDTO groupDTO = response.body();
                    Group group = saveGroup(groupDTO);
                    groupActivity.refreshGroup(group);
                } else {
                    Toast.makeText(context,"Error connecting to server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(context,"Sin conexion", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Group fromDTO(GroupDTO groupDTO) {
        Group group = new Group();
        group.setId(groupDTO.getId());
        group.setImageUrl(groupDTO.getImageUrl());
        group.setName(groupDTO.getName());
        return group;
    }


    // DATABASE

    public List<Group> getUserGroups(Long userId){
        return groupDao.getUserGroups(userId);
    }

    private void updateGroups(Long userId, List<Group> groups){
        groupDao.updateGroups(userId,groups);
    }

    private Group saveGroup(GroupDTO groupDTO){
        Group group = fromDTO(groupDTO);
        groupDao.saveGroup(group);

        for (UserDTO user : groupDTO.getUsers()){
            userService.saveUser(user);
            groupDao.saveGroupMember(groupDTO.getId(), user.getId());
        }

        for (PollDTO pollDTO: groupDTO.getPolls()){
            eventService.savePoll(pollDTO);
        }
        for (EventDTO eventDTO: groupDTO.getEvents()){
            eventService.saveEvent(eventDTO);
        }

        return getGroup(group.getId());
    }

    public List<Event> getEvents(Long groupId){
        return groupDao.getEvents(groupId);
    }

    public List<Poll> getPolls(Long groupId){
        return groupDao.getPolls(groupId);
    }

    public PollRequest savePollRequest(PollRequest request){
        return groupDao.savePollRequest(request);
    }

    public void deleteGroup(Long userId, Group group){
       groupDao.deleteGroup(userId,group);
    }

    public Group loadGroupData(Long groupId, GroupActivity groupActivity){

       Group group = groupDao.loadGroupData(groupId);

        for (Event event : group.getEvents()){
            eventService.populateUsers(event);
        }

        for (GroupMember member : groupDao.getGroupMembers(group.getId())){
            group.getUsers().add(userService.getUser(member.getUserId()));
        }

        loadGroup(groupId, groupActivity);

        return group;
    }

    public Group getGroup(Long groupId){

        Group group = groupDao.loadGroupData(groupId);

        for (GroupMember member : groupDao.getGroupMembers(group.getId())){
            group.getUsers().add(userService.getUser(member.getUserId()));
        }

        return group;
    }

    public PollRequest getPollRequest(Long pollRequestId) {
        return groupDao.getPollRequest(pollRequestId);
    }

    public interface Callbacks{
        public void updateGroups();
    }
}
