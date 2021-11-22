package ca.cmpt276.titanium.ui.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Timer;

/**
 * NotificationReceiver is called when the user interacts with timer notification actions.
 */
public class TimerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TimerNotification timerNotification = TimerNotification.getInstance(context);
        Timer timer = Timer.getInstance(context);

        String action = intent.getStringExtra("notificationAction");
        action = action == null ? "" : action;

        switch (action) {
            case "Dismiss":
                timerNotification.dismissNotification(false);
                break;
            case "Pause":
                timer.setPaused();
                timerNotification.launchNotification(context.getString(R.string.timer_notification_resume_button));
                break;
            case "Resume":
                context.getApplicationContext().startService(new Intent(context.getApplicationContext(), TimerService.class));
                timerNotification.launchNotification(context.getString(R.string.timer_notification_pause_button));
                break;
            case "Cancel":
                timer.setStopped();
                timerNotification.dismissNotification(true);
                break;
            default:
                throw new IllegalArgumentException("Invalid notificationAction");
        }
    }
}
