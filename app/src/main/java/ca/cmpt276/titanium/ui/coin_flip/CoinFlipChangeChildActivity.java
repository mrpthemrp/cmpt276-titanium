package ca.cmpt276.titanium.ui.coin_flip;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.CoinFlipChildQueue;
import ca.cmpt276.titanium.model.CoinFlipHistory;

/**
 * This activity represents the children list.
 * Allows the user to change the current child turn.
 */
public class CoinFlipChangeChildActivity extends AppCompatActivity {
    private ChildManager childManager;
    private CoinFlipChildQueue coinFlipChildQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_change_child);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.ToolBar_coin_flip_change_child);
        setSupportActionBar(myToolbar);

        setTitle(R.string.title_change_child);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.childManager = ChildManager.getInstance(this);
        this.coinFlipChildQueue = CoinFlipChildQueue.getInstance(this);

        displayChildrenList();
    }

    private void displayChildrenList() {
        if (childManager.getChildren().size() == 0) {
            findViewById(R.id.TextView_coin_flip_change_child_empty_state_message).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.TextView_coin_flip_change_child_empty_state_message).setVisibility(View.INVISIBLE);
        }

        ArrayList<Child> childrenQueueCopy = new ArrayList<>(coinFlipChildQueue.getChildrenQueue());
        ListView changeChildListView = findViewById(R.id.ListView_coin_flip_change_child);
        CoinFlipChangeChildAdapter adapter = new CoinFlipChangeChildAdapter(this, childrenQueueCopy);
        changeChildListView.setAdapter(adapter);
        changeChildListView.setClickable(true);

        changeChildListView.setOnItemClickListener((parent, view, position, id) -> {
            UUID childUUID = childrenQueueCopy.get(position).getUniqueID();
            coinFlipChildQueue.moveChildPositionToFront(childUUID);
            CoinFlipHistory.setNextPickerUniqueID(childUUID);
            finish();
        });
    }
}