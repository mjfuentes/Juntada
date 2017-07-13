package com.nedelu.juntada.service;

import android.content.Context;
import android.widget.Toast;

import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.nedelu.juntada.activity.LoginActivity;
import com.nedelu.juntada.model.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        return adapter.findFirst(User.class, "facebook_id = ?", new String[]{facebookId});
    }

    public void saveUser(User user){
        SqlAdapter adapter = Persistence.getAdapter(context);
        adapter.store(user);
    }

    public User createUser(final LoginActivity activity, String facebookId, String name, String lastName, String imageUrl) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.16:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerInterface server = retrofit.create(ServerInterface.class);

        final User user = new User();
        user.setFirstName(name);
        user.setLastName(lastName);
        user.setFacebookId(facebookId);
        user.setImageUrl(imageUrl);
        Call<Long> call = server.createUser(user);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Long userId = response.body();
                user.setId(userId);
                saveUser(user);
                activity.nextActivity(user);

            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Toast.makeText(context,"Registration failed!", Toast.LENGTH_LONG).show();
            }
        });

        return user;
    }
}
