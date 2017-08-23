package com.nedelu.juntada.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.nedelu.juntada.R;
import com.nedelu.juntada.service.GroupService;

public class NewGroupTwoActivity extends AppCompatActivity implements TokenResultActivity {

    private Long userId;
    private Long groupId;
    private ProgressBar progressBar;
    private GroupService groupService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_two);

        Bundle inBundle = getIntent().getExtras();
        userId = Long.valueOf(inBundle.get("userId").toString());
        groupId = Long.valueOf(inBundle.get("groupId").toString());
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        groupService = GroupService.getInstance(NewGroupTwoActivity.this);

        FloatingActionButton createButton = (FloatingActionButton) findViewById(R.id.add_members_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupService.getGroupToken(groupId, NewGroupTwoActivity.this);
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

    @Override
    public void tokenGenerated(String token) {
        String url = "http://www.juntada.nedelu.com/join/" + token;

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Juntada");
        String sAux = "\nTe invite a mi grupo de Juntada! Para ingresar usa el siguiente link:\n\n";
        sAux += "\n"+ url;
        i.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(i, "Elegir aplicacion"));
    }
}
