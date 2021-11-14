package ca.cmpt276.titanium.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collections;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipHistory;

/**
 * This represents the coin flip history activity.
 * Shows a list of the history from latest to oldest.
 */
public class CoinFlipHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_history);
        setTitle(R.string.coinFlipHistoryTitle);

        populateListView();

        if (CoinFlipHistory.getCoinFlipHistory().size() == 0) {
            ConstraintLayout emptyStateLayout = findViewById(R.id.emptyStateLayout);
            emptyStateLayout.setVisibility(View.VISIBLE);

            ListView listView = findViewById(R.id.coinFlipHistoryList);
            listView.setVisibility(View.GONE);
        }
    }

    private void populateListView() {
        ArrayList<CoinFlip> coinFlipHistory = CoinFlipHistory.getCoinFlipHistory();
        Collections.reverse(coinFlipHistory);
        CoinFlipHistoryAdapter adapter = new CoinFlipHistoryAdapter(this, coinFlipHistory);
        ListView list = findViewById(R.id.coinFlipHistoryList);
        list.setAdapter(adapter);
    }
}