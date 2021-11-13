package ca.cmpt276.titanium.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import java.util.Locale;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.ui.NotificationReceiver;
import ca.cmpt276.titanium.ui.TimerActivity;

public class TimerNotifications {
    private static final long[] TIMER_VIBRATION_PATTERN = {0, 500, 1000};
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_HOUR = 3600000;

    // TODO: What are good ID values? Will 0 and 1 cause conflicts?
    private static final int TIMER_INTERACTIVE_NOTIFICATION_ID = 0;
    private static final int TIMER_FINISH_NOTIFICATION_ID = 1;

    private static TimerNotifications instance;
    private final Context context;
    private final Vibrator timerFinishVibrator;
    private final NotificationManager notificationManager;
    private final PendingIntent notificationClickPendingIntent;
    private final PendingIntent dismissPendingIntent;
    private final Intent toggleTimerIntent;
    private final PendingIntent cancelPendingIntent;
    private MediaPlayer timerFinishSound;
    private PendingIntent toggleTimerPendingIntent;

    private final TimerData timerData;

    private boolean tempHackyBool = false;

    private TimerNotifications(Context context) {
        NotificationChannel channel = new NotificationChannel(
                "practical_parent_timer",
                context.getString(R.string.timer_channel_name),
                NotificationManager.IMPORTANCE_HIGH);

        this.context = context.getApplicationContext();
        this.timerFinishVibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.timerFinishSound = MediaPlayer.create(context.getApplicationContext(), R.raw.timeralarm);

        notificationManager.createNotificationChannel(channel);

        Intent notificationClickIntent = new Intent(context.getApplicationContext(), TimerActivity.class);
        notificationClickIntent.putExtra("isNotificationClicked", true);
        notificationClickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.notificationClickPendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationClickIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent dismissIntent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        dismissIntent.putExtra("isNotificationDismissed", true);
        this.dismissPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE);

        this.toggleTimerIntent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        toggleTimerIntent.putExtra("isNotificationResumed", true);
        toggleTimerIntent.putExtra("isNotificationPaused", false);
        this.toggleTimerPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, toggleTimerIntent, PendingIntent.FLAG_MUTABLE);

        Intent cancelIntent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        cancelIntent.putExtra("isNotificationCancelled", true);
        this.cancelPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 2, cancelIntent, PendingIntent.FLAG_IMMUTABLE);

        this.timerData = TimerData.getInstance(context);
    }

    public static TimerNotifications getInstance(Context context) {
        if (instance == null) {
            TimerNotifications.instance = new TimerNotifications(context);
        }

        return instance;
    }

    public void launchNotification(boolean isInteractive) {
        if (isInteractive) {
            long milliseconds = timerData.getRemainingMilliseconds();
            long hours = milliseconds / MILLIS_IN_HOUR;
            long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
            long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

            NotificationCompat.Builder interactiveBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "practical_parent_timer")
                    .setSmallIcon(R.drawable.ic_time)
                    .setContentTitle(context.getString(R.string.timer_interactive_notification_title))
                    .setContentText(formattedTime)
                    .setContentIntent(notificationClickPendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.GREEN)
                    .addAction(R.drawable.ic_sound, "Pause", toggleTimerPendingIntent)
                    .addAction(R.drawable.ic_sound, "Cancel", cancelPendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true);

            notificationManager.notify(TIMER_INTERACTIVE_NOTIFICATION_ID, interactiveBuilder.build());

            tempHackyBool = true;
        } else {
            notificationManager.cancel(TIMER_INTERACTIVE_NOTIFICATION_ID);

            NotificationCompat.Builder finishNotificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "practical_parent_timer")
                    .setSmallIcon(R.drawable.ic_time)
                    .setContentTitle(context.getString(R.string.timer_finish_notification_title))
                    .setContentIntent(notificationClickPendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.GREEN)
                    .addAction(R.drawable.ic_sound, context.getString(R.string.timer_notification_dismiss_button), dismissPendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(true);

            notificationManager.notify(TIMER_FINISH_NOTIFICATION_ID, finishNotificationBuilder.build());

            timerFinishSound = MediaPlayer.create(context.getApplicationContext(), R.raw.timeralarm);
            timerFinishSound.setLooping(true);
            timerFinishSound.start();

            timerFinishVibrator.vibrate(VibrationEffect.createWaveform(TIMER_VIBRATION_PATTERN, 0),
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());

            tempHackyBool = false;
        }
    }

    public void dismissNotification(boolean isInteractive) {
        if (isInteractive) {
            notificationManager.cancel(TIMER_INTERACTIVE_NOTIFICATION_ID);
        } else {
            notificationManager.cancel(TIMER_FINISH_NOTIFICATION_ID);

            timerFinishSound.stop();

            timerFinishVibrator.cancel();
        }
    }

    public void updateNotificationTime() {
        if (tempHackyBool) {
            long milliseconds = timerData.getRemainingMilliseconds();
            long hours = milliseconds / MILLIS_IN_HOUR;
            long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
            long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

            NotificationCompat.Builder interactiveBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "practical_parent_timer")
                    .setSmallIcon(R.drawable.ic_time)
                    .setContentTitle(context.getString(R.string.timer_interactive_notification_title))
                    .setContentText(formattedTime)
                    .setContentIntent(notificationClickPendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.GREEN)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true);

            if (toggleTimerIntent.getBooleanExtra("isNotificationResumed", false)) {
                interactiveBuilder.addAction(R.drawable.ic_sound, "Pause", toggleTimerPendingIntent);
            } else {
                interactiveBuilder.addAction(R.drawable.ic_sound, "Resume", toggleTimerPendingIntent);
            }

            interactiveBuilder.addAction(R.drawable.ic_sound, "Cancel", cancelPendingIntent);
            notificationManager.notify(TIMER_INTERACTIVE_NOTIFICATION_ID, interactiveBuilder.build());
        }
    }

    public void changeInteractiveNotification(boolean hasPauseButton) {
        long milliseconds = timerData.getRemainingMilliseconds();
        long hours = milliseconds / MILLIS_IN_HOUR;
        long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
        long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        NotificationCompat.Builder interactiveBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "practical_parent_timer")
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle(context.getString(R.string.timer_interactive_notification_title))
                .setContentText(formattedTime)
                .setContentIntent(notificationClickPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.GREEN)
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true);

        toggleTimerIntent.putExtra("isNotificationResumed", hasPauseButton);
        toggleTimerIntent.putExtra("isNotificationPaused", !hasPauseButton);

        this.toggleTimerPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, toggleTimerIntent, PendingIntent.FLAG_MUTABLE);

        if (hasPauseButton) {
            interactiveBuilder.addAction(R.drawable.ic_sound, "Pause", toggleTimerPendingIntent);
        } else {
            interactiveBuilder.addAction(R.drawable.ic_sound, "Resume", toggleTimerPendingIntent);
        }

        interactiveBuilder.addAction(R.drawable.ic_sound, "Cancel", cancelPendingIntent);
        notificationManager.notify(TIMER_INTERACTIVE_NOTIFICATION_ID, interactiveBuilder.build());
    }
}
