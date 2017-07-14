package com.nedelu.juntada.service.interfaces;

import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public interface ServerInterface {

    @POST("users")
    Call<Long> createUser(@Body User user);

    @GET("users/{userID}/groups")
    Call<List<Group>> getGroups(@Path("userID") Long id);

    @POST("users/{userID}/groups")
    Call<Group> createGroup(@Path("userID") Long id, @Body Group group);

}
