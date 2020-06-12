package com.svoemestodev.notifier;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NotifierService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private final Random mGenerator = new Random();
    private Timer timer;
    private boolean isWorking;
    private long hoursMinutesFrom;
    private long hoursMinutesTo;
    private MediaPlayer player;

    private long minutes;
    private String message;

    public class LocalBinder extends Binder {
        NotifierService getService() {
            return NotifierService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void stopTimer() {

        timer.cancel();
        isWorking = false;
        player = null;

    }


    public void startTimer(long minutes, long hoursMinutesFrom, long hoursMinutesTo, String message) {

        this.hoursMinutesFrom = hoursMinutesFrom;
        this.hoursMinutesTo = hoursMinutesTo;
        this.message = message;
        this.minutes = minutes;
        player = MediaPlayer.create(this, R.raw.alert);

        if (timer == null) {    // если таймер не запущен
            timer = new Timer();    // запускаем таймер
            timer.schedule(new timerTask(), minutes*60*1000,minutes*60*1000); // запускаем такс таймера
            isWorking = true;
        }

    }
    class timerTask extends TimerTask {

        @Override
        public void run() {

            Calendar rightNow = Calendar.getInstance();

            long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
            long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
            if (sinceMidnight >= hoursMinutesFrom && sinceMidnight <= hoursMinutesTo) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        player.start();
                        Toast toast = Toast.makeText(NotifierService.this.getApplicationContext(), message, Toast.LENGTH_LONG);
                        View viewToast = toast.getView();
                        viewToast.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        TextView textView = viewToast.findViewById(android.R.id.message);
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setTextColor(Color.WHITE);
                        toast.show();
                    }
                });
            }
        }
    };

}
