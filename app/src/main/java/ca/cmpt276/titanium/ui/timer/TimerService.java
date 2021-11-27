package ca.cmpt276.titanium.ui.timer;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import ca.cmpt276.titanium.model.Timer;

/**
 * This class handles the operation of a timer in the background.
 */
public class TimerService extends Service {
  public static final String TIMER_UPDATE_INTENT = "timerUpdateIntent";
  private static final int TIMER_COUNTDOWN_INTERVAL = 50;

  private TimerNotification timerNotification;
  private Timer timer;
  private Intent timerUpdateIntent;
  private float timeFactor;
  private CountDownTimer countDownTimer;

  @Override
  public void onCreate() {
    super.onCreate();

    this.timerNotification = TimerNotification.getInstance(this);
    timerNotification.dismissNotification(false);

    this.timer = Timer.getInstance(this);
    timer.setRunning();

    this.timerUpdateIntent = new Intent(TIMER_UPDATE_INTENT);
    this.timeFactor = timer.getTimeFactor();

    this.countDownTimer = new CountDownTimer(timer.getRemainingMilliseconds(),
        TIMER_COUNTDOWN_INTERVAL) {
      @Override
      public void onTick(long remainingMilliseconds) {
        long timeIncrement = (long) timeFactor * TIMER_COUNTDOWN_INTERVAL;

        if (!timer.isRunning()) {
          stopSelf();
        } else if (timer.getRemainingMilliseconds() <= timeIncrement) {
          onFinish();
          // TODO: Ensure that onFinish always completes before
          //  TimerService is destroyed
        }

        // TODO: Ideally, use remainingMilliseconds instead because onTick is not
        //  necessarily called every TIMER_COUNTDOWN_INTERVAL
        // TODO: However, the line below should always function completely fine, so
        //  this is not a necessary change
        timer.setRemainingMilliseconds(timer.getRemainingMilliseconds() - timeIncrement);

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
    }

    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
