package ca.cmpt276.titanium.ui;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import ca.cmpt276.titanium.model.TimerData;
import ca.cmpt276.titanium.model.TimerNotifications;

public class TimerService extends Service {
    public static final String TIMER_UPDATE_INTENT = "timerUpdateIntent";
    private static final int TIMER_COUNTDOWN_INTERVAL = 50;
    private static final int UPDATE_DELAY_THRESHOLD = 10;

    private TimerNotifications timerNotifications;
    private TimerData timerData;
    private Intent timerUpdateIntent;
    private CountDownTimer countDownTimer;
    private int updateNotificationDelayCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        this.timerNotifications = TimerNotifications.getInstance(this);
        timerNotifications.dismissNotification(false);

        this.timerData = TimerData.getInstance(this);
        timerData.setRunning();

        this.timerUpdateIntent = new Intent(TIMER_UPDATE_INTENT);

        this.countDownTimer = new CountDownTimer(timerData.getRemainingMilliseconds(), TIMER_COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long remainingMilliseconds) {
                if (!timerData.isRunning()) {
                    stopSelf();
                }

                timerData.setRemainingMilliseconds(remainingMilliseconds);
                sendBroadcast(timerUpdateIntent);
                updateNotificationDelayCount++;

                if (updateNotificationDelayCount >= UPDATE_DELAY_THRESHOLD) {
                    timerNotifications.updateNotificationTime();
                    updateNotificationDelayCount = 0;
                }
            }

            @Override
            public void onFinish() {
                timerData.setStopped();
                sendBroadcast(timerUpdateIntent);
                timerNotifications.updateNotificationTime();
                timerNotifications.launchNotification(false);
                stopSelf();
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        if (!timerData.isPaused()) {
            timerData.setStopped();
        }

        countDownTimer.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
