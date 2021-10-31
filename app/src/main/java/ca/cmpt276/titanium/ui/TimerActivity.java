package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ca.cmpt276.titanium.R;

public class TimerActivity extends AppCompatActivity {
    private ImageView playPause;
    private Button cancelBtn, oneMinButton, twoMinButton, threeMinButton, fiveMinButton,tenMinbutton;
    private Button setTimeButton;
    private EditText userInputTime;
    private boolean isPause;//get from sharedPreferences?
    private boolean inputIsSet;
    private boolean isTimeRunning;
    long durationOfTime = 0;
    private TextView time;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupTitle();
        setupAttributes();
        setupPlayPause();
        setupCancelBtn();
        setUpButton();
        //make below a method onc shared preferences is sorted out
        if(!this.inputIsSet){
            // ?
        }
    }

    private void setupTitle() {
        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.timerTitle);
    }

    private void setupAttributes() {
        this.playPause = findViewById(R.id.timerPlayPauseBtn);
        this.cancelBtn = findViewById(R.id.timerCancelBtn);
        this.oneMinButton = findViewById(R.id.oneMin);
        this.twoMinButton = findViewById(R.id.twoMin);
        this.threeMinButton = findViewById(R.id.threeMin);
        this.fiveMinButton = findViewById(R.id.fiveMin);
        this.tenMinbutton = findViewById(R.id.tenMin);
        this.userInputTime = findViewById(R.id.editTextNumber);
        this.setTimeButton = findViewById(R.id.setTimeButton);
        this.time = findViewById(R.id.timer);

        this.isPause = false;
        this.inputIsSet = false;
    }

    private void setUpButton(){
        oneMinButton.setOnClickListener(view -> {
            durationOfTime = 60000;
            setUpTime();
        });
        twoMinButton.setOnClickListener(view -> {
            durationOfTime = 120000;
            setUpTime();
        });
        threeMinButton.setOnClickListener(view -> {
            durationOfTime = 180000;
            setUpTime();
        });
        fiveMinButton.setOnClickListener(view -> {
            durationOfTime = 300000;
            setUpTime();
        });
        tenMinbutton.setOnClickListener(view -> {
            durationOfTime = 600000;
            setUpTime();
        });

        setTimeButton.setOnClickListener(view -> {
            durationOfTime = Integer.parseInt(userInputTime.getText().toString());
            setUpTime();
        });
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
            return;
        }
        int min = (int) durationOfTime / 60000;
        int sec = (int) durationOfTime % 60000 / 1000;
        String timeDuration = "";
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
            //only change if the timer is running
            if(!this.isPause){
                setPlayPause();
                Toast.makeText(TimerActivity.this, R.string.timer_cancelled_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPlayPause() {
        this.playPause.setOnClickListener(view -> setPlayPause());
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