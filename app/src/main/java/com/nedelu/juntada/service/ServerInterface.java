package com.nedelu.juntada.service;

import com.nedelu.juntada.model.User;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public interface ServerInterface {

    @POST("/users")
    User createUser(@Body User user);

}
