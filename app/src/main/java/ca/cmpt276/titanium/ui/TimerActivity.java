package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import ca.cmpt276.titanium.R;

public class TimerActivity extends AppCompatActivity {
    private ImageView playPause;
    private Button cancelBtn, oneMinButton, twoMinButton, threeMinButton, fiveMinButton,tenMinbutton;
    private Button setTimeButton;
    private View userInputTime;
    private boolean isPause;//get from sharedPreferences?
    private boolean inputIsSet;
    long durationOfTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupTitle();
        setupAttributes();
        setupPlayPause();
        setupCancelBtn();
        //make below a method onc shared preferences is sorted out
        if(!this.inputIsSet){
            setupInput(); //this method implements text watcher
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
        this.isPause = false;
        this.inputIsSet = false;
    }

    private void setUpButton(){
        oneMinButton.setOnClickListener(view -> {
            durationOfTime = TimeUnit.MINUTES.toMinutes(1);
        });
        twoMinButton.setOnClickListener(view -> {
            durationOfTime = TimeUnit.MINUTES.toMinutes(2);
        });
        threeMinButton.setOnClickListener(view -> {
            durationOfTime = TimeUnit.MINUTES.toMinutes(3);
        });
        fiveMinButton.setOnClickListener(view -> {
            durationOfTime = TimeUnit.MINUTES.toMinutes(5);
        });
        tenMinbutton.setOnClickListener(view -> {
            durationOfTime = TimeUnit.MINUTES.toMinutes(10);
        });
    }



    private void setupInput() {
        //to do
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
            this.isPause =false;
        }
        else{
            this.playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
            this.isPause =true;
        }
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, TimerActivity.class);
    }
}