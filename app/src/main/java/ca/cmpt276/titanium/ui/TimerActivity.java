package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.titanium.R;

public class TimerActivity extends AppCompatActivity {
    private ImageView playPause;
    private Button cancelBtn, resetButton;
    private Button oneMinButton, twoMinButton, threeMinButton, fiveMinButton, tenMinButton;
    private Button setTimeButton;
    private EditText userInputTime;
    private boolean isTimeRunning, isPaused;
    long durationOfTime;
    long durationStartTime;
    private TextView time;
    private CountDownTimer countDownTimer;
    private long endTime;

    public static MediaPlayer timerEndSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerEndSound = MediaPlayer.create(TimerActivity.this, R.raw.timeralarm);

        setupTitle();
        setupAttributes();
        setupCancelBtn();
        clickResetButton();
        customTimeFunctionality();
    }

    private void setupTitle() {
        ActionBar toolbar = getSupportActionBar();
        assert toolbar != null;
        toolbar.setTitle(R.string.timerTitle);
    }

    private void setupAttributes() {
        this.playPause = findViewById(R.id.timerPlayPauseBtn);
        this.cancelBtn = findViewById(R.id.timerCancelBtn);
        this.resetButton = findViewById(R.id.resetButton);
        this.oneMinButton = findViewById(R.id.oneMin);
        this.twoMinButton = findViewById(R.id.twoMin);
        this.threeMinButton = findViewById(R.id.threeMin);
        this.fiveMinButton = findViewById(R.id.fiveMin);
        this.tenMinButton = findViewById(R.id.tenMin);
        this.userInputTime = findViewById(R.id.editTextNumber);
        this.setTimeButton = findViewById(R.id.setTimeButton);
        this.time = findViewById(R.id.timer);
    }

    private void customTimeFunctionality(){
        oneMinButton.setOnClickListener(view -> {
            durationOfTime = 60000;
            durationStartTime = 60000;
            changeTime();
        });
        twoMinButton.setOnClickListener(view -> {
            durationOfTime = 120000;
            durationStartTime = 120000;
            changeTime();
        });
        threeMinButton.setOnClickListener(view -> {
            durationOfTime = 180000;
            durationStartTime = 180000;
            changeTime();
        });
        fiveMinButton.setOnClickListener(view -> {
            durationOfTime = 300000;
            durationStartTime = 300000;
            changeTime();
        });
        tenMinButton.setOnClickListener(view -> {
            durationOfTime = 600000;
            durationStartTime = 600000;
            changeTime();
        });

        setTimeButton.setOnClickListener(view -> {
            if(userInputTime.getText().toString().isEmpty()){
                durationOfTime = 0;
                durationStartTime = 0;
                return;
            }
            durationOfTime = Integer.parseInt(userInputTime.getText().toString());
            durationOfTime *= 60000;
            durationStartTime = Integer.parseInt(userInputTime.getText().toString());
            durationStartTime *= 60000;
            changeTime();
        });
    }

    private void changeTime(){
        setUpTime();
        isPaused = true;
        setPlayPause();
    }

    private void startCountDown(){
        endTime = System.currentTimeMillis() + durationOfTime;

        countDownTimer = new CountDownTimer(durationOfTime, 1000) {

            @Override
            public void onTick(long l) {
                durationOfTime = l;
                setUpTime();
            }

            @Override
            public void onFinish() {
                setPlayPause();
                timerEndSound.setLooping(true);
                timerEndSound.start();
                notificationOnEndTime();
            }
        }.start();

        isTimeRunning = true;
    }

    private void stopTimer(){
        if(isTimeRunning){
            if(countDownTimer != null){
                countDownTimer.cancel();
            }
            isTimeRunning = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
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

        setUpTime();
        setupPlayPause();

        if(isTimeRunning){
            endTime = prefs.getLong("endOfTimer", 0);
            durationOfTime = endTime - System.currentTimeMillis();

            if(durationOfTime < 0){
                durationOfTime = 0;
                isTimeRunning = true;
                isPaused = true;
                setUpTime();
            }
            else{
                isTimeRunning = false;
                isPaused = false;
                setPlayPause();
            }
        }
    }

    private void setUpTime(){
        if(durationOfTime == 0){
            String noTime = "00:00:00";
            time.setText(noTime);
            return;
        }

        int totalTimeInSeconds = (int) durationOfTime / 1000;
        int hour = totalTimeInSeconds / 60;
        int min = hour % 60;
        int sec = totalTimeInSeconds % 60;
        hour /= 60;

        String timeDuration = "";
        if(hour < 10){
            timeDuration += "0";
        }
        timeDuration += hour + ":";
        if(min < 10){
            timeDuration += "0";
        }
        timeDuration += min + ":";
        if(sec < 10){
            timeDuration += "0";
        }
        timeDuration += sec;

        time.setText(timeDuration);
    }

    private void setupCancelBtn() {
        this.cancelBtn.setOnClickListener(view -> {
            isPaused = true;
            setPlayPause();
            durationOfTime = 0;
            setUpTime();
            Toast.makeText(TimerActivity.this, R.string.timer_cancelled_toast, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupPlayPause() {
        this.playPause.setOnClickListener(view -> setPlayPause());
    }

    private void clickResetButton(){
        this.resetButton.setOnClickListener(view -> {
            durationOfTime = durationStartTime;
            setUpTime();
            isPaused = true;
            setPlayPause();
        });
    }

    private void setPlayPause() {
        if(this.isTimeRunning || isPaused){
            this.playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
            stopTimer();
            isPaused = false;
        }
        else{
            this.playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_pause_24, getTheme()));
            startCountDown();
            isPaused = true;
        }
    }

    public static void stopTime(){
        timerEndSound.stop();
    }

    /*
    Building a notification found from https://stackoverflow.com/questions/47409256/what-is-notification-channel-idnotifications-not-work-in-api-27
    Using a pending intent found from https://www.youtube.com/watch?v=CZ575BuLBo4
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private void notificationOnEndTime(){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID",
                    "CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("NOTIFICATION_CHANNEL_DESCRIPTION");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("sound","off");
        PendingIntent soundIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle("Time Is Up")
                .setContentText("Click OK to stop the sound and vibration")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.GREEN)
                .addAction(R.drawable.ic_sound, "OK", soundIntent)
                .setAutoCancel(true);

        builder.setContentIntent(pendingIntent);
        manager.notify(0, builder.build());
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, TimerActivity.class);
    }
}