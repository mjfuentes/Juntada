package com.nedelu.juntada.service;

import android.content.Context;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.nedelu.juntada.model.User;

import retrofit2.Retrofit;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class UserService {

    private Context context;

    public UserService(Context context) {
        this.context = context;
    }

    public User getUser(String facebookId){
        SqlAdapter adapter = Persistence.getAdapter(context);
        return adapter.findFirst(User.class, "facebook_id = ", new String[]{facebookId});
    }

    public User createUser(String facebookId, String name, String lastName, String imageUrl){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
}
