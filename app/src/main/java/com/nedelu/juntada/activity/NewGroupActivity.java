package com.nedelu.juntada.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.manager.GroupManager;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.service.GroupService;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

public class NewGroupActivity extends AppCompatActivity {

    private Long userId;
    private ImageView imageView;
    private Uri imageUri;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CROP_ICON = 2;
    private ProgressBar progressBar;
    private FloatingActionButton createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Bundle inBundle = getIntent().getExtras();
        userId = Long.valueOf(inBundle.get("id").toString());
        progressBar = (ProgressBar) findViewById(R.id.button_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        imageView = (ImageView) findViewById(R.id.upload_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(4,3)
                        .setRequestedSize(800,600)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(NewGroupActivity.this);

            }
        });

        createButton = (FloatingActionButton) findViewById(R.id.create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextName = (EditText) findViewById(R.id.editTextName);
//                EditText editTextImage = (EditText) findViewById(R.id.editTextImage);

                if (verifyFields(editTextName)){
                    View v = NewGroupActivity.this.getCurrentFocus();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    createButton.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    Group group = new Group();
                    group.setName(StringEscapeUtils.escapeJava(editTextName.getText().toString()));
                    GroupService.getInstance(NewGroupActivity.this).createGroup(NewGroupActivity.this, userId, group,imageUri, NewGroupActivity.this);
                } else {
                    Snackbar.make(view, R.string.please_fill_all_fields, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Picasso.with(NewGroupActivity.this).load(imageUri).into(imageView);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private boolean verifyFields(EditText editTextName) {
        return !editTextName.getText().toString().equals("") && imageUri != null;
    }

    public void groupCreated(Long groupId){

        if (groupId != null) {
            Intent main = new Intent(NewGroupActivity.this, NewGroupTwoActivity.class);
            main.putExtra("userId", userId);
            main.putExtra("groupId", groupId);
            startActivity(main);
            finish();
        } else {
            createButton.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
