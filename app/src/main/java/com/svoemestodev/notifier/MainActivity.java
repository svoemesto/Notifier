package com.svoemestodev.notifier;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    EditText et_minutes;
    EditText et_text;
    Button bt_doit;

    private static final long hoursMinutesFrom = 10*60*60*1000;
    private static final long hoursMinutesTo = 20*60*60*1000;
    private static boolean isWorking;
    private static long minutes;
    private static String message;
    private Timer timer;
    private MediaPlayer player;
    private boolean isPlayerRunning;

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
        player = MediaPlayer.create(this, R.raw.alert);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!isPlayerRunning) {
                    player.pause();
                } else {
                    player.start();
                }

            }
        });

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
            stopTimer();
        } else {
            bt_doit.setText("Стоп");
            startTimer();
        }

    }

    private void stopTimer() {

        timer.cancel();
        isWorking = false;

    }


    private void startTimer() {

        if (timer == null) {    // если таймер не запущен
            timer = new Timer();    // запускаем таймер
            timer.schedule(new timerTask(), minutes*60*1000,minutes*60*1000); // запускаем такс таймера
            isWorking = true;
        }

    }
    class timerTask extends TimerTask {

        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Calendar rightNow = Calendar.getInstance();

                    long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
                    long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
                    if (sinceMidnight >= hoursMinutesFrom && sinceMidnight <= hoursMinutesTo) {
                        player.start();
                        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
                        View viewToast = toast.getView();
                        viewToast.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        TextView textView = viewToast.findViewById(android.R.id.message);
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setTextColor(Color.WHITE);
                        toast.show();
                    }


                }
            });
        }
    };

}