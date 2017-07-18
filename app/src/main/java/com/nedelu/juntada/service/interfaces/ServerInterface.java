package com.nedelu.juntada.service.interfaces;

import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;

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

    @GET("users/{userID}/groups")
    Call<List<Group>> getGroups(@Path("userID") Long id);

    @Multipart
    @POST("users/{userID}/groups")
    Call<Group> createGroup(@Path("userID") Long id, @Part("name") RequestBody name, @Part MultipartBody.Part file);

}
