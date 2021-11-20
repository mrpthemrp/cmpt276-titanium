package ca.cmpt276.titanium.ui;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import ca.cmpt276.titanium.model.Timer;

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

        this.countDownTimer = new CountDownTimer(timer.getRemainingMilliseconds(), TIMER_COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long remainingMilliseconds) {
                if (!timer.isRunning()) {
                    stopSelf();
                }

                timer.setRemainingMilliseconds(remainingMilliseconds);

                if (timer.isGUIEnabled()) {
                    sendBroadcast(timerUpdateIntent);
                } else {
                    timerNotification.updateNotificationTime();
                }
            }

            @Override
            public void onFinish() {
                timer.setStopped();

                if (timer.isGUIEnabled()) {
                    sendBroadcast(timerUpdateIntent);
                }

                timerNotification.launchNotification("Finish");
                stopSelf();
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();

        // for if onTick updated remainingMilliseconds after setStopped() initially called
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
