package ca.cmpt276.titanium.ui.timer;

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

/**
 * Displays timer information while away from TimerActivity.
 * Notifies a user when a timer has finished.
 *
 * @author Titanium
 */
public class TimerNotification {
  private static final String TIMER_NOTIFICATION_TAG = "practicalParent";
  private static final long[] TIMER_VIBRATION_PATTERN = {0L, 500L, 1000L};
  private static final int TIMER_INTERACTIVE_NOTIFICATION_ID = 1;
  private static final int TIMER_FINISH_NOTIFICATION_ID = 0;
  private static final int MILLIS_IN_SECOND = 1000;
  private static final int MILLIS_IN_MINUTE = 60000;
  private static final int MILLIS_IN_HOUR = 3600000;

  private static TimerNotification instance;
  private final Context context;
  private final Timer timer;
  private final NotificationManagerCompat notificationManager;
  private final Vibrator timerFinishVibrator;

  private PendingIntent pauseTimerPendingIntent;
  private PendingIntent resumeTimerPendingIntent;
  private PendingIntent cancelTimerPendingIntent;
  private NotificationCompat.Builder finishNotificationBuilder;
  private NotificationCompat.Builder interactiveNotificationBuilder;

  private MediaPlayer timerFinishSound;
  private String lastFormattedTime;

  public static TimerNotification getInstance(Context context) {
    if (instance == null) {
      TimerNotification.instance = new TimerNotification(context);
    }

    return instance;
  }

  private TimerNotification(Context context) {
    this.context = context.getApplicationContext();
    this.timer = Timer.getInstance(this.context);
    this.notificationManager = NotificationManagerCompat.from(this.context);

    notificationManager.createNotificationChannel(new NotificationChannel("practicalParentTimer",
        this.context.getString(R.string.timer_notification_channel_name),
        NotificationManager.IMPORTANCE_HIGH));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      VibratorManager vibratorManager =
          (VibratorManager) this.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
      this.timerFinishVibrator = vibratorManager.getDefaultVibrator();
    } else {
      this.timerFinishVibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    createNotificationBuilders();
  }

  private void createNotificationBuilders() {
    AtomicInteger atomicInteger = new AtomicInteger();

    Intent clickIntent = new Intent(context, TimerActivity.class);
    clickIntent.putExtra("isClicked", true);
    clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent clickPendingIntent = PendingIntent.getActivity(
        context, atomicInteger.getAndIncrement(), clickIntent, PendingIntent.FLAG_IMMUTABLE);

    Intent dismissTimerIntent = new Intent(context, TimerBroadcastReceiver.class);
    dismissTimerIntent.putExtra("notificationAction", "Dismiss");
    PendingIntent dismissTimerPendingIntent = PendingIntent.getBroadcast(
        context, atomicInteger.getAndIncrement(), dismissTimerIntent, PendingIntent.FLAG_IMMUTABLE);

    Intent pauseTimerIntent = new Intent(context, TimerBroadcastReceiver.class);
    pauseTimerIntent.putExtra("notificationAction", "Pause");
    this.pauseTimerPendingIntent = PendingIntent.getBroadcast(
        context, atomicInteger.getAndIncrement(), pauseTimerIntent, PendingIntent.FLAG_IMMUTABLE);

    Intent resumeTimerIntent = new Intent(context, TimerBroadcastReceiver.class);
    resumeTimerIntent.putExtra("notificationAction", "Resume");
    this.resumeTimerPendingIntent = PendingIntent.getBroadcast(
        context, atomicInteger.getAndIncrement(), resumeTimerIntent, PendingIntent.FLAG_IMMUTABLE);

    Intent cancelTimerIntent = new Intent(context, TimerBroadcastReceiver.class);
    cancelTimerIntent.putExtra("notificationAction", "Cancel");
    this.cancelTimerPendingIntent = PendingIntent.getBroadcast(
        context, atomicInteger.getAndIncrement(), cancelTimerIntent, PendingIntent.FLAG_IMMUTABLE);

    NotificationCompat.Builder basicNotificationBuilder =
        new NotificationCompat.Builder(context, "practicalParentTimer")
            .setSmallIcon(R.drawable.ic_baseline_timer_white_24)
            .setColor(Color.GREEN)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(clickPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

    this.finishNotificationBuilder =
        new NotificationCompat.Builder(context, basicNotificationBuilder.build())
            .setContentTitle(context.getString(R.string.title_timer_notification_finish))
            .addAction(R.drawable.ic_baseline_cancel_white_24,
                context.getString(R.string.button_timer_notification_dismiss),
                dismissTimerPendingIntent);

    this.interactiveNotificationBuilder =
        new NotificationCompat.Builder(context, basicNotificationBuilder.build())
            .setContentTitle(context.getString(R.string.title_timer_notification_interactive))
            .setOnlyAlertOnce(true);
  }

  public void launchNotification(String notificationType) {
    if (notificationType.equals("Finish")) {
      dismissNotification(true);

      notificationManager.notify(
          TIMER_NOTIFICATION_TAG, TIMER_FINISH_NOTIFICATION_ID, finishNotificationBuilder.build());

      this.timerFinishSound = MediaPlayer.create(context, R.raw.sound_timer_alarm);
      timerFinishSound.start();

      timerFinishVibrator.vibrate(
          VibrationEffect.createWaveform(TIMER_VIBRATION_PATTERN, 0),
          new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
    } else if (!(notificationType.equals("Pause") || notificationType.equals("Resume"))) {
      throw new IllegalArgumentException(
          "notificationType must be one of \"Finish\", \"Pause\", or \"Resume\"");
    } else {
      this.lastFormattedTime = getFormattedTime();

      interactiveNotificationBuilder.setContentText(lastFormattedTime);
      interactiveNotificationBuilder.clearActions();

      if (notificationType.equals("Pause")) {
        interactiveNotificationBuilder.addAction(R.drawable.ic_baseline_pause_white_24,
            context.getString(R.string.button_timer_notification_pause),
            pauseTimerPendingIntent);
      } else {
        interactiveNotificationBuilder.addAction(R.drawable.ic_baseline_play_arrow_white_24,
            context.getString(R.string.button_timer_notification_resume),
            resumeTimerPendingIntent);
      }

      interactiveNotificationBuilder.addAction(R.drawable.ic_baseline_cancel_white_24,
          context.getString(R.string.button_timer_notification_cancel),
          cancelTimerPendingIntent);

      notificationManager.notify(TIMER_NOTIFICATION_TAG,
          TIMER_INTERACTIVE_NOTIFICATION_ID,
          interactiveNotificationBuilder.build());
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
      notificationManager.notify(TIMER_NOTIFICATION_TAG,
          TIMER_INTERACTIVE_NOTIFICATION_ID,
          interactiveNotificationBuilder.build());
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
