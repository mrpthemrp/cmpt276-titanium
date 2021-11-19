package ca.cmpt276.titanium.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Collections;
import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.CoinFlipHistory;

/**
 * This represents the coin flip history activity.
 * Shows a list of the history from latest to oldest.
 */
public class CoinFlipHistoryActivity extends AppCompatActivity {
    private CoinFlipHistory coinFlipHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_history);
        setTitle(R.string.coinFlipHistoryTitle);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.coinFlipHistory = CoinFlipHistory.getInstance(this);
        populateListView();

        if (coinFlipHistory.getCoinFlipHistory().size() == 0) {
            ConstraintLayout emptyStateLayout = findViewById(R.id.emptyStateLayout);
            emptyStateLayout.setVisibility(View.VISIBLE);

            ListView listView = findViewById(R.id.coinFlipHistoryList);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void populateListView() {
        Collections.reverse(coinFlipHistory.getCoinFlipHistory());
        CoinFlipHistoryListAdapter adapter = new CoinFlipHistoryListAdapter(this, coinFlipHistory.getCoinFlipHistory());
        ListView list = findViewById(R.id.coinFlipHistoryList);
        list.setAdapter(adapter);
    }
}