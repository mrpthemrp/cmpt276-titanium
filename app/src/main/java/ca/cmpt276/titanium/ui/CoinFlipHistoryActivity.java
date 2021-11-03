package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
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

        if (CoinFlipHistory.getCoinFlipHistory().size() == 0) {
            ConstraintLayout emptyStateLayout = (ConstraintLayout) findViewById(R.id.emptyStateLayout);
            emptyStateLayout.setVisibility(View.VISIBLE);

            ListView listView = (ListView) findViewById(R.id.coinFlipHistoryList);
            listView.setVisibility(View.GONE);
        }
    }

    private void setupTitle() {
        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.coinFlipHistoryTitle);
    }

    private void populateListView() {
        ArrayList<CoinFlip> coinFlipHistory = CoinFlipHistory.getCoinFlipHistory();
        Collections.reverse(coinFlipHistory);
        CoinFlipHistoryAdapter adapter = new CoinFlipHistoryAdapter(this, coinFlipHistory);
        ListView list = (ListView) findViewById(R.id.coinFlipHistoryList);
        list.setAdapter(adapter);
    }
}