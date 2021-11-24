package ca.cmpt276.titanium.ui.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;
import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Timer;

/**
 * This activity represents the timer activity.
 * Shows times that can be set, and buttons that start and cancel the countdown.
 */
public class TimerActivity extends AppCompatActivity {
    private static final boolean IS_CLICKED_DEFAULT = false;
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_HOUR = 3600000;

    private TimerNotification timerNotification;
    private Toast toast; // prevents toast stacking
    private Timer timer;
    private BroadcastReceiver timerReceiver;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);

        setTitle(R.string.timerTitle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.timerNotification = TimerNotification.getInstance(this);
        boolean isClicked = getIntent().getBooleanExtra("isNotificationClicked", IS_CLICKED_DEFAULT);

        if (isClicked) {
            timerNotification.dismissNotification(false);
        }

        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.timer = Timer.getInstance(this);
        this.timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateGUI();
            }
        };

        setupCircularProgressBar();
        setupButtons();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerNotification.dismissNotification(true);
        timer.setGUIEnabled(true);
        registerReceiver(timerReceiver, new IntentFilter(TimerService.TIMER_UPDATE_INTENT));

        if (!timer.isRunning()) {
            updateGUI();
        }
    }

    @Override
    protected void onStop() {
        if (timer.isRunning()) {
            timerNotification.launchNotification(getString(R.string.timer_notification_pause_button));
        } else if (timer.isPaused()) {
            timerNotification.launchNotification(getString(R.string.timer_notification_resume_button));
        }

        timer.setGUIEnabled(false);
        unregisterReceiver(timerReceiver);
        super.onStop();
    }

    private void setupCircularProgressBar() { // rotates progress bar so that it starts at top
        ProgressBar circularProgressBar = findViewById(R.id.circularProgressBar);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.timer_progress_bar);
        circularProgressBar.startAnimation(animation);
    }

    private void setupButtons() {
        // preset input
        Button oneMinute = findViewById(R.id.oneMin);
        Button twoMinutes = findViewById(R.id.twoMin);
        Button threeMinutes = findViewById(R.id.threeMin);
        Button fiveMinutes = findViewById(R.id.fiveMin);
        Button tenMinutes = findViewById(R.id.tenMin);

        oneMinute.setOnClickListener(view -> changeTimerDuration(1));
        twoMinutes.setOnClickListener(view -> changeTimerDuration(2));
        threeMinutes.setOnClickListener(view -> changeTimerDuration(3));
        fiveMinutes.setOnClickListener(view -> changeTimerDuration(5));
        tenMinutes.setOnClickListener(view -> changeTimerDuration(10));

        // custom input
        Button setCustomTime = findViewById(R.id.setCustomTimeButton);
        EditText customTime = findViewById(R.id.customMinutes);

        setCustomTime.setOnClickListener(view -> updateCustomTime(customTime));

        customTime.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                updateCustomTime(customTime);
            }

            return false;
        });

        // play/pause
        ImageView playPause = findViewById(R.id.timerPlayPauseBtn);
        playPause.setOnClickListener(view -> {
            if (timer.getDurationMilliseconds() == 0) {
                updateToast(getString(R.string.timer_zero_toast));
            } else if (timer.isRunning()) {
                timer.setPaused();
            } else {
                // TODO: Allow user to input different timeFactor values
                timer.setTimeFactor(2.0f);
                getApplicationContext().startService(new Intent(getApplicationContext(), TimerService.class)); // start timer
            }
        });

        // reset
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(view -> resetTimer());
    }

    private void updateCustomTime(EditText customTime) {
        if (customTime.getText().toString().isEmpty()) {
            updateGUI();
            updateToast(getString(R.string.custom_time_toast));
        } else {
            changeTimerDuration(Long.parseLong(customTime.getText().toString()));
        }
    }

    private void updateToast(String toastText) {
        toast.cancel();
        toast.setText(toastText);
        toast.show();
    }

    private void updateGUI() {
        ProgressBar circularProgressBar = findViewById(R.id.circularProgressBar);
        int progress;

        if (timer.getDurationMilliseconds() == 0) {
            progress = 0;
        } else {
            progress = (int) ((timer.getDurationMilliseconds() - timer.getRemainingMilliseconds()) * circularProgressBar.getMax() / timer.getDurationMilliseconds());
        }

        circularProgressBar.setProgress(progress);

        ConstraintLayout inputComponents = findViewById(R.id.inputsConstraintLayout);
        ImageView playPause = findViewById(R.id.timerPlayPauseBtn);

        if (!timer.isRunning()) {
            inputComponents.setVisibility(View.VISIBLE);
            playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_white_24, getTheme()));
        } else {
            inputComponents.setVisibility(View.INVISIBLE);
            playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_pause_white_24, getTheme()));
        }

        displayTime();
        resetCustomTime();
    }

    private void displayTime() {
        long milliseconds = timer.getRemainingMilliseconds();
        long hours = milliseconds / MILLIS_IN_HOUR;
        long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
        long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        TextView timerOutput = findViewById(R.id.timer);
        timerOutput.setText(formattedTime);
    }

    private void resetCustomTime() {
        // minimize keyboard
        Button setCustomTime = findViewById(R.id.setCustomTimeButton);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(setCustomTime.getWindowToken(), 0);

        EditText customTime = findViewById(R.id.customMinutes);
        customTime.setText("");
        customTime.clearFocus();
    }

    private void changeTimerDuration(long minutes) {
        timer.setDurationMilliseconds(minutes * MILLIS_IN_MINUTE);
        resetTimer();
    }

    private void resetTimer() {
        timerNotification.dismissNotification(false);
        timer.setStopped();

        if (!timer.isRunning()) {
            updateGUI();
        }
    }
}
