package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipHistory;

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

        if (this.children.getChildren().size() > 0) {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        }
        children.loadSavedData();

        FloatingActionButton addChildButton = findViewById(R.id.menuFAB);
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

        if (this.children.getChildren().size() > 0) {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.menuTextChildrenList).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        children.saveData();
    }

    private void displayChildren() {
        ArrayList<Child> childList = children.getChildren();
        Collections.reverse(childList);
        ListView childrenList = (ListView) findViewById(R.id.childrenList);

        ChildrenListAdapter adapter = new ChildrenListAdapter(this, childList);
        childrenList.setAdapter(adapter);
        childrenList.setClickable(true);
        Context context = this;

        childrenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UUID childUUID = childList.get(position).getUniqueID();
                Intent viewChildIntent = ViewChildActivity.makeIntent(context, childUUID);

                startActivity(viewChildIntent);
            }
        });
    }
}
