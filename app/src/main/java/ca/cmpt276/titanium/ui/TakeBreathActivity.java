package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ca.cmpt276.titanium.R;

public class TakeBreathActivity extends AppCompatActivity {
  public static final int START_SCREEN =0;
  public static final int IN_PROGRESS =1;
  public static final int END_SCREEN =2;

  public static final int BREATH_IN =0;
  public static final int BREATH_OUT_3SEC =1;
  public static final int BREATH_OUT_10SEC =2;

  private int state, breathState;
  private NumberPicker numPicker;
  private int selectedNumberOfBreaths;
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

    mainBtn.setOnClickListener(view -> {
      //STATES
      if(state == START_SCREEN){
        //do smt
        numPicker.setEnabled(false);
        numPicker.setVisibility(View.INVISIBLE);
        showNumber.setVisibility(View.VISIBLE);
        state = IN_PROGRESS;
      }
      if(state == IN_PROGRESS){
        if(selectedNumberOfBreaths >= 0){
          if(selectedNumberOfBreaths == 0){
            state = END_SCREEN;
          }
          if(breathState == BREATH_IN){
            //do smt
            mainBtn.setText(R.string.breath_in);
            breathState = BREATH_OUT_3SEC;
          }
          else if(breathState == BREATH_OUT_3SEC){
            //do smt
            mainBtn.setText(R.string.breath_out);
            breathState = BREATH_IN;
//             if(){
//             }
            selectedNumberOfBreaths--;
          }
        }
        if(state == END_SCREEN){
          mainBtn.setText(R.string.breath_done);
          showNumber.setVisibility(View.INVISIBLE);
          numPicker.setVisibility(View.VISIBLE);
          numPicker.setEnabled(true);
        }
      }

    });
  }

  private void setupBreathNumPicker() {
    //reference for number picker: https://www.youtube.com/watch?v=dWq5CJDBDVE
    selectedNumberOfBreaths =1;// default value
    showNumber = findViewById(R.id.breathSelectedNumber);

    numPicker = findViewById(R.id.breathNumPicker);

    numPicker.setMinValue(1);
    numPicker.setMaxValue(10);

    numPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
        selectedNumberOfBreaths = newVal;
    });
  }
}