package com.nedelu.juntada.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.Profile;
import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.service.GroupService;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class NewGroupTwoActivity extends AppCompatActivity {

    private Long userId;
    private Long groupId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_two);

        Bundle inBundle = getIntent().getExtras();
        userId = Long.valueOf(inBundle.get("userId").toString());
        groupId = Long.valueOf(inBundle.get("groupId").toString());
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        FloatingActionButton createButton = (FloatingActionButton) findViewById(R.id.add_members_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo get token from server
                String GROUP_TOKEN = "ABCD1234";

                String url = "http://10.1.1.16:8080/joinGroup/" + GROUP_TOKEN;

                progressBar.setVisibility(View.VISIBLE);

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Juntada");
                String sAux = "\nTe invite a mi Grupo de Juntada! Para ingresar usa el siguiente link:\n\n";
                sAux += "\n"+ url + "\n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Elegir aplicacion"));
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

}
