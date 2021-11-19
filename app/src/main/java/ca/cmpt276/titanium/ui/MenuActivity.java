package ca.cmpt276.titanium.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        this.children = Children.getInstance(this);

        if (this.children.getChildren().size() > 0) {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        }

        Button addChildButton = findViewById(R.id.menuGoToAddChild);
        addChildButton.setOnClickListener(view -> startActivity(AddChildActivity.makeIntent(this)));

        Button coinFlipButton = findViewById(R.id.menuGoToFlipCoin);
        coinFlipButton.setOnClickListener(view -> startActivity(CoinActivity.makeIntent(this)));

        Button timerButton = findViewById(R.id.menuGoToTimer);
        timerButton.setOnClickListener(view -> startActivity(TimerActivity.makeIntent(this)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayChildren();
    }

    private void displayChildren() {
        if (children.getChildren().size() == 0) {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.INVISIBLE);

            ListView childrenListView = (ListView) findViewById(R.id.childrenList);
            ChildrenListAdapter adapter = new ChildrenListAdapter(this, children.getChildren());
            childrenListView.setAdapter(adapter);
            childrenListView.setClickable(true);

            childrenListView.setOnItemClickListener((parent, view, position, id) -> {
                UUID childUUID = children.getChildren().get(position).getUniqueID();
                Intent viewChildIntent = ViewChildActivity.makeIntent(this, childUUID);

                startActivity(viewChildIntent);
            });
        }
    }
}
