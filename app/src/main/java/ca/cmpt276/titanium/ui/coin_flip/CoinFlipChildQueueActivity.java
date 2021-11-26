package ca.cmpt276.titanium.ui.coin_flip;

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
import androidx.core.graphics.drawable.RoundedBitmapDrawable;

import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.CoinFlipChildQueue;
import ca.cmpt276.titanium.model.CoinFlipHistory;

/**
 * This activity represents the coin flip queue.
 */
public class CoinFlipChildQueueActivity extends AppCompatActivity {
    private ChildManager childManager;
    private CoinFlipChildQueue coinFlipChildQueue;
    private CoinFlipHistory coinFlipHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_child_queue);
        setTitle(R.string.toolbar_coin_flip_history_view_queue);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.ToolBar_coin_flip_child_queue);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        childManager = ChildManager.getInstance(this);
        coinFlipChildQueue = CoinFlipChildQueue.getInstance(this);
        coinFlipHistory = CoinFlipHistory.getInstance(this);

        displayQueue();
        updateGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coin_flip_child_queue, menu);
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
        } else if (item.getItemId() == R.id.menu_item_coin_flip_child_queue_coin_flip_change_child) {
            startActivity(new Intent(this, CoinFlipChangeChildActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void displayQueue() {
        if (childManager.getChildren().size() == 0) {
            findViewById(R.id.TextView_coin_flip_child_queue_empty_state_message).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.TextView_coin_flip_child_queue_empty_state_message).setVisibility(View.INVISIBLE);
        }

        ListView childrenListView = findViewById(R.id.ListView_coin_flip_child_queue);
        CoinFlipChildQueueAdapter adapter = new CoinFlipChildQueueAdapter(this, coinFlipChildQueue.getChildrenQueue());
        childrenListView.setAdapter(adapter);
    }

    private void updateGUI() {
        ImageView currentChildIcon = findViewById(R.id.ImageView_coin_flip_child_queue_current_child_portrait);
        TextView currentChildTurnName = findViewById(R.id.TextView_coin_flip_child_queue_current_child_name);

        Child currentChildTurn = coinFlipChildQueue.getChild(coinFlipHistory.getNextPickerUniqueID());
        if (currentChildTurn != null) {
            currentChildTurnName.setText(currentChildTurn.getName());
            RoundedBitmapDrawable drawable = childManager.getChild(coinFlipHistory.getNextPickerUniqueID()).getPortrait(getResources());
            currentChildIcon.setImageDrawable(drawable);
        } else {
            currentChildTurnName.setText(R.string.coin_flip_empty_state_message_no_children);
            currentChildIcon.setImageResource(R.drawable.ic_default_portrait_green);
        }
    }
}