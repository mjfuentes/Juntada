package com.nedelu.juntada.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nedelu.juntada.R;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.UserService;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private UserService userService;
    private Boolean loginClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
//                checkUser(newProfile);
          }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = new UserService(LoginActivity.this);


        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                checkUser(profile);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
        loginButton.setReadPermissions("user_friends");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginClicked= true;
            }
        });
        loginButton.registerCallback(callbackManager, callback);

    }

    private void checkUser(Profile profile){
        if(profile != null){
            User currentUser = userService.getUserByFacebookId(profile.getId());
            if (currentUser == null) {
                try {
                    userService.createUser(LoginActivity.this, profile.getId(), profile.getFirstName(), profile.getLastName(), profile.getProfilePictureUri(200, 200).toString());
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error during user creation", Toast.LENGTH_SHORT).show();
                }
            } else {
                nextActivity(currentUser);
            }
        }
    }

    public void nextActivity(User user){
        Intent main = new Intent(LoginActivity.this, GroupsActivity.class);
        main.putExtra("id", user.getId());
        main.putExtra("name", user.getFirstName());
        main.putExtra("surname", user.getLastName());
        main.putExtra("imageUrl", user.getImageUrl());

        SharedPreferences userPref = getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("id", user.getId());
        editor.apply();

        startActivity(main);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (loginClicked) {
            Profile profile = Profile.getCurrentProfile();
            checkUser(profile);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

}
