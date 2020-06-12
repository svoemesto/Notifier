package com.svoemestodev.notifier;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    EditText et_minutes;
    EditText et_text;
    Button bt_doit;

    private static final long hoursMinutesFrom = 10*60*60*1000;
    private static final long hoursMinutesTo = 23*60*60*1000;

    private static long minutes;
    private static String message;

    NotifierService mService;
    boolean mBound = false;
    private boolean isWorking;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, NotifierService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            NotifierService.LocalBinder binder = (NotifierService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_minutes = findViewById(R.id.et_minutes);
        et_text = findViewById(R.id.et_text);
        bt_doit = findViewById(R.id.bt_doit);

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


        if (isWorking) {
            bt_doit.setText("Старт");
            mService.stopTimer();
        } else {
            bt_doit.setText("Стоп");
            mService.startTimer(minutes, hoursMinutesFrom, hoursMinutesTo, message);
        }
        isWorking = !isWorking;

    }



}