package com.nedelu.juntada.firebase;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nedelu.juntada.service.UserService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private UserService userService;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]


    private void sendRegistrationToServer(String token) {
        if (userService == null){
            userService = new UserService(MyFirebaseInstanceIDService.this);
        }

        SharedPreferences userPref = getSharedPreferences("user", 0);
        Long userId = userPref.getLong("id", 0L);
        try {
            userService.registerUserToken(userService.getUser(userId), token);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}