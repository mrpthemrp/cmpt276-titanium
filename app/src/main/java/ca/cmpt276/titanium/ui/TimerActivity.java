package ca.cmpt276.titanium.ui;

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
import android.widget.Toast;

import ca.cmpt276.titanium.R;

public class TimerActivity extends AppCompatActivity {
    private ImageView playPause;
    private Button cancelBtn;
    //private long hours, minutes, seconds;
    private boolean isPause;//get from sharedPreferences?
    private boolean inputIsSet;
    private CountDownTimer timer;

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
        this.isPause = false;
        this.inputIsSet = false;
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