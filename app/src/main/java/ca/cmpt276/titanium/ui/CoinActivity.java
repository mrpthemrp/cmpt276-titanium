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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Coin;
import ca.cmpt276.titanium.model.CoinFlip;
import ca.cmpt276.titanium.model.CoinFlipHistory;

/**
 * This activity represents the coin flip activity.
 * Allows the user to choose heads or tails, and shows the results of the flip.
 */
public class CoinActivity extends AppCompatActivity {
    public static final int FIRST_CHILD_INDEX = 0;
    private static final Coin DEFAULT_COIN_CHOSEN = Coin.HEADS;
    private static final String HEADS = "HEADS";
    private static final String TAILS = "TAILS";
    private static final int COIN_FLIP_DELAY = 1600;
    private final Children children = Children.getInstance(this);
    private String childNameFormat;
    private TextView childNameDisplay;
    private TextView sideChosenDisplay;
    private Button headsButton;
    private Button tailsButton;
    private Coin coinChosen = DEFAULT_COIN_CHOSEN;
    private ImageView coin;
    private final Runnable displayHeads = () -> coin.setImageResource(R.drawable.ic_coin_heads);
    private final Runnable displayTails = () -> coin.setImageResource(R.drawable.ic_coin_tails);
    private TextView coinResult;
    private final Runnable result = () -> coinResult.setVisibility(View.VISIBLE);
    private CoinFlipHistory coinFlipHistory;
    /*
    Sound from https://www.youtube.com/watch?v=1QxX9ruPUXM
     */
    private MediaPlayer coinSound;

    public static Intent makeIntent(Context c) {
        return new Intent(c, CoinActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
        setupTitle();

        coinSound = MediaPlayer.create(CoinActivity.this, R.raw.coinflip);

        coinFlipHistory = new CoinFlipHistory(getApplicationContext());

        Button historyButton = findViewById(R.id.viewHistoryButton);
        historyButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(this, CoinFlipHistoryActivity.class);
            startActivity(intent);
        });

        // doesn't matter which child chooses for first pick, so just start from beginning of children array
        if (CoinFlipHistory.getCoinFlipHistory().isEmpty() && !children.getChildren().isEmpty()) {
            setChildNameTextFirstFlip();
        } else if (!CoinFlipHistory.getCoinFlipHistory().isEmpty() && !children.getChildren().isEmpty()) {
            setChildNameText();
        }
        setUpCoinChoiceButtons();

        childNameDisplay = findViewById((R.id.childsTurnText));
        childNameDisplay = findViewById((R.id.childsTurnText));
        headsButton = findViewById(R.id.headsButton);
        tailsButton = findViewById(R.id.tailsButton);
        if (children.getChildren().isEmpty()) {
            setCoinChoiceButtonsGone();
        }
        setUpCoinChoiceButtons();

        Button flipButton = findViewById(R.id.flipButton);
        coin = findViewById(R.id.coinBlank);
        coinResult = findViewById(R.id.coinFlipResult);
        flipButton.setOnClickListener(view -> flipTheCoin());
    }

    private void setCoinChoiceButtonsGone() {
        childNameDisplay.setVisibility(View.GONE);
        sideChosenDisplay.setVisibility(View.GONE);
        headsButton.setVisibility(View.GONE);
        tailsButton.setVisibility(View.GONE);
    }

    private void setUpCoinChoiceButtons() {
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
    }

    private void setChildNameTextFirstFlip() {
        childNameFormat = getString(R.string.childTurn, children.getChildren().get(FIRST_CHILD_INDEX).getName());
        childNameDisplay = findViewById((R.id.childsTurnText));
        childNameDisplay.setText(childNameFormat);
    }

    private void setChildNameText() {
        childNameFormat = getString(R.string.childTurn, getChildForNextTurn().getName());
        childNameDisplay = findViewById((R.id.childsTurnText));
        childNameDisplay.setText(childNameFormat);
    }

    private void setSideChosenText() {
        String sideChosenFormat = getString(R.string.coinSideChosen, coinChosen.toString());
        sideChosenDisplay = findViewById(R.id.sideChosenText);
        sideChosenDisplay.setText(sideChosenFormat);
    }

    private void setupTitle() {
        ActionBar toolbar = getSupportActionBar();
        assert toolbar != null;
        toolbar.setTitle(R.string.menuFlipCoinBtn);
    }

    private void flipTheCoin() {
        coin.setImageResource(R.drawable.ic_coin_blank);
        Coin coinSide = CoinFlip.flipCoin();
        Animation animation = AnimationUtils.loadAnimation(CoinActivity.this, R.anim.flipanim);
        coin.startAnimation(animation);
        coinResult.setVisibility(View.INVISIBLE);
        coinSound.start();

        if (coinSide == Coin.HEADS) {
            coinResult.setText(HEADS);
            coin.postDelayed(displayHeads, COIN_FLIP_DELAY);
        } else { // tails
            coinResult.setText(TAILS);
            coin.postDelayed(displayTails, COIN_FLIP_DELAY);
        }
        coinResult.postDelayed(result, COIN_FLIP_DELAY);

        // If there are no children configured, we don't need to save any info
        if (!children.getChildren().isEmpty()) {
            saveCoinFlip(coinSide);

            new Handler(Looper.getMainLooper()).postDelayed(this::setChildNameText, COIN_FLIP_DELAY);
        }
    }

    private void saveCoinFlip(Coin coinSideLandedOn) {
        CoinFlip coinFlip;
        LocalDateTime timeOfFlip = LocalDateTime.now();

        Child childOfNextTurn = getChildForNextTurn();
        if (CoinFlipHistory.getCoinFlipHistory().isEmpty()) {
            coinFlip = new CoinFlip(children.getChildren().get(0), coinChosen, timeOfFlip, coinSideLandedOn);
        } else {
            coinFlip = new CoinFlip(childOfNextTurn, coinChosen, timeOfFlip, coinSideLandedOn);
        }

        coinFlipHistory.addCoinFlipToHistory(coinFlip);
    }

    private Child getChildForNextTurn() {
        ArrayList<Child> childrenArray = children.getChildren();

        Child childToPickLastTurn = getChildOfLastTurn();
        if (childToPickLastTurn == null && !childrenArray.isEmpty()) {
            return childrenArray.get(FIRST_CHILD_INDEX);
        }

        Child childOfNextTurn;
        for (int i = 0; i < childrenArray.size(); i++) {
            if (childrenArray.get(i).getUniqueId().toString().equals(childToPickLastTurn.getUniqueId().toString())) {
                childOfNextTurn = childrenArray.get((i + 1) % childrenArray.size());
                return childOfNextTurn;
            }
        }

        return childrenArray.get(FIRST_CHILD_INDEX);
    }

    private Child getChildOfLastTurn() {
        int sizeOfHistory = CoinFlipHistory.getCoinFlipHistory().size();
        if (!CoinFlipHistory.getCoinFlipHistory().isEmpty()) {
            return CoinFlipHistory.getCoinFlipHistory().get(sizeOfHistory - 1).getChildWhoPicksSide();
        }

        return null;
    }
}