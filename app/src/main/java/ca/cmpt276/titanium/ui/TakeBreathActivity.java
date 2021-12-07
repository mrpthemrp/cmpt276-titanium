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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.BreathManager;

public class TakeBreathActivity extends AppCompatActivity {
  public static final int NOT_IN_PROGRESS = 0;
  public static final int IN_PROGRESS = 1;

  private final long SECONDS_3 = 3;
  private final long SECONDS_10 = 10;


  private int state;
  private long startTime, timeHeld, currentTime, previousSecond;
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

    MaterialButton goHome = findViewById(R.id.breathGoHomeBtn);
    goHome.setOnClickListener(view -> finish()); //TODO update with proper method

    state = NOT_IN_PROGRESS;
    startTime = 0;
    timeHeld =0;
    setupBreathNumPicker();
    mainBtn = findViewById(R.id.breathBtn);
    numPicker.setEnabled(true);
    numPicker.setVisibility(View.VISIBLE);
    showNumber.setVisibility(View.INVISIBLE);


    mainBtn.setOnTouchListener((view, motionEvent) -> {
      if (state == NOT_IN_PROGRESS) {
        numPicker.setEnabled(false);
        numPicker.setVisibility(View.INVISIBLE);
        showNumber.setText(Integer.toString(selectedNumberOfBreaths));
        showNumber.setVisibility(View.VISIBLE);
        breathsRemaining = selectedNumberOfBreaths;
        BreathManager.setNumBreaths(selectedNumberOfBreaths);
        state = IN_PROGRESS;
      }

      if (state == IN_PROGRESS) {
        if (breathsRemaining >= 0) {
          calculateTimeHeld();

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              startTime = System.currentTimeMillis();
              changeToIn();
              return true;
            case MotionEvent.ACTION_UP:
              mainBtn.clearAnimation();
              changeToOut();
              if (breathsRemaining == 0) {
                mainBtn.setText(R.string.breath_done);
                numPicker.setVisibility(View.VISIBLE);
                showNumber.setVisibility(View.INVISIBLE);
                numPicker.setEnabled(true);
                state = NOT_IN_PROGRESS;
              }
              return true;
          }
          if(timeHeld == SECONDS_3) {
            timeHeld = 0;
            Toast.makeText(this, "3 seoncds", Toast.LENGTH_SHORT).show();
          }
          if(timeHeld == SECONDS_10){
            Toast.makeText(this, "10 seoncds", Toast.LENGTH_SHORT).show();
            timeHeld =0;
          }
        }
      }
      return false;
    });
  }

  private void setupBreathNumPicker() {
    //reference for number picker: https://www.youtube.com/watch?v=dWq5CJDBDVE
    selectedNumberOfBreaths = 1;

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
    breathsRemaining--;
    mainBtn.setText(TakeBreathActivity.this.getString(R.string.breath_out));
    showNumber.setText(Integer.toString(breathsRemaining));
  }

  private void calculateTimeHeld() {
    previousSecond = currentTime;
    currentTime = System.currentTimeMillis() - startTime;
    if (currentTime != previousSecond) {
      currentTime /= 1000;
      if(currentTime == SECONDS_3){
        timeHeld = 3;
      }
      if (currentTime == SECONDS_10){
        timeHeld =10;
      }
    }
  }
}