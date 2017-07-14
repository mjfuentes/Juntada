package com.nedelu.juntada.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.manager.GroupManager;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.service.GroupService;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class NewGroupActivity extends AppCompatActivity {

    private Long userId;
    private ImageView imageView;
    private static final int RESULT_LOAD_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Bundle inBundle = getIntent().getExtras();
        userId = Long.valueOf(inBundle.get("id").toString());

        imageView = (ImageView) findViewById(R.id.upload_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        Button createButton = (Button) findViewById(R.id.create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextName = (EditText) findViewById(R.id.editTextName);
                EditText editTextDescription = (EditText) findViewById(R.id.editTextDescription);
//                EditText editTextImage = (EditText) findViewById(R.id.editTextImage);

                if (verifyFields(editTextName, editTextDescription)){
                    Group group = new Group();
                    group.setName(editTextName.getText().toString());
                    group.setImageUrl("http://thomrainer.com/wp-content/uploads/2013/10/Start-New-Groups.jpg");

                    //TODO SEND GROUP TO SERVER

                    GroupService.getInstance(NewGroupActivity.this).createGroup(userId, group, NewGroupActivity.this);
                } else {
                    Snackbar.make(view, "All fields must be completed!.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    private boolean verifyFields(EditText editTextName, EditText editTextDescription) {
        return !editTextName.getText().toString().equals("") && !editTextDescription.getText().toString().equals("");
    }

    public void groupCreated(){
        finish();
    }
}
