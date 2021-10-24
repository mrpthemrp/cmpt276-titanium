package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import ca.cmpt276.titanium.R;

public class CoinActivity extends AppCompatActivity {

    private Button flipButton;
    private ImageView coin;
    private TextView coinResult;
    private static final String HEADS = "HEADS";
    private static final String TAILS = "TAILS";

    private Runnable result = () -> coinResult.setVisibility(View.VISIBLE);
    private Runnable displayHeads = () -> coin.setImageResource(R.drawable.ic_coin_heads);
    private Runnable displayTails = () -> coin.setImageResource(R.drawable.ic_coin_tails);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        flipButton = findViewById(R.id.flipButton);
        coin = findViewById(R.id.coinHeads);
        coinResult = findViewById(R.id.coinFlipResult);

        flipButtonClick();
    }

    public static Intent makeLaunchIntent(Context c){
        return new Intent(c, CoinActivity.class);
    }

    private void flipTheCoin(){
        Random rand = new Random();
        int coinSide = rand.nextInt(2);
        Animation animation = AnimationUtils.loadAnimation(CoinActivity.this, R.anim.flipanim);
        coin.startAnimation(animation);
        coinResult.setVisibility(View.INVISIBLE);

        // heads == 0
        if(coinSide == 0){
            coinResult.setText(HEADS);
            coin.postDelayed(displayHeads, 1600);
        }

        // tails == 1
        else{
            coinResult.setText(TAILS);
            coin.postDelayed(displayTails, 1600);
        }
        coinResult.postDelayed(result, 1600);
    }

    private void flipButtonClick(){
        flipButton.setOnClickListener(view -> flipTheCoin());
    }

}