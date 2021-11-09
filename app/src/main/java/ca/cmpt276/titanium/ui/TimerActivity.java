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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.TimerInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TimerActivity extends AppCompatActivity {
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_HOUR = 3600000;
    private static final int TIMER_COUNTDOWN_INTERVAL = 50;
    private static final int TIMER_NOTIFICATION_ID = 52;
    private static final long[] TIMER_VIBRATION_PATTERN = {0, 500, 1000};

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

        timerInfo = TimerInfo.getInstance(this);

        String test = getIntent().getStringExtra("test");

        if (test != null && test.equals("test")) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            timerInfo.setStopped();

            //timerEndSound = MediaPlayer.create(getApplicationContext(), R.raw.timeralarm);
            timerEndSound.setLooping(true);
            timerEndSound.start();

            toggleVibrations(getApplicationContext(), true);
            endTimerNotification();
            finish();
        }

        /*
        Receiving notification click found from https://stackoverflow.com/questions/13822509/android-call-method-on-notification-click/14539858
         */
        if (savedInstanceState == null) { // TODO
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                timerEndSound = MediaPlayer.create(getApplicationContext(), R.raw.timeralarm);
            } else if (extras.getBoolean("isClicked")) {
                if (timerEndSound != null) {
                    timerEndSound.setLooping(false);
                    timerEndSound.stop();
                }

                toggleVibrations(getApplicationContext(), false);

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(TIMER_NOTIFICATION_ID);

                finish();
            }
        }

        boolean dismissed = getIntent().getBooleanExtra("dismissed", false);

        if (!dismissed) {
            setContentView(R.layout.activity_timer);
            setTitle(R.string.timerTitle);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

            setupButtons();

            if (timerInfo.isRunning()) {
                startTimer();
            } else {
                displayTime();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (timerInfo.isRunning()) {
            startBackgroundTimer();
        }
    }

    private void startTimer() {
        startCountDown(timerInfo.getRemainingMilliseconds(), false);
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

        //timerEndSound = MediaPlayer.create(getApplicationContext(), R.raw.timeralarm);
        playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
        makeInputButtonsVisible(true);
    }

    private void changeTimerDuration(long minutes) {
        timerInfo.setDurationMilliseconds(5000);//minutes * MILLIS_IN_MINUTE); // TODO
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

    public static void dismissNotification(Context context) {
        timerEndSound.stop();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(TIMER_NOTIFICATION_ID);
    }

    private void startCountDown(long durationMilliseconds, boolean inBackground) {
        countDownTimer = new CountDownTimer(durationMilliseconds, TIMER_COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                timerInfo.setRemainingMilliseconds(l);

                if (!inBackground) {
                   displayTime();
                }
            }

            @Override
            public void onFinish() { // TODO
                resetTimer();

                timerEndSound = MediaPlayer.create(getApplicationContext(), R.raw.timeralarm);
                if (timerEndSound != null) {
                    timerEndSound.setLooping(true);
                    timerEndSound.start();
                }

                toggleVibrations(getApplicationContext(), true);
                endTimerNotification();
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

    private void startBackgroundTimer() {
        long endTimerMilliseconds = System.currentTimeMillis() + timerInfo.getRemainingMilliseconds();
        ongoingTimerNotification("Pause", endTimerMilliseconds);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.obj == "start") {
                    startCountDown(timerInfo.getRemainingMilliseconds(), true);
                    timerInfo.setRunning();
                    System.out.println("start");
                } else if (msg.obj == "pause") {
                    long endTimerMilliseconds = System.currentTimeMillis() + timerInfo.getRemainingMilliseconds();
                    ongoingTimerNotification("Resume", endTimerMilliseconds);
                    countDownTimer.cancel();
                    timerInfo.setPaused();
                    System.out.println("pause");
                } else if (msg.obj == "resume") {
                    long endTimerMilliseconds = System.currentTimeMillis() + timerInfo.getRemainingMilliseconds();
                    ongoingTimerNotification("Pause", endTimerMilliseconds);
                    startCountDown(timerInfo.getRemainingMilliseconds(), true);
                    timerInfo.setResumed();
                    System.out.println("resume");
                }
            }
        };

        Thread thread = new Thread(() -> {
            Message message = handler.obtainMessage(0, "start");
            message.sendToTarget();

            //boolean running = true;

            while (true) {
                if (timerInfo.isStopped()) {
                    return;
                } else if (timerInfo.isPaused()) {
                    message = handler.obtainMessage(0, "pause");
                    message.sendToTarget();
                } else if (timerInfo.isResumed()) {
                    message = handler.obtainMessage(0, "resume");
                    message.sendToTarget();
                }
            }
        });

        thread.start();
    }

    public static void cancelTimerNotification(Context context) {
        TimerInfo timerInfo = TimerInfo.getInstance(context.getApplicationContext());
        timerInfo.setStopped();
    }

    public static void pauseTimerNotification(Context context) {
        TimerInfo timerInfo = TimerInfo.getInstance(context.getApplicationContext());
        timerInfo.setPaused();
    }

    public static void resumeTimerNotification(Context context) {
        TimerInfo timerInfo = TimerInfo.getInstance(context.getApplicationContext());
        timerInfo.setRunning();
    }

    public static void toggleVibrations(Context context, boolean startVibrations) {
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);

        if (startVibrations) {
            vibrator.vibrate(VibrationEffect.createWaveform(TIMER_VIBRATION_PATTERN, 0),
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
        } else {
            vibrator.cancel();
        }
    }

    private void ongoingTimerNotification(String leftButton, long endTimerMilliseconds) { // leftButton: "resume" or "pause"
        NotificationChannel notificationChannel = new NotificationChannel(
                "practical_parent_timer",
                "Timers",
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        Intent clickIntent = new Intent(getApplicationContext(), TimerActivity.class);
        clickIntent.putExtra("isClicked", true);
        clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent leftButtonIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        leftButtonIntent.putExtra(leftButton, true);
        PendingIntent leftButtonPendingIntent = PendingIntent.getBroadcast(this, 0, leftButtonIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent cancelIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        cancelIntent.putExtra("cancel", true);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE);

        DateFormat formatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "practical_parent_timer")
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle("Timer")
                .setContentText("End time: " + formatter.format(new Date(endTimerMilliseconds)))
                .setContentIntent(clickPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setTimeoutAfter(MILLIS_IN_MINUTE)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.GREEN)
                .addAction(R.drawable.ic_sound, leftButton, leftButtonPendingIntent)
                .addAction(R.drawable.ic_sound, "Cancel", cancelPendingIntent)
                .setOngoing(true);

        notificationManager.notify(TIMER_NOTIFICATION_ID, builder.build());
    }

    private void endTimerNotification() { // TODO make okay stop everything and cancel notification
        NotificationChannel notificationChannel = new NotificationChannel(
                "practical_parent_timer",
                "Timers",
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(TIMER_NOTIFICATION_ID);
        notificationManager.createNotificationChannel(notificationChannel);

        Intent clickIntent = new Intent(getApplicationContext(), TimerActivity.class);
        clickIntent.putExtra("isClicked", true);
        clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent dismissIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        dismissIntent.putExtra("dismissed", true);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "practical_parent_timer")
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle("Timer")
                //.setContentText("Click OK to stop the sound and vibration")
                .setContentIntent(clickPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setTimeoutAfter(MILLIS_IN_MINUTE)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.GREEN)
                .addAction(R.drawable.ic_sound, "Dismiss", dismissPendingIntent)
                .setOngoing(true)
                .setAutoCancel(true);

        notificationManager.notify(TIMER_NOTIFICATION_ID, builder.build());
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }
}
