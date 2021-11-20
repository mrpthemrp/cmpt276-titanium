package ca.cmpt276.titanium.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Timer;

public class TimerNotification {
    private static final int MILLIS_IN_HOUR = 3600000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_SECOND = 1000;
    private static final String TIMER_NOTIFICATION_TAG = "practicalParent";
    private static final int TIMER_FINISH_NOTIFICATION_ID = 0;
    private static final int TIMER_INTERACTIVE_NOTIFICATION_ID = 1;
    private static final long[] TIMER_VIBRATION_PATTERN = {0, 500, 1000};

    private static TimerNotification instance;
    private final Context context;
    private final Timer timer;
    private final NotificationManagerCompat notificationManager;
    private final Vibrator timerFinishVibrator;

    private final PendingIntent pauseTimerPendingIntent;
    private final PendingIntent resumeTimerPendingIntent;
    private final PendingIntent cancelPendingIntent;
    private final NotificationCompat.Builder finishNotificationBuilder;
    private final NotificationCompat.Builder interactiveNotificationBuilder;

    private MediaPlayer timerFinishSound;
    private String lastFormattedTime;

    private TimerNotification(Context context) {
        NotificationChannel channel = new NotificationChannel(
                "practicalParentTimer",
                context.getString(R.string.timer_channel_name),
                NotificationManager.IMPORTANCE_HIGH);

        this.context = context.getApplicationContext();
        this.timer = Timer.getInstance(context);
        this.notificationManager = NotificationManagerCompat.from(context);

        notificationManager.createNotificationChannel(channel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) this.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            this.timerFinishVibrator = vibratorManager.getDefaultVibrator();
        } else {
            this.timerFinishVibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        AtomicInteger atomicInteger = new AtomicInteger();

        Intent notificationClickIntent = new Intent(this.context, TimerActivity.class);
        notificationClickIntent.putExtra("isNotificationClicked", true);
        notificationClickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notificationClickPendingIntent = PendingIntent.getActivity(this.context, atomicInteger.getAndIncrement(), notificationClickIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent dismissIntent = new Intent(this.context, TimerBroadcastReceiver.class);
        dismissIntent.putExtra("notificationAction", "Dismiss");
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this.context, atomicInteger.getAndIncrement(), dismissIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseTimerIntent = new Intent(this.context, TimerBroadcastReceiver.class);
        pauseTimerIntent.putExtra("notificationAction", "Pause");
        this.pauseTimerPendingIntent = PendingIntent.getBroadcast(this.context, atomicInteger.getAndIncrement(), pauseTimerIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent resumeTimerIntent = new Intent(this.context, TimerBroadcastReceiver.class);
        resumeTimerIntent.putExtra("notificationAction", "Resume");
        this.resumeTimerPendingIntent = PendingIntent.getBroadcast(this.context, atomicInteger.getAndIncrement(), resumeTimerIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent cancelIntent = new Intent(this.context, TimerBroadcastReceiver.class);
        cancelIntent.putExtra("notificationAction", "Cancel");
        this.cancelPendingIntent = PendingIntent.getBroadcast(this.context, atomicInteger.getAndIncrement(), cancelIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder basicNotificationBuilder = new NotificationCompat.Builder(this.context, "practicalParentTimer")
                .setSmallIcon(R.drawable.ic_outline_timer_white_24)
                .setColor(Color.GREEN)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(notificationClickPendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        this.finishNotificationBuilder = new NotificationCompat.Builder(context, basicNotificationBuilder.build())
                .setContentTitle(context.getString(R.string.timer_finish_notification_title))
                .addAction(R.drawable.ic_baseline_cancel_white_24, context.getString(R.string.timer_notification_dismiss_button), dismissPendingIntent);

        this.interactiveNotificationBuilder = new NotificationCompat.Builder(context, basicNotificationBuilder.build())
                .setContentTitle(context.getString(R.string.timer_interactive_notification_title))
                .setOnlyAlertOnce(true);
    }

    public static TimerNotification getInstance(Context context) {
        if (instance == null) {
            TimerNotification.instance = new TimerNotification(context);
        }

        return instance;
    }

    public void launchNotification(String notificationType) {
        if (notificationType.equals("Finish")) {
            dismissNotification(true);

            notificationManager.notify(TIMER_NOTIFICATION_TAG, TIMER_FINISH_NOTIFICATION_ID, finishNotificationBuilder.build());

            this.timerFinishSound = MediaPlayer.create(context, R.raw.sound_timer_alarm);
            timerFinishSound.start();

            timerFinishVibrator.vibrate(VibrationEffect.createWaveform(TIMER_VIBRATION_PATTERN, 0),
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
        } else if (!(notificationType.equals("Pause") || notificationType.equals("Resume"))) {
            throw new IllegalArgumentException("notificationType must be one of \"Finish\", \"Pause\", or \"Resume\"");
        } else {
            String formattedTime = getFormattedTime();
            this.lastFormattedTime = formattedTime;

            interactiveNotificationBuilder.setContentText(formattedTime);
            interactiveNotificationBuilder.clearActions();

            if (notificationType.equals("Pause")) {
                interactiveNotificationBuilder.addAction(R.drawable.ic_baseline_pause_white_24, context.getString(R.string.timer_notification_pause_button), pauseTimerPendingIntent);
            } else {
                interactiveNotificationBuilder.addAction(R.drawable.ic_baseline_play_arrow_white_24, context.getString(R.string.timer_notification_resume_button), resumeTimerPendingIntent);
            }

            interactiveNotificationBuilder.addAction(R.drawable.ic_baseline_cancel_white_24, context.getString(R.string.timer_notification_cancel_button), cancelPendingIntent);
            notificationManager.notify(TIMER_NOTIFICATION_TAG, TIMER_INTERACTIVE_NOTIFICATION_ID, interactiveNotificationBuilder.build());
        }
    }

    public void dismissNotification(boolean dismissInteractive) {
        if (dismissInteractive) {
            notificationManager.cancel(TIMER_NOTIFICATION_TAG, TIMER_INTERACTIVE_NOTIFICATION_ID);
        } else {
            notificationManager.cancel(TIMER_NOTIFICATION_TAG, TIMER_FINISH_NOTIFICATION_ID);

            if (timerFinishSound != null) {
                timerFinishSound.stop();
            }

            timerFinishVibrator.cancel();
        }
    }

    public void updateNotificationTime() {
        String formattedTime = getFormattedTime();

        if (!formattedTime.equals(lastFormattedTime)) {
            this.lastFormattedTime = formattedTime;
            interactiveNotificationBuilder.setContentText(formattedTime);
            notificationManager.notify(TIMER_NOTIFICATION_TAG, TIMER_INTERACTIVE_NOTIFICATION_ID, interactiveNotificationBuilder.build());
        }
    }

    private String getFormattedTime() {
        long milliseconds = timer.getRemainingMilliseconds();
        long hours = milliseconds / MILLIS_IN_HOUR;
        long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
        long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
