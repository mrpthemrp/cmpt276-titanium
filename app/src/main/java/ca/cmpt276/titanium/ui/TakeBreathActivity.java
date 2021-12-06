package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import ca.cmpt276.titanium.model.BreathManager;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import ca.cmpt276.titanium.R;

public class TakeBreathActivity extends AppCompatActivity {
  public static final int START_SCREEN = 0;
  public static final int IN_PROGRESS = 1;
  public static final int END_SCREEN = 2;

  public static final int BREATH_IN = 0;
  public static final int BREATH_OUT_3SEC = 1;
  public static final int BREATH_OUT_10SEC = 2;

  private int state, breathState;
  private NumberPicker numPicker;
  private int selectedNumberOfBreaths, breathsRemaining;
  private TextView showNumber;
  private Button mainBtn;

  public static Intent makeIntent(Context context) {
    return new Intent(context, TakeBreathActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_take_breath);

    state = START_SCREEN;
    breathState = BREATH_IN;
    MaterialButton goHome = findViewById(R.id.breathGoHomeBtn);
    goHome.setOnClickListener(view -> finish()); //TODO update with proper method

    setupBreathNumPicker();
    mainBtn = findViewById(R.id.breathBtn);
    //STATES
    if (state == START_SCREEN) {
      //do smt
      numPicker.setEnabled(true);
      numPicker.setVisibility(View.VISIBLE);
      showNumber.setVisibility(View.INVISIBLE);
      breathsRemaining = selectedNumberOfBreaths;
    }
    if (state == END_SCREEN) {
      mainBtn.setText(R.string.breath_done);
      numPicker.setVisibility(View.VISIBLE);
      showNumber.setVisibility(View.INVISIBLE);
      numPicker.setEnabled(true);
    }

    mainBtn.setOnClickListener(view -> {
      if (state == START_SCREEN) {
        numPicker.setEnabled(false);
        numPicker.setVisibility(View.INVISIBLE);
        showNumber.setText(Integer.toString(selectedNumberOfBreaths));
        showNumber.setVisibility(View.VISIBLE);
        state = IN_PROGRESS;
      }
    });



    mainBtn.setOnTouchListener((view, motionEvent) -> {
      if(state == IN_PROGRESS) {
        if (breathsRemaining == 0) {
          breathState = BREATH_IN;
          state = END_SCREEN;
          return false;
        }
        if (breathsRemaining > 0) {
          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              mainBtn.setText(getString(R.string.breath_in));
              mainBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.breath_button));
              breathState = BREATH_OUT_3SEC;
              return true;
            case MotionEvent.ACTION_UP:
              mainBtn.setText(getString(R.string.breath_out));
              breathsRemaining--;
              mainBtn.clearAnimation();
              breathState = BREATH_IN;
              return false;
          }
        }
      }
      return false;
    });
  }

  private void setupBreathNumPicker() {
    //reference for number picker: https://www.youtube.com/watch?v=dWq5CJDBDVE
    selectedNumberOfBreaths = 1;// default value
    showNumber = findViewById(R.id.breathSelectedNumber);

    numPicker = findViewById(R.id.breathNumPicker);

    numPicker.setMinValue(1);
    numPicker.setMaxValue(10);

    numPicker.setOnValueChangedListener((picker, oldVal, newVal) -> selectedNumberOfBreaths = newVal);
  }
}