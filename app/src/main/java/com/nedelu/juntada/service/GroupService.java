package com.nedelu.juntada.service;

import android.app.Activity;
import android.content.Context;
import android.icu.text.MessagePattern;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nedelu.juntada.activity.NewEventActivity;
import com.nedelu.juntada.activity.NewGroupActivity;
import com.nedelu.juntada.activity.NewPollActivity;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.Participant;
import com.nedelu.juntada.service.interfaces.ServerInterface;


import java.io.File;
import java.util.ArrayList;
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
import retrofit2.http.Multipart;

import static com.nedelu.juntada.R.menu.groups;

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

        final Call<Group> call = server.createGroup(userId, groupName, body);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                Group group = response.body();
                saveGroup(group);
                newGroupActivity.groupCreated(group.getId());
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(context,"Group creation failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createEvent(PollRequest request, final NewEventActivity newEventActivity){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        final Call<Event> call = server.createEvent(request);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event event = response.body();
                saveEvent(event);
                newEventActivity.eventCreated(event);
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
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

        final Call<Poll> call = server.createPoll(request);
        call.enqueue(new Callback<Poll>() {
            @Override
            public void onResponse(Call<Poll> call, Response<Poll> response) {
                Poll poll = response.body();
                savePoll(poll);

                newPollActivity.pollCreated(poll);
            }

            @Override
            public void onFailure(Call<Poll> call, Throwable t) {
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

        Call<List<Group>> call = server.getGroups(userId);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.code() != 404) {
                    List<Group> groups = response.body();
                    updateGroups(userId, groups);
                    activity.updateGroups();
                } else {
                    Toast.makeText(context,"Error connecting to server", Toast.LENGTH_LONG).show();
                }
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
        try {
            for (User user : group.getUsers()) {
                userService.saveUser(user);
                userService.saveUserGroup(user.getId(), group.getId());
            }
        } catch (Exception e){
            //todo // FIXME: 17/07/17
        }
    }

    public void savePoll(Poll poll){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(poll);
        try {
            for (PollOption option : poll.getOptions()) {
                adapter.store(option);
            }
        } catch (Exception e){
        }
    }

    public void saveEvent(Event event){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(event);
    }

    public List<Event> getEvents(Long groupId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        return adapter.findAll(Event.class);
    }

    public PollRequest savePollRequest(PollRequest request){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(request);
        return adapter.findFirst(request);
    }

    public void deleteGroup(Long userId, Group group){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.delete(group);

        Participant participant = new Participant();
        participant.setGroupId(group.getId());
        participant.setUserId(userId);

        adapter.delete(participant);
    }

    public Group getGroupData(Long groupId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        Group group = new Group();
        group.setId(groupId);
        group = adapter.findFirst(group);

        List<Participant> participants = adapter.findAll(Participant.class, "group_id= ? ", new String[]{groupId.toString()});
        for (Participant participant : participants){
            User user = new User();
            user.setId(participant.getUserId());
            group.getUsers().add(adapter.findFirst(user));
        }

        return group;
    }

    public PollRequest getPollRequest(Long pollRequestId) {
        SqlAdapter adapter = Persistence.getAdapter(context);
        PollRequest request = new PollRequest();
        request.setId(pollRequestId);
        return adapter.findFirst(request);
    }

    public interface Callbacks{
        public void updateGroups();
    }
}
