package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import ca.cmpt276.titanium.R;

public class TakeBreathActivity extends AppCompatActivity {
  public static final int START_SCREEN =0;
  public static final int IN_PROGRESS =1;
  public static final int END_SCREEN =2;

  public static Intent makeIntent(Context context) {
    return new Intent(context, TakeBreathActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_take_breath);

    MaterialButton goHome = findViewById(R.id.breathGoHomeBtn);
    goHome.setOnClickListener(view -> finish()); //TODO update with proper method
  }
}