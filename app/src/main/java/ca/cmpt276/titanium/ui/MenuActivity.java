package ca.cmpt276.titanium.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;

/**
 * This activity represents the main menu.
 * Shows children and buttons to the timer and coin flip.
 */
public class MenuActivity extends AppCompatActivity {
    private Children children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.children = Children.getInstance(this);

        Button addChildButton = findViewById(R.id.menuGoToAddChild);
        addChildButton.setOnClickListener(view -> startActivity(ChildAddActivity.makeIntent(this)));

        Button coinFlipButton = findViewById(R.id.menuGoToFlipCoin);
        coinFlipButton.setOnClickListener(view -> startActivity(CoinFlipActivity.makeIntent(this)));

        Button timerButton = findViewById(R.id.menuGoToTimer);
        timerButton.setOnClickListener(view -> startActivity(TimerActivity.makeIntent(this)));

        Button whoseTurnButton = findViewById(R.id.menuWhoseTurn);
        whoseTurnButton.setOnClickListener(view -> startActivity(TasksActivity.makeIntent(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayChildren();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.optionsHelp) {
            startActivity(HelpActivity.makeIntent(this));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void displayChildren() {
        if (children.getChildren().size() == 0) {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.INVISIBLE);
        }

        ListView childrenListView = (ListView) findViewById(R.id.childrenList);
        MenuChildrenListAdapter adapter = new MenuChildrenListAdapter(this, children.getChildren());
        childrenListView.setAdapter(adapter);
        childrenListView.setClickable(true);

        childrenListView.setOnItemClickListener((parent, view, position, id) -> {
            UUID childUUID = children.getChildren().get(position).getUniqueID();
            Intent viewChildIntent = ChildViewActivity.makeIntent(this, childUUID);

            startActivity(viewChildIntent);
        });
    }
}
