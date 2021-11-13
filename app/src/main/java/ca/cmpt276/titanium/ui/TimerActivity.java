package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.TimerData;
import ca.cmpt276.titanium.model.TimerNotifications;

import java.util.Locale;
import java.util.Objects;

// TODO: Implement DataManager?
// TODO: Implement progress bar

/**
 * This activity represents the timer activity.
 * Shows times that can be set, and buttons that start and cancel the countdown.
 */
public class TimerActivity extends AppCompatActivity {
    private static final boolean IS_CLICKED_DEFAULT = false;
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_HOUR = 3600000;

    private TimerNotifications timerNotifications;
    private Toast toast; // prevents toast stacking
    private TimerData timerData;
    private TimerReceiver timerReceiver;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setTitle(R.string.timerTitle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.timerNotifications = TimerNotifications.getInstance(this);
        boolean isClicked = getIntent().getBooleanExtra("isNotificationClicked", IS_CLICKED_DEFAULT);

        if (isClicked) {
            timerNotifications.dismissNotification(false);
        }

        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.timerData = TimerData.getInstance(this);
        this.timerReceiver = new TimerReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                updateGUI();
            }
        };

        setupButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();

        timerNotifications.dismissNotification(true);

        if (!timerData.isRunning()) {
            updateGUI();
        }

        registerReceiver(timerReceiver, new IntentFilter(TimerService.TIMER_UPDATE_INTENT));
    }

    @Override
    protected void onStop() {
        if (timerData.isRunning()) {
            timerNotifications.launchNotification(true);
        } else if (timerData.isPaused()) {
            timerNotifications.launchNotification(true);
            timerNotifications.changeInteractiveNotification(false);
        }

        unregisterReceiver(timerReceiver);
        super.onStop();
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
            if (timerData.getDurationMilliseconds() <= 0) {
                updateToast(getString(R.string.timer_zero_toast));
            } else if (timerData.isRunning()) {
                timerData.setPaused();
            } else {
                startTimer();
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
        ConstraintLayout inputComponents = findViewById(R.id.inputsConstraintLayout);
        ImageView playPause = findViewById(R.id.timerPlayPauseBtn);

        if (!timerData.isRunning()) {
            inputComponents.setVisibility(View.VISIBLE);
            playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, getTheme()));
        } else {
            inputComponents.setVisibility(View.INVISIBLE);
            playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_pause_24, getTheme()));
        }

        displayTime();
        resetCustomTime();
    }

    private void displayTime() {
        long milliseconds = timerData.getRemainingMilliseconds();
        long hours = milliseconds / MILLIS_IN_HOUR;
        long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
        long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        TextView timerOutput = findViewById(R.id.timer);
        timerOutput.setText(formattedTime);
    }

    private void resetCustomTime() {
        Button setCustomTime = findViewById(R.id.setCustomTimeButton);
        EditText customTime = findViewById(R.id.customMinutes);

        // minimize keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(setCustomTime.getWindowToken(), 0);

        customTime.setText("");
        customTime.clearFocus();
    }

    private void changeTimerDuration(long minutes) {
        timerData.setDurationMilliseconds(10000);//minutes * MILLIS_IN_MINUTE); // TODO: Retest with non-hardcoded values
        resetTimer();
    }

    private void startTimer() { // makes it clear what startService() is doing
        getApplicationContext().startService(new Intent(getApplicationContext(), TimerService.class));
    }

    private void resetTimer() {
        timerNotifications.dismissNotification(false);
        timerData.setStopped();

        if (!timerData.isRunning()) {
            updateGUI();
        }
    }
}
