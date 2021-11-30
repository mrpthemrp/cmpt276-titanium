package ca.cmpt276.titanium.ui.coinflip;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipManager;

/**
 * Displays coin flip history from newest to oldest.
 *
 * @author Titanium
 */
public class CoinFlipHistoryActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coin_flip_history);

    setSupportActionBar(findViewById(R.id.ToolBar_coin_flip_history));
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    populateListView();
  }

  private void populateListView() {
    CoinFlipManager coinFlipManager = CoinFlipManager.getInstance(this);

    if (coinFlipManager.getCoinFlipHistory().size() == 0) {
      TextView emptyStateMessage = findViewById(R.id.TextView_coin_flip_history_empty_state);
      emptyStateMessage.setVisibility(View.VISIBLE);
    }

    ArrayList<CoinFlip> coinFlipHistoryCopy = new ArrayList<>(coinFlipManager.getCoinFlipHistory());
    Collections.reverse(coinFlipHistoryCopy);
    CoinFlipAdapter coinFlipAdapter = new CoinFlipAdapter(this, coinFlipHistoryCopy);

    ListView coinFlipHistoryListView = findViewById(R.id.ListView_coin_flip_history);
    coinFlipHistoryListView.setAdapter(coinFlipAdapter);
  }
}
