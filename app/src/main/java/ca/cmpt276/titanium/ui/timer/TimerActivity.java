package ca.cmpt276.titanium.ui.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;
import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Timer;

/**
 * Displays timer information.
 * Allows a user to start and interact with a timer.
 *
 * @author Titanium
 */
public class TimerActivity extends AppCompatActivity {
  private static final boolean IS_CLICKED_DEFAULT = false;
  private static final int MILLIS_IN_SECOND = 1000;
  private static final int MILLIS_IN_MINUTE = 60000;
  private static final int MILLIS_IN_HOUR = 3600000;

  private TimerNotification timerNotification;
  private Timer timer;
  private Toast toast; // prevents toast stacking
  private BroadcastReceiver timerReceiver;

  public static Intent makeIntent(Context context) {
    return new Intent(context, TimerActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timer);

    setSupportActionBar(findViewById(R.id.ToolBar_timer));
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    this.timerNotification = TimerNotification.getInstance(this);

    if (getIntent().getBooleanExtra("isClicked", IS_CLICKED_DEFAULT)) {
      timerNotification.dismissNotification(false);
    }

    this.timer = Timer.getInstance(this);
    this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    this.timerReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        updateGUI();
      }
    };

    setupTimerProgressBar();
    setupButtons();
  }

  @Override
  protected void onStart() {
    super.onStart();
    timerNotification.dismissNotification(true);
    registerReceiver(timerReceiver, new IntentFilter(TimerService.TIMER_UPDATE_INTENT));
    timer.setGUIEnabled(true);

    if (!timer.isRunning()) {
      updateGUI();
    }
  }

  @Override
  protected void onStop() {
    if (timer.isRunning()) {
      timerNotification.launchNotification(getString(R.string.button_timer_notification_pause));
    } else if (timer.isPaused()) {
      timerNotification.launchNotification(getString(R.string.button_timer_notification_resume));
    }

    timer.setGUIEnabled(false);
    unregisterReceiver(timerReceiver);
    super.onStop();
  }

  private void setupTimerProgressBar() { // rotates progress bar so that it starts at top
    ProgressBar timerProgressBar = findViewById(R.id.ProgressBar_timer);
    timerProgressBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.timer_progress_bar));
  }

  private void setupButtons() {
    // preset input
    Button oneMinute = findViewById(R.id.Button_timer_1_minute);
    Button twoMinutes = findViewById(R.id.Button_timer_2_minutes);
    Button threeMinutes = findViewById(R.id.Button_timer_3_minutes);
    Button fiveMinutes = findViewById(R.id.Button_timer_5_minutes);
    Button tenMinutes = findViewById(R.id.Button_timer_10_minutes);

    oneMinute.setOnClickListener(view -> changeTimerDuration(1));
    twoMinutes.setOnClickListener(view -> changeTimerDuration(2));
    threeMinutes.setOnClickListener(view -> changeTimerDuration(3));
    fiveMinutes.setOnClickListener(view -> changeTimerDuration(5));
    tenMinutes.setOnClickListener(view -> changeTimerDuration(10));

    // custom input
    Button setCustomTime = findViewById(R.id.Button_timer_set_custom_input);
    EditText customTime = findViewById(R.id.EditText_timer_enter_custom_input);

    setCustomTime.setOnClickListener(view -> updateCustomTime(customTime));

    customTime.setOnKeyListener((view, keyCode, keyEvent) -> {
      if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
        updateCustomTime(customTime);
      }

      return false;
    });

    // play/pause
    ImageView playPause = findViewById(R.id.ImageView_timer_control);
    playPause.setOnClickListener(view -> {
      if (timer.getDurationMilliseconds() == 0) {
        updateToast(getString(R.string.toast_timer_zero_minutes));
      } else if (timer.isRunning()) {
        timer.setPaused();
      } else {
        Intent timerServiceIntent = new Intent(getApplicationContext(), TimerService.class);
        getApplicationContext().startService(timerServiceIntent); // start timer
      }
    });

    // reset
    Button resetButton = findViewById(R.id.Button_timer_reset);
    resetButton.setOnClickListener(view -> resetTimer());

    // time factor
    Button timeFactorButton = findViewById(R.id.Button_timer_time_factor);
    timeFactorButton.setOnClickListener(view -> selectTimeFactor());
  }

  private void changeTimerDuration(long minutes) {
    timer.setDurationMilliseconds(minutes * MILLIS_IN_MINUTE);
    resetTimer();
  }

  private void updateCustomTime(EditText customTime) {
    if (customTime.getText().toString().isEmpty()) {
      updateGUI();
      updateToast(getString(R.string.toast_timer_enter_time));
    } else {
      changeTimerDuration(Long.parseLong(customTime.getText().toString()));
    }
  }

  private void updateToast(String toastText) {
    toast.cancel();
    toast.setText(toastText);
    toast.show();
  }

  private void resetTimer() {
    timerNotification.dismissNotification(false);
    timer.setStopped();

    if (!timer.isRunning()) {
      updateGUI();
    }
  }

  private void selectTimeFactor() {
    final String[] dialogOptions = {
        getString(R.string.prompt_option1_timer_select_time_factor),
        getString(R.string.prompt_option2_timer_select_time_factor),
        getString(R.string.prompt_option3_timer_select_time_factor),
        getString(R.string.prompt_option4_timer_select_time_factor),
        getString(R.string.prompt_option5_timer_select_time_factor),
        getString(R.string.prompt_option6_timer_select_time_factor),
        getString(R.string.prompt_option7_timer_select_time_factor),
        getString(R.string.prompt_option8_timer_select_time_factor)
    };

    new android.app.AlertDialog.Builder(this)
        .setTitle(R.string.prompt_title_timer_select_time_factor)
        .setItems(dialogOptions, (dialog, item) -> {
          if (!dialogOptions[item].equals("Cancel")) {
            float timeFactor = Float.parseFloat(
                dialogOptions[item].substring(0, dialogOptions[item].length() - 1));
            timer.setTimeFactor(Math.round(timeFactor) / 100f);
            updateGUI();
          }
        }).show();
  }

  private void updateGUI() {
    ProgressBar circularProgressBar = findViewById(R.id.ProgressBar_timer);
    int progress;

    if (timer.getDurationMilliseconds() == 0) {
      progress = 0;
    } else {
      long durationMilliseconds =
          timer.isRunning()
              ? (long) (timer.getDurationMilliseconds() / timer.getTimeFactor())
              : timer.getDurationMilliseconds();

      progress = (int) ((durationMilliseconds - timer.getRemainingMilliseconds())
          * circularProgressBar.getMax()
          / durationMilliseconds);
    }

    circularProgressBar.setProgress(progress);

    ConstraintLayout inputComponents = findViewById(R.id.ConstraintLayout_timer_inputs);
    ImageView playPause = findViewById(R.id.ImageView_timer_control);

    if (!timer.isRunning()) {
      inputComponents.setVisibility(View.VISIBLE);
      playPause.setImageResource(R.drawable.ic_baseline_play_arrow_white_24);
    } else {
      inputComponents.setVisibility(View.INVISIBLE);
      playPause.setImageResource(R.drawable.ic_baseline_pause_white_24);
    }

    Button timeFactorButton = findViewById(R.id.Button_timer_time_factor);
    String timeFactor = String.format(Locale.getDefault(), "%1.2fx", timer.getTimeFactor());
    timeFactorButton.setText(timeFactor);
    timeFactorButton.setEnabled(!timer.isRunning() && !timer.isPaused());

    displayTime();
    resetCustomTime();
  }

  private void displayTime() {
    long milliseconds =
        timer.isRunning()
            ? (long) (timer.getRemainingMilliseconds() * timer.getTimeFactor())
            : timer.getRemainingMilliseconds();

    long hours = milliseconds / MILLIS_IN_HOUR;
    long minutes = (milliseconds % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
    long seconds = (milliseconds % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;
    String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

    TextView timerOutput = findViewById(R.id.TextView_timer_time);
    timerOutput.setText(time);
  }

  private void resetCustomTime() {
    Button setCustomTime = findViewById(R.id.Button_timer_set_custom_input);
    InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(setCustomTime.getWindowToken(), 0);

    EditText customTime = findViewById(R.id.EditText_timer_enter_custom_input);
    customTime.setText("");
    customTime.clearFocus();
  }
}
