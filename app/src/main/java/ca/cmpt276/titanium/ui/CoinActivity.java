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
    private final Children children = Children.getInstance(this);

    private Button historyButton;

    private String childNameFormat;
    private TextView childNameDisplay;
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

    private CoinFlipHistory coinFlipHistory;

    private final Runnable result = () -> coinResult.setVisibility(View.VISIBLE);
    private final Runnable displayHeads = () -> coin.setImageResource(R.drawable.ic_coin_heads);
    private final Runnable displayTails = () -> coin.setImageResource(R.drawable.ic_coin_tails);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
        setupTitle();

        children.loadSavedData();

        coinFlipHistory = new CoinFlipHistory(getApplicationContext());

        historyButton = findViewById(R.id.viewHistoryButton);
        historyButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(this, CoinFlipHistoryActivity.class);
            startActivity(intent);
        });

        // doesn't matter which child chooses for first pick, so just start from beginning of children array
        if (CoinFlipHistory.getCoinFlipHistory().isEmpty()) {
            setChildNameTextFirstFlip();
        } else {
            setChildNameText();
        }

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

    private void setChildNameTextFirstFlip() {
        childNameFormat = getString(R.string.childTurn, children.getChildren().get(0).getName());
        childNameDisplay = findViewById((R.id.childsTurnText));
        childNameDisplay.setText(childNameFormat);
    }

    private void setChildNameText() {
        childNameFormat = getString(R.string.childTurn, getChildOfNextTurn().getName());
        childNameDisplay = findViewById((R.id.childsTurnText));
        childNameDisplay.setText(childNameFormat);
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
        if (coinSide == 0) {
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
            CoinFlip coinFlip;
            LocalDateTime timeOfFlip = LocalDateTime.now();

            Child childOfNextTurn = getChildOfNextTurn();
            if (CoinFlipHistory.getCoinFlipHistory().isEmpty()) {
                coinFlip = new CoinFlip(children.getChildren().get(0), coinChosen, timeOfFlip, coinSideLandedOn);
            } else {
                coinFlip = new CoinFlip(childOfNextTurn, coinChosen, timeOfFlip, coinSideLandedOn);
            }

            coinFlipHistory.addCoinFlipToHistory(coinFlip);
            setSideChosenText();
        }
    }

    private void flipButtonClick(){
        flipButton.setOnClickListener(view -> flipTheCoin());
    }

    private Child getChildOfNextTurn() {
        ArrayList<Child> childrenArray = children.getChildren();

        Child childToPickLastTurn = getChildOfLastTurn();
        if (childToPickLastTurn == null && !childrenArray.isEmpty()) {
            return childrenArray.get(0); // if no coin flips exist
        }

        Child childOfNextTurn = null;
        for (int i = 0; i < childrenArray.size(); i++) {
            if (childrenArray.get(i).getUniqueId().toString().equals(childToPickLastTurn.getUniqueId().toString())) { // TODO: use uuids to compare children
                childOfNextTurn = childrenArray.get((i + 1) % childrenArray.size());
            }
        }
        return childOfNextTurn;
    }

    private Child getChildOfLastTurn() {
        int sizeOfHistory = CoinFlipHistory.getCoinFlipHistory().size();
        if (sizeOfHistory != 0) {
            Child childToPickLastTurn = CoinFlipHistory.getCoinFlipHistory().get(sizeOfHistory - 1).getChildWhoPicksSide();
            return childToPickLastTurn;
        }

        return null;
    }
}