package ca.cmpt276.titanium.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.BreathManager;
import ca.cmpt276.titanium.model.ChildManager;

public class TakeBreathActivity extends AppCompatActivity {
  public static final int NOT_IN_PROGRESS = 0;
  public static final int IN_PROGRESS = 1;

  private BreathManager breathManager;

  private int state;
  private NumberPicker numPicker;
  private int selectedNumberOfBreaths, breathsRemaining;
  private TextView showNumber;
  private Button mainBtn;
  private boolean isPressed = false, atLeast3Seconds = false;
  private CountDownTimer threeSecond, tenSecond, threeSecondStart;
  private MediaPlayer mPlayer;

  public static Intent makeIntent(Context context) {
    return new Intent(context, TakeBreathActivity.class);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_take_breath);

    MaterialButton goHome = findViewById(R.id.breathGoHomeBtn);
    goHome.setOnClickListener(view -> finish()); //TODO update with proper method
    mPlayer = MediaPlayer.create(TakeBreathActivity.this, R.raw.sound_calm_music);

    breathManager = BreathManager.getInstance(this);

    state = NOT_IN_PROGRESS;
    setupBreathNumPicker();
    mainBtn = findViewById(R.id.breathBtn);
    numPicker.setEnabled(true);
    numPicker.setVisibility(View.VISIBLE);
    numPicker.setValue(selectedNumberOfBreaths);
    showNumber.setVisibility(View.INVISIBLE);

    threeSecondStart = new CountDownTimer(3000, 1000) {
      @Override
      public void onTick(long l) {
      }

      @Override
      public void onFinish() {
        atLeast3Seconds = false;
        if (breathsRemaining > 0) {
          Toast.makeText(TakeBreathActivity.this, getString(R.string.breath_help_in), Toast.LENGTH_SHORT).show();
          changeToIn();
          mainBtn.clearAnimation();
        }
      }
    };

    threeSecond = new CountDownTimer(3000, 1000) {
      @Override
      public void onTick(long l) {
        if (!isPressed) {
          threeSecond.cancel();
          tenSecond.cancel();
        }
      }

      @Override
      public void onFinish() {
        atLeast3Seconds = true;
        if (isPressed) {
          Toast.makeText(TakeBreathActivity.this, getString(R.string.breath_help_out_10), Toast.LENGTH_SHORT).show();
          changeToOut();
        }
        if (!isPressed) {
          mainBtn.setText(TakeBreathActivity.this.getString(R.string.breath_in));
          tenSecond.cancel();
        }
      }
    };

    tenSecond = new CountDownTimer(10000, 1000) {
      @Override
      public void onTick(long l) {
        if (!isPressed) {
          threeSecond.cancel();
          tenSecond.cancel();
        }
      }

      @Override
      public void onFinish() {
        if (isPressed) {
          mainBtn.clearAnimation();
          mPlayer.pause();
          changeToOut();
          Toast.makeText(TakeBreathActivity.this, getString(R.string.breath_help_out_10), Toast.LENGTH_SHORT).show();
          if (atLeast3Seconds) {
            threeSecondStart.start();
          }
        }
      }
    };

    mainBtn.setOnTouchListener((view, motionEvent) -> {
      if (state == NOT_IN_PROGRESS) {
        numPicker.setEnabled(false);
        numPicker.setVisibility(View.INVISIBLE);
        showNumber.setText(Integer.toString(selectedNumberOfBreaths));
        showNumber.setVisibility(View.VISIBLE);
        breathManager.setNumBreaths(selectedNumberOfBreaths);
        breathManager.saveData();
        breathsRemaining = selectedNumberOfBreaths;
        state = IN_PROGRESS;
      }

      if (state == IN_PROGRESS) {
        if (breathsRemaining >= 0) {
          showNumber.setText(Integer.toString(breathsRemaining));
          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              mPlayer.start();
              isPressed = true;
              changeToIn();
              threeSecond.start();
              tenSecond.start();
              return true;
            case MotionEvent.ACTION_UP:
              mPlayer.pause();
              mainBtn.clearAnimation();
              isPressed = false;
              if (atLeast3Seconds) {
                breathsRemaining--;
              }
              if (breathsRemaining == 0) {
                mainBtn.clearAnimation();
                mainBtn.setText(R.string.breath_done);
                numPicker.setVisibility(View.VISIBLE);
                showNumber.setVisibility(View.INVISIBLE);
                numPicker.setEnabled(true);
                state = NOT_IN_PROGRESS;
              }
              return true;
          }
        }
      }
      return false;
    });
  }

  private void setupBreathNumPicker() {
    //reference for number picker: https://www.youtube.com/watch?v=dWq5CJDBDVE
    selectedNumberOfBreaths = breathManager.getNumBreaths();

    showNumber = findViewById(R.id.breathSelectedNumber);
    numPicker = findViewById(R.id.breathNumPicker);

    numPicker.setMinValue(1);
    numPicker.setMaxValue(10);
    numPicker.setOnValueChangedListener((picker, oldVal, newVal) -> selectedNumberOfBreaths = newVal);
  }

  private void changeToIn() {
    mainBtn.setText(TakeBreathActivity.this.getString(R.string.breath_in));
    mainBtn.startAnimation(AnimationUtils.loadAnimation(TakeBreathActivity.this, R.anim.breath_button));
  }

  private void changeToOut() {
    mainBtn.setText(TakeBreathActivity.this.getString(R.string.breath_out));
  }
}