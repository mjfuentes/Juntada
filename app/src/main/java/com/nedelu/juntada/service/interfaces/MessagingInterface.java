package com.nedelu.juntada.service.interfaces;

import com.nedelu.juntada.model.Message;
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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MessagingInterface {

    @POST("messages")
    Call<Message> createMessage(@Body Message message);
}
