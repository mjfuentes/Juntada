package com.nedelu.juntada.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.gms.internal.ca;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nedelu.juntada.R;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.UserService;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putString("server_url", "http://10.1.1.4:8080");
//        editor.putString("server_url", "http://www.juntada.nedelu.com");
        editor.apply();

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ActivityManager.TaskDescription taskDesc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }



        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.nedelu.juntada", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("HASH", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("HASH", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("HASH", "printHashKey()", e);
        }

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        accessTokenTracker.startTracking();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = new UserService(LoginActivity.this);


        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            checkUser(profile2);
                            profileTracker.stopTracking();
                        }
                    };
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    checkUser(profile);
                }
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
                nextActivity(currentUser, false);
            }
        }
    }

    public void nextActivity(User user, Boolean newUser){
        Intent main = new Intent(LoginActivity.this, GroupsActivity.class);
        main.putExtra("id", user.getId());
        main.putExtra("name", user.getFirstName());
        main.putExtra("surname", user.getLastName());
        main.putExtra("imageUrl", user.getImageUrl());
        if (newUser){
            main.putExtra("reload",true);
            try {
                userService.registerUserToken(user, FirebaseInstanceId.getInstance().getToken());
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putLong("userId", user.getId());
        editor.apply();

        startActivity(main);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        checkUser(profile);
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

}
