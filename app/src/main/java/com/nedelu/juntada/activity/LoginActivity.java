package com.nedelu.juntada.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nedelu.juntada.R;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.UserService;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private UserService userService;
    private Boolean loginClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = userPref.edit();
//        editor.putString("server_url", "http://10.1.1.3:8080");
//        editor.putString("server_url", "http://www.demo.juntada.nedelu.com");
        editor.apply();
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userService = new UserService(LoginActivity.this);
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = userPref.edit();

                    User user = userService.getUserByFirebaseId(firebaseUser.getUid());
                    if (user == null) {
                        if (firebaseUser.getDisplayName() != null) {
                            String photo = (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : "";
                            userService.createUser(LoginActivity.this, firebaseUser.getUid(), firebaseUser.getDisplayName(), "", photo);
                        } else {
                           firebaseAuth.signOut();
                        }
                    } else {
                        nextActivity(user, false);
                    }
                }
            }
        });

        showSignInScreen();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    finish();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

        }
    }

    private void showSnackbar(int stringId){

    }

    private void showSignInScreen() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                        .setIsSmartLockEnabled(false)
                        .setAllowNewEmailAccounts(true)
                        .setLogo(R.drawable.login_logo_new)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                1);
    }


    public void nextActivity(User user, Boolean newUser){
        Intent main = new Intent(LoginActivity.this, GroupsActivity.class);
        main.putExtra("reload",true);
        if (newUser){
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





}
