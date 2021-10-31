package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipHistory;

public class CoinFlipHistoryActivity extends AppCompatActivity {
    private CoinFlipHistory coinFlipHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_history);
        setupTitle();

        populateListView();
    }

    private void setupTitle() {
        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.coinFlipHistoryTitle);
    }

    private void populateListView() {
        ArrayList<CoinFlip> coinFlipHistory = CoinFlipHistory.getCoinFlipHistory();
        CoinFlipHistoryAdapter adapter = new CoinFlipHistoryAdapter(this, coinFlipHistory);
        ListView list = (ListView) findViewById(R.id.coinFlipHistoryList);
        list.setAdapter(adapter);
    }
}