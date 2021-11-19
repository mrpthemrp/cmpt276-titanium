package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Coin;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipHistory;

/**
 * This activity represents the coin flip activity.
 * Allows the user to choose heads or tails, and shows the results of the flip.
 */
public class CoinActivity extends AppCompatActivity {
    private static final int COIN_FLIP_DELAY = 1600;
    private static final Coin DEFAULT_COIN = Coin.HEADS;

    private Coin coinChosen;
    private CoinFlipHistory coinFlipHistory;

    public static Intent makeIntent(Context context) {
        return new Intent(context, CoinActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
        setTitle(R.string.menuFlipCoinBtn);

        coinFlipHistory = CoinFlipHistory.getInstance(this);

        setupButtons();
        updateGUI(DEFAULT_COIN);
    }

    private void setupButtons() {
        Button historyButton = findViewById(R.id.viewHistoryButton);
        Button headsButton = findViewById(R.id.headsButton);
        Button tailsButton = findViewById(R.id.tailsButton);
        Button flipButton = findViewById(R.id.flipButton);

        historyButton.setOnClickListener((View view) -> startActivity(new Intent(this, CoinFlipHistoryActivity.class)));
        headsButton.setOnClickListener((View view) -> updateGUI(Coin.HEADS));
        tailsButton.setOnClickListener((View view) -> updateGUI(Coin.TAILS));
        flipButton.setOnClickListener(view -> animateCoinFlip());
    }

    private void updateGUI(Coin coinChosen) {
        TextView childNameDisplay = findViewById(R.id.childsTurnText);
        TextView sideChosenDisplay = findViewById(R.id.sideChosenText);
        Button headsButton = findViewById(R.id.headsButton);
        Button tailsButton = findViewById(R.id.tailsButton);

        UUID nextPickerUniqueID = coinFlipHistory.getNextPickerUniqueID();

        if (nextPickerUniqueID != null) {
            Children children = Children.getInstance(this);
            childNameDisplay.setText(getString(R.string.childTurn, children.getChild(nextPickerUniqueID).getName()));
            sideChosenDisplay.setText(getString(R.string.coinSideChosen, coinChosen.toString()));
            this.coinChosen = coinChosen;
        } else {
            childNameDisplay.setVisibility(View.GONE);
            sideChosenDisplay.setVisibility(View.GONE);
            headsButton.setVisibility(View.GONE);
            tailsButton.setVisibility(View.GONE);
        }
    }

    private void animateCoinFlip() {
        ImageView coin = findViewById(R.id.coinBlank);
        coin.setImageResource(R.drawable.ic_coin_blank);

        TextView coinResultMessage = findViewById(R.id.coinFlipResult);
        coinResultMessage.setVisibility(View.INVISIBLE);

        Animation coinFlipAnimation = AnimationUtils.loadAnimation(CoinActivity.this, R.anim.flip_coin);
        coin.startAnimation(coinFlipAnimation);

        MediaPlayer coinSound = MediaPlayer.create(CoinActivity.this, R.raw.coinflip); // Source: https://www.youtube.com/watch?v=1QxX9ruPUXM
        coinSound.start();

        Coin coinResult = CoinFlip.flipCoin();

        if (coinResult.equals(Coin.HEADS)) {
            coinResultMessage.setText(R.string.heads_text);
            coin.postDelayed(() -> coin.setImageResource(R.drawable.ic_coin_heads), COIN_FLIP_DELAY);
        } else {
            coinResultMessage.setText(R.string.tails_text);
            coin.postDelayed(() -> coin.setImageResource(R.drawable.ic_coin_tails), COIN_FLIP_DELAY);
        }

        coinResultMessage.postDelayed(() -> coinResultMessage.setVisibility(View.VISIBLE), COIN_FLIP_DELAY);

        if (coinFlipHistory.getNextPickerUniqueID() != null) {
            coinFlipHistory.addCoinFlip(coinChosen, coinResult);
            new Handler(Looper.getMainLooper()).postDelayed(() -> updateGUI(coinChosen), COIN_FLIP_DELAY);
        }
    }
}
