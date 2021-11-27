package ca.cmpt276.titanium.ui.coinflip;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipManager;

/**
 * This represents the coin flip history activity.
 * Shows a list of the history from latest to oldest.
 */
public class CoinFlipHistoryActivity extends AppCompatActivity {
  private CoinFlipManager coinFlipManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coin_flip_history);
    setTitle(R.string.title_coin_flip_history);

    Toolbar toolbar = findViewById(R.id.ToolBar_coin_flip_history);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    this.coinFlipManager = CoinFlipManager.getInstance(this);
    populateListView();

    if (coinFlipManager.getCoinFlipHistory().size() == 0) {
      TextView emptyStateMessage =
          findViewById(R.id.TextView_coin_flip_history_empty_state_message);
      emptyStateMessage.setVisibility(View.VISIBLE);
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
    ArrayList<CoinFlip> coinFlipHistory = coinFlipManager.getCoinFlipHistory();
    ArrayList<CoinFlip> coinFlipHistoryCopy = new ArrayList<>(coinFlipHistory);
    Collections.reverse(coinFlipHistoryCopy);
    CoinFlipAdapter coinFlipAdapter = new CoinFlipAdapter(this, coinFlipHistoryCopy);

    ListView list = findViewById(R.id.ListView_coin_flip_history);
    list.setAdapter(coinFlipAdapter);
  }
}
