package ca.cmpt276.titanium.ui.coinflip;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
 * This activity represents the children list.
 * Allows the user to change the current child turn.
 */
public class ChangeChildActivity extends AppCompatActivity {
  private ChildManager childManager;
  private ChildQueueManager childQueueManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coin_flip_change_child);
    setTitle(R.string.title_change_child);

    Toolbar toolbar = findViewById(R.id.ToolBar_change_child);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    this.childManager = ChildManager.getInstance(this);
    this.childQueueManager = ChildQueueManager.getInstance(this);

    displayChildrenList();
  }

  private void displayChildrenList() {
    TextView emptyStateMessage =
        findViewById(R.id.TextView_change_child_empty_state_message);

    emptyStateMessage.setVisibility(
        childManager.getChildren().size() == 0
            ? View.VISIBLE
            : View.INVISIBLE);

    ArrayList<UUID> childQueue = new ArrayList<>(childQueueManager.getChildQueue());
    ArrayList<Child> childQueueChildren = new ArrayList<>();
    childQueue.forEach(uniqueID -> childQueueChildren.add(childManager.getChild(uniqueID)));
    ChildAdapter childAdapter = new ChildAdapter(this, childQueueChildren);

    ListView changeChildListView = findViewById(R.id.ListView_change_child);
    changeChildListView.setAdapter(childAdapter);
    changeChildListView.setClickable(true);

    changeChildListView.setOnItemClickListener((parent, view, position, id) -> {
      UUID childUUID = childQueue.get(position);
      childQueueManager.moveToFront(childUUID);
      CoinFlipManager.setNextPickerUniqueID(childUUID);
      finish();
    });
  }
}
