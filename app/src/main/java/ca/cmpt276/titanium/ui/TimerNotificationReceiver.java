package ca.cmpt276.titanium.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Timer;

/**
 * NotificationReceiver is called when the user interacts with timer notification actions.
 */
public class TimerNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TimerNotifications timerNotifications = TimerNotifications.getInstance(context);
        Timer timer = Timer.getInstance(context);

        String action = intent.getStringExtra("notificationAction");
        action = action == null ? "" : action;

        switch (action) {
            case "Dismiss":
                timerNotifications.dismissNotification(false);
                break;
            case "Pause":
                timer.setPaused();
                timerNotifications.launchNotification(context.getString(R.string.timer_notification_resume_button));
                break;
            case "Resume":
                context.getApplicationContext().startService(new Intent(context.getApplicationContext(), TimerService.class));
                timerNotifications.launchNotification(context.getString(R.string.timer_notification_pause_button));
                break;
            case "Cancel":
                timer.setStopped();
                timerNotifications.dismissNotification(true);
                break;
            default:
                throw new IllegalArgumentException("Invalid notificationAction");
        }
    }
}
