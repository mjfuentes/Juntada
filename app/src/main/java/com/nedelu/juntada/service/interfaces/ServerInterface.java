package com.nedelu.juntada.service.interfaces;

import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.GroupDTO;
import com.nedelu.juntada.model.dto.PollDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public interface ServerInterface {

    @POST("users")
    Call<Long> createUser(@Body User user);

    @POST("polls")
    Call<PollDTO> createPoll(@Body PollRequest request);

    @POST("events")
    Call<EventDTO> createEvent(@Body PollRequest request);

    @GET("users/{userID}/groups")
    Call<List<GroupDTO>> getGroups(@Path("userID") Long id);

    @GET("groups/{groupId}")
    Call<GroupDTO> getGroup(@Path("groupId") Long groupId);

    @Multipart
    @POST("users/{userID}/groups")
    Call<GroupDTO> createGroup(@Path("userID") Long id, @Part("name") RequestBody name, @Part MultipartBody.Part file);

}
