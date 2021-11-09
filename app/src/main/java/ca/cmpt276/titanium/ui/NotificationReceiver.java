package ca.cmpt276.titanium.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean dismissed = intent.getBooleanExtra("dismissed", false);

        System.out.println("dismissed");
        if (dismissed) {
            System.out.println("dismissed2");
            TimerActivity.dismissNotification(context.getApplicationContext());
            TimerActivity.toggleVibrations(context.getApplicationContext(), false);
        }
    }
}
