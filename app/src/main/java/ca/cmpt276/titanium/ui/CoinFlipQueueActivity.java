package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;

public class CoinFlipQueueActivity extends AppCompatActivity {
    private Children children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_queue);
        setTitle(R.string.viewQueue);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.children = Children.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coinflipqueue, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayQueue();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.changeChildButton) {
            startActivity(new Intent(this, ChangeChildActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void displayQueue() {
        if (children.getChildren().size() == 0) {
            findViewById(R.id.childrenQueueListText).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.childrenQueueListText).setVisibility(View.INVISIBLE);
        }

        ListView childrenListView = (ListView) findViewById(R.id.childrenQueueList);
        CoinFlipQueueAdapter adapter = new CoinFlipQueueAdapter(this, children.getChildren());
        childrenListView.setAdapter(adapter);
    }
}