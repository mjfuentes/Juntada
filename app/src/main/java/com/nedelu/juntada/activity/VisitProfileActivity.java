package com.nedelu.juntada.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.UserService;
import com.nedelu.juntada.transform.CircleTransform;
import com.squareup.picasso.Picasso;

public class VisitProfileActivity extends AppCompatActivity {

    private UserService userService;
    private Long userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_profile);

        if (getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        userService = new UserService(VisitProfileActivity.this);

        Bundle inBundle = getIntent().getExtras();
        userId = Long.valueOf(inBundle.get("id").toString());
        user = userService.getUser(userId);

        TextView name = (TextView) findViewById(R.id.user_name);
        ImageView imageView = (ImageView) findViewById(R.id.user_profile_photo);
        name.setText(user.getFirstName() + " " + user.getLastName());
        Picasso.with(this).load(user.getImageUrl()).transform(new CircleTransform()).into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
