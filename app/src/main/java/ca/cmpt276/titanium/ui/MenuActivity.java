package ca.cmpt276.titanium.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.ui.coinflip.CoinFlipActivity;
import ca.cmpt276.titanium.ui.tasks.TasksActivity;
import ca.cmpt276.titanium.ui.timer.TimerActivity;

/**
 * This activity represents the main menu.
 * Shows children and buttons to the timer and coin flip.
 */
public class MenuActivity extends AppCompatActivity {
  private ChildManager childManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);

    this.childManager = ChildManager.getInstance(this);

    MaterialButton addChild = findViewById(R.id.MaterialButton_menu_add_child);
    addChild.setOnClickListener(view -> startActivity(ChildActivity.makeIntent(
        this,
        getString(R.string.title_menu_child_add),
        null)));

    MaterialButton coinFlip = findViewById(R.id.MaterialButton_menu_coin_flip);
    coinFlip.setOnClickListener(view -> startActivity(CoinFlipActivity.makeIntent(this)));

    MaterialButton timer = findViewById(R.id.MaterialButton_menu_timer);
    timer.setOnClickListener(view -> startActivity(TimerActivity.makeIntent(this)));

    MaterialButton tasks = findViewById(R.id.MaterialButton_menu_tasks);
    tasks.setOnClickListener(view -> startActivity(TasksActivity.makeIntent(this)));

    MaterialButton help = findViewById(R.id.MaterialButton_menu_help);
    help.setOnClickListener(view -> startActivity(HelpActivity.makeIntent(this)));
  }

  @Override
  protected void onResume() {
    super.onResume();
    displayChildren();
    setMessage();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_item_child_edit) {
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  private void setMessage() {
    TextView welcomeMessage = findViewById(R.id.TextView_menu_welcome_message);

    Calendar calendar = Calendar.getInstance();
    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

    if (currentHour <= 12) {
      welcomeMessage.setText(getString(R.string.subtitle_menu_welcome_morning));
    } else if (currentHour <= 16) {
      welcomeMessage.setText(getString(R.string.subtitle_menu_welcome_afternoon));
    } else {
      welcomeMessage.setText(getString(R.string.subtitle_menu_welcome_evening));
    }
  }

  private void displayChildren() {
    if (childManager.getChildren().size() == 0) {
      findViewById(R.id.TextView_menu_empty_state_message).setVisibility(View.VISIBLE);
    } else {
      findViewById(R.id.TextView_menu_empty_state_message).setVisibility(View.INVISIBLE);
    }

    ChildAdapter adapter = new ChildAdapter(this, childManager.getChildren());

    ListView childrenListView = (ListView) findViewById(R.id.ListView_menu_children);
    childrenListView.setAdapter(adapter);
    childrenListView.setClickable(true);

    childrenListView.setOnItemClickListener((parent, view, position, id) ->
        startActivity(ChildActivity.makeIntent(
            this,
            getString(R.string.title_menu_child_manage),
            childManager.getChildren().get(position).getUniqueID())));
  }
}
