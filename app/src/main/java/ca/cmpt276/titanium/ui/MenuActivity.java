package ca.cmpt276.titanium.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.ui.coinflip.CoinFlipActivity;
import ca.cmpt276.titanium.ui.tasks.TasksActivity;
import ca.cmpt276.titanium.ui.timer.TimerActivity;

/**
 * Displays the main menu and a list of Child objects.
 * Allows a user to navigate to other activities.
 *
 * @author Titanium
 */
public class MenuActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);

    MaterialButton help = findViewById(R.id.MaterialButton_menu_help);
    help.setOnClickListener(view -> startActivity(HelpActivity.makeIntent(this)));

    MaterialButton timer = findViewById(R.id.MaterialButton_menu_timer);
    timer.setOnClickListener(view -> startActivity(TimerActivity.makeIntent(this)));

    MaterialButton coinFlip = findViewById(R.id.MaterialButton_menu_coin_flip);
    coinFlip.setOnClickListener(view -> startActivity(CoinFlipActivity.makeIntent(this)));

    MaterialButton addChild = findViewById(R.id.MaterialButton_menu_add_child);
    addChild.setOnClickListener(view -> startActivity(ChildActivity.makeIntent(
        this, getString(R.string.title_menu_child_add), null)));

    MaterialButton tasks = findViewById(R.id.MaterialButton_menu_tasks);
    tasks.setOnClickListener(view -> startActivity(TasksActivity.makeIntent(this)));

    MaterialButton breathe = findViewById(R.id.MaterialButton_menu_breath);
    breathe.setOnClickListener(view -> startActivity(TakeBreathActivity.makeIntent(this)));

  }

  @Override
  protected void onResume() {
    super.onResume();
    setWelcomeMessage();
    displayChildren();
  }

  private void setWelcomeMessage() {
    TextView welcomeMessage = findViewById(R.id.TextView_menu_welcome_message);
    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

    if (currentHour <= 12) {
      welcomeMessage.setText(getString(R.string.subtitle_menu_welcome_morning));
    } else if (currentHour <= 16) {
      welcomeMessage.setText(getString(R.string.subtitle_menu_welcome_afternoon));
    } else {
      welcomeMessage.setText(getString(R.string.subtitle_menu_welcome_evening));
    }
  }

  private void displayChildren() {
    ChildManager childManager = ChildManager.getInstance(this);
    ArrayList<Child> children = childManager.getChildren();

    TextView emptyStateMessage = findViewById(R.id.TextView_menu_empty_state);
    emptyStateMessage.setVisibility(children.size() == 0 ? View.VISIBLE : View.INVISIBLE);

    ChildAdapter childAdapter = new ChildAdapter(this, children);

    ListView childrenListView = findViewById(R.id.ListView_menu_children);
    childrenListView.setAdapter(childAdapter);
    childrenListView.setOnItemClickListener((adapterView, view, i, l) ->
        startActivity(ChildActivity.makeIntent(
            this,
            getString(R.string.title_menu_child_manage),
            children.get(i).getUniqueID())));
  }
}
