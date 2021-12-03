package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ca.cmpt276.titanium.R;

/**
 * Displays app information.
 *
 * @author Titanium
 */
public class HelpActivity extends AppCompatActivity {
  public static Intent makeIntent(Context context) {
    return new Intent(context, HelpActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);

    setSupportActionBar(findViewById(R.id.ToolBar_help));
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
  }
}
