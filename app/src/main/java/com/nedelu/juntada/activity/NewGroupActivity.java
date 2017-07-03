package com.nedelu.juntada.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.manager.GroupManager;
import com.nedelu.juntada.model.Group;

import java.util.Objects;

public class NewGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Button createButton = (Button) findViewById(R.id.create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextName = (EditText) findViewById(R.id.editTextName);
                EditText editTextDescription = (EditText) findViewById(R.id.editTextDescription);
                EditText editTextImage = (EditText) findViewById(R.id.editTextImage);

                if (verifyFields(editTextName, editTextDescription, editTextImage)){
                    Group group = new Group();
                    group.setName(editTextName.getText().toString());
                    group.setImageUrl("http://thomrainer.com/wp-content/uploads/2013/10/Start-New-Groups.jpg");

                    //TODO SEND GROUP TO SERVER

                    GroupManager.getInstance().addGroup(group);
                    finish();
                } else {
                    Snackbar.make(view, "All fields must be completed!.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private boolean verifyFields(EditText editTextName, EditText editTextDescription, EditText editTextImage) {
        return !editTextName.getText().toString().equals("") &&
                !editTextDescription.getText().toString().equals("") &&
                    !editTextImage.getText().toString().equals("");
    }
}
