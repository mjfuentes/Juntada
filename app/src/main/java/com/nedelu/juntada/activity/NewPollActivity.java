package com.nedelu.juntada.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.PollRequest;
import com.nedelu.juntada.service.GroupService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewPollActivity extends AppCompatActivity {

    private GroupService groupService;
    private DateAdapter dateAdapter;
    private GridView dateList;
    private Calendar myCalendar;
    private EditText editDate;
    private ImageView dateImage;
    private Long userId;
    private Long groupId;
    private Long pollRequestId;
    private PollRequest request;
    private Spinner editTime;
    private FloatingActionButton button;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        Bundle inBundle = getIntent().getExtras();
        SharedPreferences userPref = getSharedPreferences("user", 0);
        userId = userPref.getLong("userId", 0L);
        groupId =  userPref.getLong("groupId", 0L);
        pollRequestId = Long.valueOf(inBundle.get("pollRequestId").toString());

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        groupService = GroupService.getInstance(NewPollActivity.this);
        request = groupService.getPollRequest(pollRequestId);

        getSupportActionBar().setTitle(request.getTitle());

        editDate = (EditText) findViewById(R.id.edit_date);
        editTime = (Spinner) findViewById(R.id.edit_time);
        dateImage = (ImageView) findViewById(R.id.date_image);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.horarios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTime.setAdapter(adapter);

        dateAdapter = new DateAdapter(NewPollActivity.this);
        dateList = (GridView) findViewById(R.id.date_list);
        dateList.setAdapter(dateAdapter);

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

        final ImageButton addDate = (ImageButton) findViewById(R.id.add_date);
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields() && dateAdapter.getCount() < 4) {
                    PollOption option = new PollOption();
                    option.setDate(myCalendar.getTime());
                    option.setTime(editTime.getSelectedItem().toString());
                    dateAdapter.addDate(option);

                    if (dateAdapter.getCount() == 4){
                        addDate.setVisibility(View.INVISIBLE);
                    }

                    button.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button = (FloatingActionButton) findViewById(R.id.add_event);
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                request.setOptions(dateAdapter.getItems());
                groupService.createPoll(request, NewPollActivity.this);
            }
        });

        dateImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(NewPollActivity.this,R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        editDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void pollCreated(Poll poll) {
        this.finish();
    }

    private class DateAdapter extends BaseAdapter {

        private Context context;
        private List<PollOption> options = new ArrayList<>();

        public DateAdapter(Context context){
            this.context = context;
        }

        public void addDate(PollOption date) {
            this.options.add(date);
            this.notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Object getItem(int i) {
            return options.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.poll_item, viewGroup, false);
            TextView dateText = (TextView) rowView.findViewById(R.id.text_date);
            TextView timeText = (TextView) rowView.findViewById(R.id.text_time);

            String myFormat = "dd/MM";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

            timeText.setText(options.get(i).getTime());
            dateText.setText(sdf.format(options.get(i).getDate()));

            return rowView;
        }

        public List<PollOption> getItems() {
            return options;
        }
    }

    private boolean checkFields() {

        return !editDate.getText().toString().equals("") &&
                !(editTime.getSelectedItem() == null);
    }
}
