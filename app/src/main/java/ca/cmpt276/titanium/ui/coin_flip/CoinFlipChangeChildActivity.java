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
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.ChildrenQueue;
import ca.cmpt276.titanium.model.CoinFlipHistory;

/**
 * This activity represents the children list.
 * Allows the user to change the current child turn.
 */
public class CoinFlipChangeChildActivity extends AppCompatActivity {
    private Children children;
    private ChildrenQueue childrenQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_change_child);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);

        setTitle(R.string.title_change_child);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.children = Children.getInstance(this);
        this.childrenQueue = ChildrenQueue.getInstance(this);

        displayChildrenList();
    }

    private void displayChildrenList() {
        if (children.getChildren().size() == 0) {
            findViewById(R.id.changeChildText).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.changeChildText).setVisibility(View.INVISIBLE);
        }

        ArrayList<Child> childrenQueueCopy = new ArrayList<>(childrenQueue.getChildrenQueue());
        ListView changeChildListView = findViewById(R.id.changeChildList);
        CoinFlipChangeChildAdapter adapter = new CoinFlipChangeChildAdapter(this, childrenQueueCopy);
        changeChildListView.setAdapter(adapter);
        changeChildListView.setClickable(true);

        changeChildListView.setOnItemClickListener((parent, view, position, id) -> {
            UUID childUUID = childrenQueueCopy.get(position).getUniqueID();
            childrenQueue.moveChildPositionToFront(childUUID);
            CoinFlipHistory.setNextPickerUniqueID(childUUID);
            finish();
        });
    }
}