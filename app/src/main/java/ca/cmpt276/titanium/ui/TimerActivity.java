package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import ca.cmpt276.titanium.R;

public class TimerActivity extends AppCompatActivity {
    private ImageView playPause;
    private Button cancelBtn;
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupAttributes();
        setupPlayPause();
        setupCancelBtn();
    }

    private void setupAttributes() {
        this.playPause = findViewById(R.id.timerPlayPauseBtn);
        this.cancelBtn = findViewById(R.id.timerCancelBtn);
        this.isPause = false;
    }

    private void setupCancelBtn() {
        this.cancelBtn.setOnClickListener(view -> {
            Toast.makeText(TimerActivity.this, "Timer cancelled", Toast.LENGTH_SHORT).show();
            setPlayPause();
        });
    }

    private void setupPlayPause() {
        this.playPause.setOnClickListener(view -> {
            setPlayPause();
        });
    }

    private void setPlayPause() {
        if(this.isPause){
            this.playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, getTheme()));
            this.isPause =false;
        }
        else{
            this.playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, getTheme()));
            this.isPause =true;
        }
    }

    public static Intent makeLaunchIntent(Context c){
        return new Intent(c, TimerActivity.class);
    }
}