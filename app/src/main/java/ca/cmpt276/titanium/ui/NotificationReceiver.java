package ca.cmpt276.titanium.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.cmpt276.titanium.model.TimerData;
import ca.cmpt276.titanium.model.TimerNotifications;

/**
 * This activity is called when user interacts with timer notifications.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isNotificationDismissed = intent.getBooleanExtra("isNotificationDismissed", false);
        boolean isNotificationPaused = intent.getBooleanExtra("isNotificationPaused", false);
        boolean isNotificationResumed = intent.getBooleanExtra("isNotificationResumed", false);
        boolean isNotificationCancelled = intent.getBooleanExtra("isNotificationCancelled", false);

        TimerNotifications timerNotifications = TimerNotifications.getInstance(context);
        TimerData timerData = TimerData.getInstance(context);

        if (isNotificationDismissed) {
            timerNotifications.dismissNotification(false);
        } else if (isNotificationPaused) {
            context.getApplicationContext().startService(new Intent(context.getApplicationContext(), TimerService.class));
            timerNotifications.changeInteractiveNotification(true);
        } else if (isNotificationResumed) {
            timerData.setPaused();
            timerNotifications.changeInteractiveNotification(false);
        } else if (isNotificationCancelled) {
            timerData.setStopped();
            timerNotifications.dismissNotification(true);
        }
    }
}
