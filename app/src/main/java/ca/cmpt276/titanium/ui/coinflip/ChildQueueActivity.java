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

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.ui.ChildAdapter;

/**
 * Displays the children in the coin flip queue.
 *
 * @author Titanium
 */
public class ChildQueueActivity extends AppCompatActivity {
  private ChildManager childManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coin_flip_child_queue);

    setSupportActionBar(findViewById(R.id.ToolBar_child_queue));
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    this.childManager = ChildManager.getInstance(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_coin_flip_child_queue, menu);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    displayCurrentChild();
    displayQueue();
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

  private void displayCurrentChild() {
    ImageView currentChildPortrait =
        findViewById(R.id.ImageView_child_queue_current_child_portrait);
    TextView currentChildName = findViewById(R.id.TextView_child_queue_current_child_name);

    if (childManager.getCoinFlipQueue().size() > 0 && childManager.isChildIsChoosing()) {
      Child currentChild = childManager.getChoosingChild();
      currentChildPortrait.setImageDrawable(currentChild.getPortrait(getResources()));
      currentChildName.setText(currentChild.getName());
    } else if (childManager.getCoinFlipQueue().size() > 0 && !childManager.isChildIsChoosing()) {
      currentChildPortrait.setImageResource(R.drawable.ic_default_portrait_green);
      currentChildName.setText(R.string.subtitle_child_turn_nobody);
    } else {
      currentChildPortrait.setImageResource(R.drawable.ic_default_portrait_green);
      currentChildName.setText(R.string.empty_state_coin_flip_no_children);
    }
  }

  private void displayQueue() {
    TextView emptyStateMessage = findViewById(R.id.TextView_child_queue_empty_state);
    emptyStateMessage.setVisibility(
        childManager.getChildren().size() == 0
            ? View.VISIBLE
            : View.INVISIBLE);

    ArrayList<UUID> coinFlipQueue = new ArrayList<>(childManager.getCoinFlipQueue());
    ArrayList<Child> coinFlipQueueChildren = new ArrayList<>();
    coinFlipQueue.forEach(uniqueID -> coinFlipQueueChildren.add(childManager.getChild(uniqueID)));
    ChildAdapter childAdapter = new ChildAdapter(this, coinFlipQueueChildren);

    ListView childrenListView = findViewById(R.id.ListView_child_queue);
    childrenListView.setAdapter(childAdapter);
  }
}
