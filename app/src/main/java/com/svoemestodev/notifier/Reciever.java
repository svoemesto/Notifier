package com.svoemestodev.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class Reciever extends BroadcastReceiver {

    private MediaPlayer player;

    @Override
    public void onReceive(Context context, Intent intent) {

        long minutes = intent.getLongExtra("minutes", 20);
        long timeFrom = intent.getLongExtra("timeFrom", 10*60);
        long timeTo = intent.getLongExtra("timeTo", 10*60);
        String message = intent.getStringExtra("message");
        if (player == null) {
            player = MediaPlayer.create(context, R.raw.alert);
        }

        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
        long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
        if (sinceMidnight >= timeFrom*60*1000 && sinceMidnight <= timeTo*60*1000) {

            player.start();
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            View viewToast = toast.getView();
            viewToast.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            TextView textView = viewToast.findViewById(android.R.id.message);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.WHITE);
            toast.show();

        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + minutes*60*1000, pendingIntent);

    }
}
