package com.nedelu.juntada.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nedelu.juntada.R;
import com.nedelu.juntada.activity.GroupTabbedActivity;
import com.nedelu.juntada.activity.GroupsActivity;
import com.nedelu.juntada.activity.NewEventActivity;
import com.nedelu.juntada.activity.NewGroupActivity;
import com.nedelu.juntada.activity.NewPollActivity;
import com.nedelu.juntada.activity.TokenResultActivity;
import com.nedelu.juntada.dao.EventDao;
import com.nedelu.juntada.dao.GroupDao;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.aux.GroupMember;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.GroupDTO;
import com.nedelu.juntada.model.dto.GroupTokenDTO;
import com.nedelu.juntada.model.dto.InvitedEventDTO;
import com.nedelu.juntada.model.dto.JoinGroupDTO;
import com.nedelu.juntada.model.dto.PollDTO;
import com.nedelu.juntada.model.dto.UserDTO;
import com.nedelu.juntada.service.interfaces.ServerInterface;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private GroupDao groupDao;
    private EventDao eventDao;
    private EventService eventService;
    private Long userId;
    private String baseUrl = "http://10.1.1.16:8080";

    private GroupService(Context context) {
        this.context = context;
        this.userService = new UserService(context);
        this.eventService = new EventService(context);
        this.groupDao = new GroupDao(context);
        this.eventDao = new EventDao(context);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = userPref.getString("server_url", "http://10.1.1.3:8080");
        userId = userPref.getLong("userId", 0l);
    }

    private Context getContext(){
        return this.context;
    }

    public static GroupService getInstance(Context context){
        if (instance == null || instance.getContext() != context){
            instance = new GroupService(context);
        }
        return instance;
    }

    // SERVER
    public void createGroup(final Context context, final Long userId, final Group group, final Uri fileUri, final NewGroupActivity newGroupActivity) {

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
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

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                final Call<GroupDTO> call = server.createGroup(headers, userId, groupName, body);
                call.enqueue(new Callback<GroupDTO>() {
                    @Override
                    public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                        if (response.code() == 200) {
                            GroupDTO groupDTO = response.body();
                            Group group = saveGroup(groupDTO);
                            newGroupActivity.groupCreated(group.getId());
                        } else {
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                            newGroupActivity.groupCreated(null);

                        }
                    }

                    @Override
                    public void onFailure(Call<GroupDTO> call, Throwable t) {
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        newGroupActivity.groupCreated(null);
                    }
                });
            }
        });
    }

    public void createEvent(final PollRequest request, final NewEventActivity newEventActivity) {

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                final Call<EventDTO> call = server.createEvent(headers, request);
                call.enqueue(new Callback<EventDTO>() {
                    @Override
                    public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                        if (response.code() == 200) {
                            EventDTO eventDTO = response.body();
                            Event event = eventService.saveEvent(eventDTO);
                            newEventActivity.eventCreated(event);
                        } else {
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                            newEventActivity.eventCreated(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<EventDTO> call, Throwable t) {
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        newEventActivity.eventCreated(null);
                    }
                });
            }
        });
    }

    public void createPoll(final PollRequest request, final NewPollActivity newPollActivity) {
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                final Call<PollDTO> call = server.createPoll(headers, request);
                call.enqueue(new Callback<PollDTO>() {
                    @Override
                    public void onResponse(Call<PollDTO> call, Response<PollDTO> response) {
                        if (response.code() == 200) {
                            PollDTO pollDTO = response.body();
                            Poll poll = eventService.savePoll(pollDTO);
                            newPollActivity.pollCreated(poll);
                        } else {
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                            newPollActivity.pollCreated(null);

                        }
                    }

                    @Override
                    public void onFailure(Call<PollDTO> call, Throwable t) {
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        newPollActivity.pollCreated(null);
                    }
                });
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

    public void loadGroups(final Long userId, final GroupsLoadedListener listener){

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                Map<String,String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                Call<List<GroupDTO>> call = server.getGroups(headers,userId);
                call.enqueue(new Callback<List<GroupDTO>>() {
                    @Override
                    public void onResponse(Call<List<GroupDTO>> call, Response<List<GroupDTO>> response) {
                        if (response.code() == 200) {
                            new SaveGroupsTask().execute(response.body(), listener);
                        } else {
                            if (listener != null) {
                                listener.updateGroups(false);
                            }
                            Toast.makeText(context,R.string.error_connecting, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GroupDTO>> call, Throwable t) {
                        if (listener != null) {
                            listener.updateGroups(false);
                        }
                        Toast.makeText(context,R.string.error_connecting, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    public void loadGroup(final Long groupId) {


        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                Call<GroupDTO> call = server.getGroup(headers, groupId);
                call.enqueue(new Callback<GroupDTO>() {
                    @Override
                    public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                        if (response.code() == 200) {
                            new SaveGroupTask().execute(response.body());
                        } else {

                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupDTO> call, Throwable t) {
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private Group fromDTO(GroupDTO groupDTO) {
        Group group = new Group();
        group.setId(groupDTO.getId());
        User creator = userService.getUser(groupDTO.getCreator().getId());
        if (creator == null){
            creator = userService.saveUser(groupDTO.getCreator());
        }
        group.setCreator(creator);
        group.setImageUrl(groupDTO.getImageUrl());
        group.setName(groupDTO.getName());
        return group;
    }


    // DATABASE

    public void getUserGroups(Long userId, GroupsActivity groupsActivity){
        new GetGroupsTask().execute(userId, groupsActivity);
    }

    private void updateGroups(Long userId, List<Group> groups){
        groupDao.updateGroups(userId,groups);
    }

    private Group saveGroup(GroupDTO groupDTO){
        Group group = fromDTO(groupDTO);
        groupDao.saveGroup(group);

        groupDao.clearMembers(groupDTO.getId());

        for (UserDTO user : groupDTO.getUsers()){
            if (!userId.equals(user.getId())) userService.saveUser(user);
            groupDao.saveGroupMember(groupDTO.getId(), user.getId());
        }

        groupDao.clearPolls(groupDTO.getId());

        for (PollDTO pollDTO: groupDTO.getPolls()){
            eventService.savePoll(pollDTO);
        }

        groupDao.clearEvents(groupDTO.getId());
        for (InvitedEventDTO eventDTO: groupDTO.getEvents()){
            eventService.saveEvent(eventDTO);
        }

        return getGroup(group.getId());
    }

    public List<Event> getEvents(Long groupId){
        List<Event> events = groupDao.getEvents(groupId);
        for (Event event : events){
            event.answered = eventDao.isEventAnswered(event.getId(), userId);
            event.confirmed = eventDao.getConfirmedCount(event.getId());
        }

        return events;
    }

    public List<Poll> getPolls(Long groupId){
        List<Poll> polls = groupDao.getPolls(groupId);
        for (Poll poll : polls){
            poll.voted = eventDao.isPollVoted(poll.getId(), userId);
        }
        return polls;
    }

    public PollRequest savePollRequest(PollRequest request){
        return groupDao.savePollRequest(request);
    }

    public void deleteGroup(final Long userId, final Group group, final GroupTabbedActivity groupActivity){
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                Call<UserDTO> call = server.deleteGroup(headers, userId, group.getId());
                call.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.code() == 200) {
                            groupDao.deleteGroup(userId, group);
                            if (groupActivity != null) {
                                groupActivity.groupDeleted(true);
                            }
                        } else {
                            if (groupActivity != null) {
                                Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                                groupActivity.groupDeleted(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        if (groupActivity != null) {
                            groupActivity.groupDeleted(false);
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }


    public Group loadGroupData(Long groupId){

       Group group = groupDao.loadGroupData(groupId);
        for (Event event : group.getEvents()){
            event.answered = eventDao.isEventAnswered(event.getId(),userId);
        }
        for (GroupMember member : groupDao.getGroupMembers(group.getId())){
            group.getUsers().add(userService.getUser(member.getUserId()));
        }

        loadGroup(groupId);

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

    public void getGroupToken(final Long groupId, final TokenResultActivity activity) {
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                Call<GroupTokenDTO> call = server.getGroupToken(headers, groupId);

                call.enqueue(new Callback<GroupTokenDTO>() {
                    @Override
                    public void onResponse(Call<GroupTokenDTO> call, Response<GroupTokenDTO> response) {
                        if (response.code() == 200) {
                            activity.tokenGenerated(response.body().getToken());
                        } else {
                            activity.tokenGenerated(null);
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupTokenDTO> call, Throwable t) {
                        activity.tokenGenerated(null);
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void joinGroup(final Long userId, final String token, final GroupsActivity joinActivity) {

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                JoinGroupDTO joinGroup = new JoinGroupDTO();
                joinGroup.setUserId(userId);
                joinGroup.setGroupToken(token);

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                Call<GroupDTO> call = server.joinGroup(headers, joinGroup);

                call.enqueue(new Callback<GroupDTO>() {
                    @Override
                    public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                        if (response.code() == 200) {
                            Group group = saveGroup(response.body());

                            Intent main = new Intent(joinActivity, GroupTabbedActivity.class);
                            SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.putLong("userId", userId);
                            editor.putLong("groupId", group.getId());
                            editor.putString("image_url", group.getImageUrl());
                            editor.apply();
                            joinActivity.startActivity(main);
                        } else {
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                            joinActivity.finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupDTO> call, Throwable t) {
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        joinActivity.finish();
                    }
                });
            }
        });
    }

    public void updateGroupName(final Long groupId, final String text, final GroupTabbedActivity groupTabbedActivity) {
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ServerInterface server = retrofit.create(ServerInterface.class);

                GroupDTO groupDTO = new GroupDTO();
                groupDTO.setId(groupId);
                groupDTO.setName(text);

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                Call<GroupDTO> call = server.updateGroupName(headers, groupId, groupDTO);

                call.enqueue(new Callback<GroupDTO>() {
                    @Override
                    public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                        if (response.code() == 200) {
                            Group group = saveGroup(response.body());
                            groupTabbedActivity.updateName(group.getName());
                        } else {
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                            groupTabbedActivity.updateName(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupDTO> call, Throwable t) {
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        groupTabbedActivity.updateName(null);
                    }
                });
            }
        });
    }

    public void updateGroupImage(final Long groupId, final Uri imageUri, final GroupTabbedActivity groupTabbedActivity) {
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {

                File file = FileUtils.getFile(context, imageUri);

                // create RequestBody instance from file
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getMimeType(imageUri.toString())),
                                file
                        );
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Map<String, String> headers = new HashMap<>();
                headers.put("token", task.getResult().getToken());

                ServerInterface server = retrofit.create(ServerInterface.class);
                final Call<GroupDTO> call = server.updateGroupImage(headers, groupId, body);
                call.enqueue(new Callback<GroupDTO>() {
                    @Override
                    public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                        if (response.code() == 200) {
                            GroupDTO groupDTO = response.body();
                            Group group = saveGroup(groupDTO);
                            groupTabbedActivity.imageUpdated(group.getImageUrl());
                        } else {
                            Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                            groupTabbedActivity.imageUpdated(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupDTO> call, Throwable t) {
                        Toast.makeText(context, R.string.error_connecting, Toast.LENGTH_LONG).show();
                        groupTabbedActivity.imageUpdated(null);
                    }
                });
            }
        });
    }

    public void deleteGroup(Long id) {
        groupDao.deleteGroup(userId, groupDao.getGroup(id));
    }


    public interface GroupsLoadedListener{
        public void updateGroups(Boolean result);
    }

    public int getUnansweredEventsAndPolls(Long userId, Long eventId){
        return groupDao.getUnansweredEventsAndPolls(userId, eventId);
    }

    private class SaveGroupsTask extends AsyncTask<Object, Void, Void> {
        private GroupsLoadedListener listener;
        protected Void doInBackground(Object... params) {
            List<GroupDTO> groups = (List<GroupDTO>) params[0];
            listener = (GroupsLoadedListener) params[1];
            List<Long> groupIds = new ArrayList<>();
            for (GroupDTO groupDTO:groups){
                System.out.println("running task");
                saveGroup(groupDTO);
                groupIds.add(groupDTO.getId());
            }

            for (Group group : groupDao.getUserGroups(userId)){
                if (!groupIds.contains(group.getId())){
                    groupDao.deleteGroup(userId, group);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener != null) {
                listener.updateGroups(true);
            }
        }


    }

    private class SaveGroupTask extends AsyncTask<Object, Void, Void> {
        private Group group;
        protected Void doInBackground(Object... params) {
            GroupDTO groupDTO = (GroupDTO) params[0];
            group = saveGroup(groupDTO);
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
        }
    }

    private class GetGroupsTask extends AsyncTask<Object, Void, Void> {
        private Long userId;
        private GroupsActivity activity;
        private List<Group> groups;
        protected Void doInBackground(Object... params) {
            groups = groupDao.getUserGroups((Long) params[0]);
            activity = (GroupsActivity) params[1];
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            activity.setupGroups(groups);
        }


    }
}
