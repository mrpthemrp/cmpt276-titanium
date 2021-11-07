package ca.cmpt276.titanium.ui;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import ca.cmpt276.titanium.R;

import java.util.Locale;
import java.util.Objects;

public class TimerActivity extends AppCompatActivity {
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_HOUR = 3600000;

    private ImageView playPause;
    private EditText userInputTime;
    private boolean isTimeRunning, isPaused;
    long durationOfTime;
    long durationStartTime;
    private CountDownTimer countDownTimer;
    private long endTime;

    public static MediaPlayer timerEndSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                timerEndSound = MediaPlayer.create(TimerActivity.this, R.raw.timeralarm);
            } else if (extras.getBoolean("isClicked")) {
                timerEndSound.stop();
                startStopVibrations(TimerActivity.this, "off");
            }
        }

        timerEndSound = MediaPlayer.create(TimerActivity.this, R.raw.timeralarm);

        setupActionBar();
        setupAttributes();
        setupCancelBtn();
        clickResetButton();
        customTimeFunctionality();
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.timerTitle);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupAttributes() {
        this.playPause = findViewById(R.id.timerPlayPauseBtn);
        this.userInputTime = findViewById(R.id.editTextNumber);
    }

    private void customTimeFunctionality() {
        Button oneMinButton = findViewById(R.id.oneMin);
        Button twoMinButton = findViewById(R.id.twoMin);
        Button threeMinButton = findViewById(R.id.threeMin);
        Button fiveMinButton = findViewById(R.id.fiveMin);
        Button tenMinButton = findViewById(R.id.tenMin);

        oneMinButton.setOnClickListener(view -> changeTime(MILLIS_IN_MINUTE, MILLIS_IN_MINUTE));
        twoMinButton.setOnClickListener(view -> changeTime(2 * MILLIS_IN_MINUTE, 2 * MILLIS_IN_MINUTE));
        threeMinButton.setOnClickListener(view -> changeTime(3 * MILLIS_IN_MINUTE, 3 * MILLIS_IN_MINUTE));
        fiveMinButton.setOnClickListener(view -> changeTime(5 * MILLIS_IN_MINUTE, 5 * MILLIS_IN_MINUTE));
        tenMinButton.setOnClickListener(view -> changeTime(10 * MILLIS_IN_MINUTE, 10 * MILLIS_IN_MINUTE));

        Button setTimeButton = findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(view -> {
            if (userInputTime.getText().toString().isEmpty()) {
                durationOfTime = 0;
                durationStartTime = 0;
                return;
            }
            durationOfTime = Integer.parseInt(userInputTime.getText().toString());
            durationOfTime *= 60000;
            durationStartTime = Integer.parseInt(userInputTime.getText().toString());
            durationStartTime *= 60000;
            changeTime(durationOfTime, durationStartTime);
        });
    }

    private void changeTime(long dur, long durStart) {
        durationOfTime = dur;
        durationStartTime = durStart;
        displayTime();
        isPaused = true;
        setPlayPause();
    }

    private void startCountDown() {
        endTime = System.currentTimeMillis() + durationOfTime;

        countDownTimer = new CountDownTimer(durationOfTime, 1000) {

            @Override
            public void onTick(long l) {
                durationOfTime = l;
                displayTime();
            }

            @Override
            public void onFinish() {
                setPlayPause();
                timerEndSound.setLooping(true);
                timerEndSound.start();
                startStopVibrations(TimerActivity.this, "start");
                notificationOnEndTime();
            }
        }.start();

        isTimeRunning = true;
    }

    private void stopTimer() {
        if (isTimeRunning) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            isTimeRunning = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("time", durationOfTime);
        editor.putBoolean("timerRunning", isTimeRunning);
        editor.putLong("endOfTimer", endTime);
        editor.putLong("startTime", durationStartTime);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        durationStartTime = prefs.getLong("startTime", 0);
        durationOfTime = prefs.getLong("time", durationStartTime);
        isTimeRunning = prefs.getBoolean("timerRunning", false);

        displayTime();
        setupPlayPause();

        if (isTimeRunning) {
            endTime = prefs.getLong("endOfTimer", 0);
            durationOfTime = endTime - System.currentTimeMillis();

            if (durationOfTime < 0) {
                durationOfTime = 0;
                isTimeRunning = true;
                isPaused = true;
                displayTime();
            } else {
                isTimeRunning = false;
                isPaused = false;
                setPlayPause();
            }
        }
    }

    private void displayTime() {
        int milliseconds = (int) durationOfTime;
        int hours = milliseconds / MILLIS_IN_HOUR;
        int minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
        int seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        TextView time = findViewById(R.id.timer);
        time.setText(formattedTime);
    }

    private void setupCancelBtn() {
        Button cancelBtn = findViewById(R.id.timerCancelBtn);
        cancelBtn.setOnClickListener(view -> {
            isPaused = true;
            setPlayPause();
            durationOfTime = 0;
            displayTime();
            Toast.makeText(TimerActivity.this, R.string.timer_cancelled_toast, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupPlayPause() {
        this.playPause.setOnClickListener(view -> setPlayPause());
    }

    private void clickResetButton() {
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(view -> {
            durationOfTime = durationStartTime;
            displayTime();
            isPaused = true;
            setPlayPause();
        });
    }

    private void setPlayPause() {
        if (this.isTimeRunning || isPaused) {
            this.playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
            stopTimer();
            isPaused = false;
        } else {
            this.playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_pause_24, getTheme()));
            startCountDown();
            isPaused = true;
        }
    }

    public static void stopTime() {
        timerEndSound.stop();
    }

    public static void startStopVibrations(Context c, String isStartStop) {
        Vibrator vibrations = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);

        if (isStartStop.equals("start")) {
            vibrations.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrations.cancel();
        }
    }

    /*
    Building a notification found from https://stackoverflow.com/questions/47409256/what-is-notification-channel-idnotifications-not-work-in-api-27
    Using a pending intent found from https://www.youtube.com/watch?v=CZ575BuLBo4
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private void notificationOnEndTime() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("CHANNEL_ID",
                "CHANNEL_NAME",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("NOTIFICATION_CHANNEL_DESCRIPTION");
        channel.enableVibration(true);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
        intent.putExtra("isClicked", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("sound", "off");
        PendingIntent soundIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle("Time Is Up")
                .setContentText("Click OK to stop the sound and vibration")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.GREEN)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .addAction(R.drawable.ic_sound, "OK", soundIntent)
                .setAutoCancel(true);

        builder.setContentIntent(pendingIntent);
        manager.notify(0, builder.build());
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }
}
