package ca.cmpt276.titanium.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean dismissed = intent.getBooleanExtra("dismissed", false);
        boolean cancelled = intent.getBooleanExtra("cancel", false);
        boolean paused = intent.getBooleanExtra("cancel", false);
        boolean resumed = intent.getBooleanExtra("cancel", false);

        if (dismissed) {
            TimerActivity.dismissNotification(context.getApplicationContext());
            TimerActivity.toggleVibrations(context.getApplicationContext(), false);
        } else if (cancelled) {
            TimerActivity.cancelTimerNotification(context.getApplicationContext());
        } else if (paused) {
            TimerActivity.pauseTimerNotification(context.getApplicationContext());
        } else if (resumed) {
            TimerActivity.resumeTimerNotification(context.getApplicationContext());
        }
    }
}
