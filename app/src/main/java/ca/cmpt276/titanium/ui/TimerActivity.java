package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.TimerInfo;

public class TimerActivity extends AppCompatActivity {
    private TimerInfo timerInfo = TimerInfo.getInstance(TimerActivity.this);
    private ImageView playPause;
    private Button cancelBtn, resetButton;
    private Button oneMinButton, twoMinButton, threeMinButton, fiveMinButton, tenMinButton;
    private Button setTimeButton;
    private EditText userInputTime;
    private boolean isPause;
    private boolean isTimeRunning;
    private boolean isReset;
    long durationOfTime = 0;
    long durationStartTime = 0;
    private TextView time;
    private CountDownTimer countDownTimer;
    private int hour, min , sec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupTitle();
        setupAttributes();
        setupPlayPause();
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
        this.isPause = false;
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
            durationOfTime = Integer.parseInt(userInputTime.getText().toString());
            durationOfTime *= 60000;
            durationStartTime = Integer.parseInt(userInputTime.getText().toString());
            durationStartTime *= 60000;
            changeTime();
        });
    }

    private void changeTime(){
        setUpTime();
        stopTimer();
        isPause = false;
        setPlayPause();
    }

    private void startCountDown(){
        countDownTimer = new CountDownTimer(durationOfTime, 1000) {

            @Override
            public void onTick(long l) {
                durationOfTime = l;
                setUpTime();
            }

            @Override
            public void onFinish() {
                isPause = false;
                isTimeRunning = false;
                setPlayPause();
            }
        }.start();

        isTimeRunning = true;
    }

    private void stopTimer(){
        if(isTimeRunning){
            countDownTimer.cancel();
            isTimeRunning = false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("time", durationOfTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        durationOfTime = savedInstanceState.getLong("time");
        time.setText(String.valueOf(durationOfTime));
    }

    private void setUpTime(){
        if(durationOfTime == 0){
            String noTime = "00:00:00";
            time.setText(noTime);
            return;
        }

        if(isReset){
            durationOfTime = durationStartTime;
        }
        isReset = false;

        int totalTimeInSeconds = (int) durationOfTime / 1000;
        hour = totalTimeInSeconds / 60;
        min = hour % 60;
        sec = totalTimeInSeconds % 60;
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
            stopTimer();
            isPause = false;
            isTimeRunning = false;
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
            isReset = true;
            setUpTime();
            isPause = false;
            setPlayPause();
        });
    }

    private void setPlayPause() {
        if(this.isPause){
            this.playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_pause_24, getTheme()));
            startCountDown();
            this.isPause =false;
        }
        else{
            this.playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
            stopTimer();
            this.isPause =true;
        }
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, TimerActivity.class);
    }
}