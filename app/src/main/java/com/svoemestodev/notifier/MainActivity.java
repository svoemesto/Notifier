package com.svoemestodev.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText et_minutes;
    EditText et_text;
    Button bt_doit;
    Button btn_time_from;
    Button btn_time_to;

    private static long hoursMinutesFrom = 10*60;
    private static long hoursMinutesTo = 20*60;

    private static long minutes;
    private static String message;

    private boolean isWorking;

    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_minutes = findViewById(R.id.et_minutes);
        et_text = findViewById(R.id.et_text);
        bt_doit = findViewById(R.id.bt_doit);
        btn_time_from = findViewById(R.id.btn_time_from);
        btn_time_to = findViewById(R.id.btn_time_to);

        String strTimeFrom = "с: " + String.format(Locale.getDefault(), "%02d:%02d", Math.abs(hoursMinutesFrom) / 60, Math.abs(hoursMinutesFrom) % 60);
        String strTimeTo = "по: " + String.format(Locale.getDefault(), "%02d:%02d", Math.abs(hoursMinutesTo) / 60, Math.abs(hoursMinutesTo) % 60);
        btn_time_from.setText(strTimeFrom);
        btn_time_to.setText(strTimeTo);


        et_minutes.setText(String.valueOf(30));
        et_text.setText("Пора делать зарядку!");
        bt_doit.setText("Старт");

        et_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    minutes = Math.abs(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    minutes = 30;
                }
            }

        });

        et_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                message = s.toString();
            }

        });

    }

    public void doIt(View view) {

        et_minutes.setEnabled(isWorking);
        et_text.setEnabled(isWorking);

        try {
            minutes = Math.abs(Integer.parseInt(et_minutes.getText().toString()));
        } catch (NumberFormatException e) {
            minutes = 30;
        }
        message = et_text.getText().toString();

        Intent intent = new Intent(this, Reciever.class);
        intent.putExtra("minutes", minutes);
        intent.putExtra("message", message);
        intent.putExtra("timeFrom", hoursMinutesFrom);
        intent.putExtra("timeTo", hoursMinutesTo);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (isWorking) {
            bt_doit.setText("Старт");
            am.cancel(pendingIntent);
        } else {
            bt_doit.setText("Стоп");
            am.cancel(pendingIntent);
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + minutes*60*1000, pendingIntent);
        }
        isWorking = !isWorking;

    }


    public void setTimeFrom(View view) {
        new TimePickerDialog(MainActivity.this, timeFrom, (int)hoursMinutesFrom/60, (int)hoursMinutesFrom%60, true).show();
    }

    public void setTimeTo(View view) {
        new TimePickerDialog(MainActivity.this, timeTo, (int)hoursMinutesTo/60, (int)hoursMinutesTo%60, true).show();
    }


    TimePickerDialog.OnTimeSetListener timeFrom =new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hoursMinutesFrom = hourOfDay*60 + minute;
            String strTimeFrom = "с: " + String.format(Locale.getDefault(), "%02d:%02d", Math.abs(hoursMinutesFrom) / 60, Math.abs(hoursMinutesFrom) % 60);
            btn_time_from.setText(strTimeFrom);
        }
    };

    TimePickerDialog.OnTimeSetListener timeTo =new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hoursMinutesTo = hourOfDay*60 + minute;
            String strTimeTo = "по: " + String.format(Locale.getDefault(), "%02d:%02d", Math.abs(hoursMinutesTo) / 60, Math.abs(hoursMinutesTo) % 60);
            btn_time_to.setText(strTimeTo);
        }
    };

}