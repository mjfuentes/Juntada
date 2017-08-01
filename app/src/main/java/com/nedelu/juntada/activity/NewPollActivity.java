package com.nedelu.juntada.activity;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.nedelu.juntada.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewPollActivity extends AppCompatActivity {

    private DateAdapter dateAdapter;
    private GridView dateList;
    private Calendar myCalendar;
    private EditText editDate;
    private Long userId;
    private Long groupId;
    private Long pollRequestId;
    private Spinner editTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        Bundle inBundle = getIntent().getExtras();
        userId = Long.valueOf(inBundle.get("userId").toString());
        groupId = Long.valueOf(inBundle.get("groupId").toString());
        pollRequestId = Long.valueOf(inBundle.get("pollRequestId").toString());

        editDate = (EditText) findViewById(R.id.edit_date);
        editTime = (Spinner) findViewById(R.id.edit_time);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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

        Button addDate = (Button) findViewById(R.id.add_date);
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateAdapter.addDate(myCalendar.getTime());
            }
        });

        editDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    new DatePickerDialog(NewPollActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        editDate.setText(sdf.format(myCalendar.getTime()));
    }

    private class DateAdapter extends BaseAdapter {

        private Context context;
        private List<Date> dates = new ArrayList<>();

        public DateAdapter(Context context){
            this.context = context;
        }

        public void addDate(Date date) {
            this.dates.add(date);
            this.notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return dates.size();
        }

        @Override
        public Object getItem(int i) {
            return dates.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.date_item, viewGroup, false);
            TextView textView = (TextView) rowView.findViewById(R.id.date_text);

            String myFormat = "dd/MM/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

            textView.setText(sdf.format(dates.get(i)));

            return textView;
        }
    }
}
