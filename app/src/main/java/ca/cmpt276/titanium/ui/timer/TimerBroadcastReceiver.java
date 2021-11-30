package ca.cmpt276.titanium.ui.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Timer;

/**
 * Allows a user to interact with a timer while away from TimerActivity.
 *
 * @author Titanium
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
        timerNotification.launchNotification(
            context.getString(R.string.button_timer_notification_resume));
        break;
      case "Resume":
        Intent timerServiceIntent = new Intent(context.getApplicationContext(), TimerService.class);
        context.getApplicationContext().startService(timerServiceIntent);
        timerNotification.launchNotification(
            context.getString(R.string.button_timer_notification_pause));
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
