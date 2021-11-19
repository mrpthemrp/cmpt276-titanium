package ca.cmpt276.titanium.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Collections;

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

        this.coinFlipHistory = CoinFlipHistory.getInstance(this);
        populateListView();

        if (coinFlipHistory.getCoinFlipHistory().size() == 0) {
            ConstraintLayout emptyStateLayout = findViewById(R.id.emptyStateLayout);
            emptyStateLayout.setVisibility(View.VISIBLE);

            ListView listView = findViewById(R.id.coinFlipHistoryList);
            listView.setVisibility(View.GONE);
        }
    }

    private void populateListView() {
        Collections.reverse(coinFlipHistory.getCoinFlipHistory());
        CoinFlipHistoryAdapter adapter = new CoinFlipHistoryAdapter(this, coinFlipHistory.getCoinFlipHistory());
        ListView list = findViewById(R.id.coinFlipHistoryList);
        list.setAdapter(adapter);
    }
}