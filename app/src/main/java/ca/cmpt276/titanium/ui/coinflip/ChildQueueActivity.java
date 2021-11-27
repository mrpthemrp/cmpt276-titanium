package ca.cmpt276.titanium.ui.coinflip;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.ChildQueueManager;
import ca.cmpt276.titanium.model.CoinFlipManager;
import ca.cmpt276.titanium.ui.ChildAdapter;

/**
 * This activity represents the coin flip queue.
 */
public class ChildQueueActivity extends AppCompatActivity {
  private ChildManager childManager;
  private ChildQueueManager childQueueManager;
  private CoinFlipManager coinFlipManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coin_flip_child_queue);
    setTitle(R.string.toolbar_coin_flip_history_view_queue);

    Toolbar myToolbar = (Toolbar) findViewById(R.id.ToolBar_child_queue);
    setSupportActionBar(myToolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    childManager = ChildManager.getInstance(this);
    childQueueManager = ChildQueueManager.getInstance(this);
    coinFlipManager = CoinFlipManager.getInstance(this);

    displayQueue();
    updateGUI();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_child_queue, menu);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    displayQueue();
    updateGUI();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_item_child_queue_change_child) {
      startActivity(new Intent(this, ChangeChildActivity.class));
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  private void displayQueue() {
    if (childManager.getChildren().size() == 0) {
      findViewById(R.id.TextView_child_queue_empty_state_message).setVisibility(View.VISIBLE);
    } else {
      findViewById(R.id.TextView_child_queue_empty_state_message).setVisibility(View.INVISIBLE);
    }

    ArrayList<Child> childQueueChildren = new ArrayList<>();
    childQueueManager.getChildQueue().forEach(uniqueID ->
        childQueueChildren.add(childManager.getChild(uniqueID)));
    ChildAdapter childAdapter = new ChildAdapter(this, childQueueChildren);

    ListView childrenListView = findViewById(R.id.ListView_child_queue);
    childrenListView.setAdapter(childAdapter);
  }

  private void updateGUI() {
    ImageView currentChildIcon = findViewById(R.id.ImageView_child_queue_current_child_portrait);
    TextView currentChildTurnName = findViewById(R.id.TextView_child_queue_current_child_name);

    UUID nextPickerUniqueID = coinFlipManager.getNextPickerUniqueID();
    Child currentChild = childManager.getChild(nextPickerUniqueID);

    if (currentChild != null) {
      currentChildTurnName.setText(currentChild.getName());
      currentChildIcon.setImageDrawable(currentChild.getPortrait(getResources()));
    } else {
      currentChildTurnName.setText(R.string.coin_flip_empty_state_message_no_children);
      currentChildIcon.setImageResource(R.drawable.ic_default_portrait_green);
    }
  }
}
