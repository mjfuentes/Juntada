package com.nedelu.juntada.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.service.GroupService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewEventActivity extends AppCompatActivity {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Calendar myCalendar;
    private EditText editDate;
    private TextInputLayout editName;
    private TextView dateView;
    private TextView timeView;
    private TextInputLayout editLocation;
    private ImageView calendarImage;
    private ImageView timeImage;
    private RadioGroup editType;
    private Spinner editTime;
    private Place selectedPlace;
    private Long userId;
    private Long groupId;
    private GroupService groupService;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        groupId =  userPref.getLong("groupId", 0L);

        editTime = (Spinner) findViewById(R.id.edit_time);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.horarios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTime.setAdapter(adapter);

        groupService = GroupService.getInstance(NewEventActivity.this);

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        editDate = (EditText) findViewById(R.id.edit_date);
        editName = (TextInputLayout) findViewById(R.id.edit_name);
        editType = (RadioGroup) findViewById(R.id.edit_type);
        dateView = (TextView) findViewById(R.id.date_view);
        timeView = (TextView) findViewById(R.id.time_view);
        calendarImage = (ImageView) findViewById(R.id.calendar_image);
        timeImage = (ImageView) findViewById(R.id.time_image);


        editType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                if (i == R.id.votacion){
                    editDate.setVisibility(View.INVISIBLE);
                    editTime.setVisibility(View.INVISIBLE);
                    dateView.setVisibility(View.INVISIBLE);
                    timeView.setVisibility(View.INVISIBLE);
                    calendarImage.setVisibility(View.INVISIBLE);
                    timeImage.setVisibility(View.INVISIBLE);

                } else {
                    editDate.setVisibility(View.VISIBLE);
                    editTime.setVisibility(View.VISIBLE);
                    dateView.setVisibility(View.VISIBLE);
                    timeView.setVisibility(View.VISIBLE);
                    calendarImage.setVisibility(View.VISIBLE);
                    timeImage.setVisibility(View.VISIBLE);
                }

            }
        });

        editDate.setVisibility(View.INVISIBLE);
        editTime.setVisibility(View.INVISIBLE);
        dateView.setVisibility(View.INVISIBLE);
        timeView.setVisibility(View.INVISIBLE);
        calendarImage.setVisibility(View.INVISIBLE);
        timeImage.setVisibility(View.INVISIBLE);

        editLocation = (TextInputLayout) findViewById(R.id.edit_location);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        editDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    new DatePickerDialog(NewEventActivity.this, R.style.DialogTheme,date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.add_event);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()){
                    PollRequest request = new PollRequest();
                    request.setGroupId(groupId);
                    request.setCreatorId(userId);
                    request.setTitle(editName.getEditText().getText().toString());
                    request.setLocation(editLocation.getEditText().getText().toString());
                    int radioButtonID = editType.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) editType.findViewById(radioButtonID);
                    List<PollOption> options = null;
                    if (radioButton.getText().equals("Votacion")){
                        PollRequest savedRequest = groupService.savePollRequest(request);
                        Intent main = new Intent(NewEventActivity.this, NewPollActivity.class);
                        main.putExtra("userId", userId);
                        main.putExtra("groupId", groupId);
                        main.putExtra("pollRequestId", savedRequest.getId());
                        startActivity(main);
                        finish();
                    } else {
                        options = new ArrayList<>();
                        PollOption option = new PollOption();
                        option.setDate(myCalendar.getTime().toString());
                        option.setTime(editTime.getSelectedItem().toString());
                        options.add(option);
                        request.setOptions(options);
                        progressBar.setVisibility(View.VISIBLE);
                        groupService.createEvent(request,NewEventActivity.this);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editLocation.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(NewEventActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
            }
        });

    }

    private boolean checkFields() {

        return !editName.getEditText().getText().toString().equals("") &&
                !editLocation.getEditText().getText().toString().equals("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                selectedPlace = PlaceAutocomplete.getPlace(this, data);
                String placeDetailsStr = selectedPlace.getName().toString();
                editLocation.getEditText().setText(placeDetailsStr);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        editDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void eventCreated(Event event){
        progressBar.setVisibility(View.INVISIBLE);
        finish();

    }


}
