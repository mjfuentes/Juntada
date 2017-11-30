package com.nedelu.juntada.service.interfaces;

import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.dto.AssitanceRequest;
import com.nedelu.juntada.model.dto.EventDTO;
import com.nedelu.juntada.model.dto.EventTokenDTO;
import com.nedelu.juntada.model.dto.FirebaseRegistration;
import com.nedelu.juntada.model.dto.GroupDTO;
import com.nedelu.juntada.model.dto.GroupTokenDTO;
import com.nedelu.juntada.model.dto.InvitedEventDTO;
import com.nedelu.juntada.model.dto.JoinEventDTO;
import com.nedelu.juntada.model.dto.JoinGroupDTO;
import com.nedelu.juntada.model.dto.PollConfirmDTO;
import com.nedelu.juntada.model.dto.PollDTO;
import com.nedelu.juntada.model.dto.PollVoteRequest;
import com.nedelu.juntada.model.dto.UserDTO;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ServerInterface {

    @POST("users")
    Call<UserDTO> createUser(@Body User user);

    @POST("users/{userId}/firebase")
    Call<UserDTO> registerFirebase(@HeaderMap Map<String, String> headers, @Path("userId") Long userId, @Body FirebaseRegistration registration);

    @POST("polls")
    Call<PollDTO> createPoll(@HeaderMap Map<String, String> headers, @Body PollRequest request);

    @POST("polls/{pollId}/vote")
    Call<PollDTO> votePoll(@HeaderMap Map<String, String> headers,@Path("pollId") Long pollId, @Body PollVoteRequest request);

    @POST("events")
    Call<EventDTO> createEvent(@HeaderMap Map<String, String> headers,@Body PollRequest request);

    @GET("users/{userID}/groups")
    Call<List<GroupDTO>> getGroups(@HeaderMap Map<String, String> headers,@Path("userID") Long id);

    @GET("groups/{groupId}")
    Call<GroupDTO> getGroup(@HeaderMap Map<String, String> headers,@Path("groupId") Long groupId);

    @GET("groups/{groupId}/token")
    Call<GroupTokenDTO> getGroupToken(@HeaderMap Map<String, String> headers,@Path("groupId") Long groupId);

    @Multipart
    @POST("users/{userID}/groups")
    Call<GroupDTO> createGroup(@HeaderMap Map<String, String> headers,@Path("userID") Long id, @Part("name") RequestBody name, @Part MultipartBody.Part file);

    @POST("groups/member")
    Call<GroupDTO> joinGroup(@HeaderMap Map<String, String> headers,@Body JoinGroupDTO joinGroup);

    @POST("events/{eventId}/assistant")
    Call<EventDTO> saveAssistance(@HeaderMap Map<String, String> headers,@Path("eventId") Long eventId, @Body AssitanceRequest request);

    @DELETE("users/{userID}/groups/{groupId}")
    Call<UserDTO> deleteGroup(@HeaderMap Map<String, String> headers,@Path("userID") Long userId ,@Path("groupId") Long groupId);

    @POST("polls/{pollId}")
    Call<EventDTO> confirmPoll(@HeaderMap Map<String, String> headers,@Path("pollId") Long pollId, @Body PollConfirmDTO request);

    @GET("events/{eventId}/token")
    Call<EventTokenDTO> getEventToken(@HeaderMap Map<String, String> headers,@Path("eventId") Long eventId);

    @POST("events/member")
    Call<InvitedEventDTO> joinEvent(@HeaderMap Map<String, String> headers,@Body JoinEventDTO joinEvent);

    @POST("groups/{groupId}")
    Call<GroupDTO> updateGroupName(@HeaderMap Map<String, String> headers,@Path("groupId") Long groupId,@Body GroupDTO groupDTO);

    @Multipart
    @POST("groups/{groupId}/image")
    Call<GroupDTO> updateGroupImage(@HeaderMap Map<String, String> headers,@Path("groupId") Long groupId,@Part MultipartBody.Part body);

    @GET("events/{eventId}")
    Call<InvitedEventDTO> getEvent(@HeaderMap Map<String, String> headers,@Path("eventId") Long eventId);

    @GET("polls/{pollId}")
    Call<PollDTO> getPoll(@HeaderMap Map<String, String> headers,@Path("pollId") Long pollId);

    @GET("users/{userId}/events")
    Call<List<InvitedEventDTO>> getEventsForUser(@HeaderMap Map<String, String> headers,@Path("userId") Long userId);

    @POST("events/{eventId}")
    Call<EventDTO> updateEvent(@HeaderMap Map<String, String> headers,@Path("eventId") Long id,@Body EventDTO event);

    @DELETE("events/{eventId}")
    Call<Boolean> deleteEvent(@HeaderMap Map<String, String> headers,@Path("eventId") Long eventId);
}
