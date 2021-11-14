package ca.cmpt276.titanium.ui;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import ca.cmpt276.titanium.model.TimerData;

public class TimerService extends Service {
    public static final String TIMER_UPDATE_INTENT = "timerUpdateIntent";
    private static final int TIMER_COUNTDOWN_INTERVAL = 50;

    private TimerNotifications timerNotifications;
    private TimerData timerData;
    private Intent timerUpdateIntent;
    private CountDownTimer countDownTimer;

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

                if (timerData.isGUIEnabled()) {
                    sendBroadcast(timerUpdateIntent);
                } else {
                    timerNotifications.updateNotificationTime();
                }
            }

            @Override
            public void onFinish() {
                timerData.setStopped();

                if (timerData.isGUIEnabled()) {
                    sendBroadcast(timerUpdateIntent);
                }

                timerNotifications.launchNotification("Finish");
                stopSelf();
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();

        // for if onTick updated remainingMilliseconds after setStopped() initially called
        if (!timerData.isPaused()) {
            timerData.setStopped();
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
