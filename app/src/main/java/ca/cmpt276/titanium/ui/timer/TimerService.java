package ca.cmpt276.titanium.ui.timer;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import ca.cmpt276.titanium.model.Timer;

/**
 * Handles the operation of a timer in the background.
 *
 * @author Titanium
 */
public class TimerService extends Service {
  public static final String TIMER_UPDATE_INTENT = "timerUpdateIntent";
  private static final int TIMER_COUNTDOWN_INTERVAL = 50;

  private TimerNotification timerNotification;
  private Timer timer;
  private Intent timerUpdateIntent;
  private CountDownTimer countDownTimer;

  @Override
  public void onCreate() {
    super.onCreate();

    this.timerNotification = TimerNotification.getInstance(this);
    timerNotification.dismissNotification(false);

    this.timer = Timer.getInstance(this);
    timer.setRunning();

    this.timerUpdateIntent = new Intent(TIMER_UPDATE_INTENT);

    float timeFactor = timer.getTimeFactor();
    long timerRemainingMilliseconds = (long) (timer.getRemainingMilliseconds() / timeFactor);

    this.countDownTimer = new CountDownTimer(timerRemainingMilliseconds, TIMER_COUNTDOWN_INTERVAL) {
      @Override
      public void onTick(long remainingMilliseconds) {
        if (!timer.isRunning()) {
          stopSelf();
        } else {
          timer.setRemainingMilliseconds(remainingMilliseconds);
        }

        if (timer.isGUIEnabled()) {
          sendBroadcast(timerUpdateIntent);
        } else {
          timerNotification.updateNotificationTime();
        }
      }

      @Override
      public void onFinish() {
        stopSelf();
        timer.setStopped();

        if (timer.isGUIEnabled()) {
          sendBroadcast(timerUpdateIntent);
        }

        timerNotification.launchNotification("Finish");
      }
    }.start();
  }

  @Override
  public void onDestroy() {
    countDownTimer.cancel();

    // in case onTick updated remainingMilliseconds after setStopped() initially called
    if (!timer.isPaused()) {
      timer.setStopped();
    } else {
      float timeFactor = timer.getTimeFactor();
      timer.setRemainingMilliseconds((long) (timer.getRemainingMilliseconds() * timeFactor));
    }

    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
