package ca.cmpt276.titanium.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;
import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.TimerInfo;

public class TimerActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID = 52;
    private static final int COUNTDOWN_INTERVAL = 50;
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_HOUR = 3600000;
    private static final long[] VIBRATION_PATTERN = {0, 500, 1000};

    private TimerInfo timerInfo;
    private CountDownTimer countDownTimer;
    public static MediaPlayer timerEndSound;

    private Button oneMinButton;
    private Button twoMinButton;
    private Button threeMinButton;
    private Button fiveMinButton;
    private Button tenMinButton;
    private EditText userInputTime;
    private Button setTimeButton;
    private ImageView playPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setTitle(R.string.timerTitle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /*
        Receiving notification click found from https://stackoverflow.com/questions/13822509/android-call-method-on-notification-click/14539858
         */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                timerEndSound = MediaPlayer.create(TimerActivity.this, R.raw.timeralarm);
            } else if (extras.getBoolean("isClicked")) {
                if (timerEndSound != null) {
                    timerEndSound.setLooping(false);
                    timerEndSound.stop();
                }

                toggleVibrations(false);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }

        timerInfo = TimerInfo.getInstance(this);

        setupButtons();

        if (timerInfo.isRunning()) {
            startTimer();
        } else {
            displayTime();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timerInfo.isRunning()) {
            pauseTimer();
        }
    }

    private void startTimer() {
        startCountDown(timerInfo.getRemainingMilliseconds());
        timerInfo.setRunning();

        playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_pause_24, getTheme()));
        makeInputButtonsVisible(false);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerInfo.setPaused();

        playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
        makeInputButtonsVisible(true);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timerInfo.setStopped();
        displayTime();

        timerEndSound = MediaPlayer.create(TimerActivity.this, R.raw.timeralarm);
        playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
        makeInputButtonsVisible(true);
    }

    private void changeTimerDuration(long minutes) {
        timerInfo.setDurationMilliseconds(5000);//minutes * MILLIS_IN_MINUTE);  // TODO
        resetTimer();
    }

    private void setupButtons() {
        this.oneMinButton = findViewById(R.id.oneMin);
        this.twoMinButton = findViewById(R.id.twoMin);
        this.threeMinButton = findViewById(R.id.threeMin);
        this.fiveMinButton = findViewById(R.id.fiveMin);
        this.tenMinButton = findViewById(R.id.tenMin);
        this.userInputTime = findViewById(R.id.editTextNumber);
        this.setTimeButton = findViewById(R.id.setTimeButton);
        this.playPause = findViewById(R.id.timerPlayPauseBtn);

        oneMinButton.setOnClickListener(view -> changeTimerDuration(1));
        twoMinButton.setOnClickListener(view -> changeTimerDuration(2));
        threeMinButton.setOnClickListener(view -> changeTimerDuration(3));
        fiveMinButton.setOnClickListener(view -> changeTimerDuration(5));
        tenMinButton.setOnClickListener(view -> changeTimerDuration(10));

        setTimeButton.setOnClickListener(view -> {
            if (!userInputTime.getText().toString().isEmpty()) {
                changeTimerDuration(Long.parseLong(userInputTime.getText().toString()));
            }
        });

        playPause.setOnClickListener(view -> {
            if (timerInfo.isRunning()) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(view -> {
            if (!timerInfo.isStopped()) {
                resetTimer();
            }
        });
    }

    private void startCountDown(long durationMilliseconds) {
        countDownTimer = new CountDownTimer(durationMilliseconds, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                timerInfo.setRemainingMilliseconds(l);
                displayTime();
            }

            @Override
            public void onFinish() {
                resetTimer();

                if (timerEndSound != null) {
                    timerEndSound.setLooping(true);
                    timerEndSound.start();
                }

                toggleVibrations(true);
                notificationOnEndTime();
            }
        }.start();
    }

    private void displayTime() {
        long milliseconds = timerInfo.getRemainingMilliseconds();
        long hours = milliseconds / MILLIS_IN_HOUR;
        long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
        long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        TextView time = findViewById(R.id.timer);
        time.setText(formattedTime);
    }

    private void makeInputButtonsVisible(boolean isVisible) {
        int visibility;

        if (isVisible) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.INVISIBLE;
        }

        oneMinButton.setVisibility(visibility);
        twoMinButton.setVisibility(visibility);
        threeMinButton.setVisibility(visibility);
        fiveMinButton.setVisibility(visibility);
        tenMinButton.setVisibility(visibility);
        userInputTime.setVisibility(visibility);
        setTimeButton.setVisibility(visibility);
        findViewById(R.id.minuteText).setVisibility(visibility);
    }

    public void toggleVibrations(boolean startVibrations) {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);

        if (startVibrations) {
            vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, 0),
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
        } else {
            vibrator.cancel();
        }
    }

    /*
    Building a notification found from https://stackoverflow.com/questions/47409256/what-is-notification-channel-idnotifications-not-work-in-api-27
    Using a pending intent found from https://www.youtube.com/watch?v=CZ575BuLBo4
     */
    private void notificationOnEndTime() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("TIMER",
                "TIMER_NAME",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("TIMER_NOTIFICATION");
        channel.enableVibration(true);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
        intent.putExtra("isClicked", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "TIMER")
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle("Time Is Up")
                .setContentText("Click OK to stop the sound and vibration")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.GREEN)
                .addAction(R.drawable.ic_sound, "OK", pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);

        builder.setContentIntent(pendingIntent);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }
}
