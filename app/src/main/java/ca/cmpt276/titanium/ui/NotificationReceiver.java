package ca.cmpt276.titanium.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.TimerData;

/**
 * NotificationReceiver is called when the user interacts with timer notification actions.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TimerNotifications timerNotifications = TimerNotifications.getInstance(context);
        TimerData timerData = TimerData.getInstance(context);

        String action = intent.getStringExtra("notificationAction");
        action = action == null ? "" : action;

        switch (action) {
            case "Dismiss":
                timerNotifications.dismissNotification(false);
                break;
            case "Pause":
                timerData.setPaused();
                timerNotifications.launchNotification(context.getString(R.string.timer_notification_resume_button));
                break;
            case "Resume":
                context.getApplicationContext().startService(new Intent(context.getApplicationContext(), TimerService.class));
                timerNotifications.launchNotification(context.getString(R.string.timer_notification_pause_button));
                break;
            case "Cancel":
                timerData.setStopped();
                timerNotifications.dismissNotification(true);
                break;
            default:
                throw new IllegalArgumentException("Invalid notificationAction");
        }
    }
}
