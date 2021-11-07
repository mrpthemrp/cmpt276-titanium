package ca.cmpt276.titanium.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String sound = intent.getStringExtra("sound");
        if (sound.equals("off")) {
            TimerActivity.stopSound();
            TimerActivity.startStopVibrations(context, "off");
        }
    }
}
