package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Coin;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipHistory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

public class CoinActivity extends AppCompatActivity {
    private static final Coin DEFAULT_COIN_CHOSEN = Coin.HEADS;

    private Button historyButton;

    private String sideChosenFormat;
    private TextView sideChosenDisplay;
    private Button headsButton;
    private Button tailsButton;
    private Coin coinChosen = DEFAULT_COIN_CHOSEN;

    private Button flipButton;
    private ImageView coin;
    private TextView coinResult;
    private static final String HEADS = "HEADS";
    private static final String TAILS = "TAILS";
    private final Children children = Children.getInstance(this);

    private CoinFlipHistory coinFlipHistory;

    private final Runnable result = () -> coinResult.setVisibility(View.VISIBLE);
    private final Runnable displayHeads = () -> coin.setImageResource(R.drawable.ic_coin_heads);
    private final Runnable displayTails = () -> coin.setImageResource(R.drawable.ic_coin_tails);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
        setupTitle();

        coinFlipHistory = new CoinFlipHistory(getApplicationContext());

        historyButton = findViewById(R.id.viewHistoryButton);
        historyButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(this, CoinFlipHistoryActivity.class);
            startActivity(intent);
        });

        setSideChosenText();
        headsButton = findViewById(R.id.headsButton);
        headsButton.setOnClickListener((View view) -> {
            coinChosen = Coin.HEADS;
            setSideChosenText();
        });
        tailsButton = findViewById(R.id.tailsButton);
        tailsButton.setOnClickListener((View view) -> {
            coinChosen = Coin.TAILS;
            setSideChosenText();
        });

        flipButton = findViewById(R.id.flipButton);
        coin = findViewById(R.id.coinBlank);
        coinResult = findViewById(R.id.coinFlipResult);

        flipButtonClick();
    }

    private void setSideChosenText() {
        sideChosenFormat = getString(R.string.coinSideChosen, coinChosen.toString());
        sideChosenDisplay = findViewById(R.id.sideChosenText);
        sideChosenDisplay.setText(sideChosenFormat);
    }

    private void setupTitle() {
        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.menuFlipCoinBtn);
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, CoinActivity.class);
    }

    private void flipTheCoin(){
        coin.setImageResource(R.drawable.ic_coin_blank);
        Random rand = new Random();
        int coinSide = rand.nextInt(2);
        Animation animation = AnimationUtils.loadAnimation(CoinActivity.this, R.anim.flipanim);
        coin.startAnimation(animation);
        coinResult.setVisibility(View.INVISIBLE);

        Coin coinSideLandedOn;
        // heads == 0
        if (coinSide == 0){
            coinResult.setText(HEADS);
            coin.postDelayed(displayHeads, 1600);
            coinSideLandedOn = Coin.HEADS;
        }

        // tails == 1
        else {
            coinResult.setText(TAILS);
            coin.postDelayed(displayTails, 1600);
            coinSideLandedOn = Coin.TAILS;
        }
        coinResult.postDelayed(result, 1600);

        // If there are no children configured, we don't need to save any info (?)
        if (!children.getChildren().isEmpty()) {
            LocalDateTime timeOfFlip = LocalDateTime.now();

            Child childOfNextTurn = getChildOfNextTurn();
            // TODO: Remove Coin.TAILS once sideThatChildPicks can be retrieved
            // childOfNextTurn will always return null until adding child is implemented
            CoinFlip coinFlip = new CoinFlip(childOfNextTurn, coinChosen, timeOfFlip, coinSideLandedOn);

            coinFlipHistory.addCoinFlipToHistory(coinFlip);
        }
    }

    private void flipButtonClick(){
        flipButton.setOnClickListener(view -> flipTheCoin());
    }

    private Child getChildOfNextTurn() {
        Child childToPickLastTurn = getChildOfLastTurn();
        Child childOfNextTurn = null;
        ArrayList<Child> childrenArray = children.getChildren();

        for (int i = 0; i < childrenArray.size(); i++) {
            if (childrenArray.get(i) == childToPickLastTurn) {
                // wrap around to next item in the array
                childOfNextTurn = childrenArray.get((i + 1) % childrenArray.size());
            }
        }
        return childOfNextTurn;
    }

    private Child getChildOfLastTurn() {
        int sizeOfCoinFlips = CoinFlipHistory.getCoinFlipHistory().size();
        if (sizeOfCoinFlips != 0) {
            Child childToPickLastTurn = CoinFlipHistory.getCoinFlipHistory().get(sizeOfCoinFlips - 1).getChildWhoPicksSide();
            return childToPickLastTurn;
        }

        return null;
    }
}