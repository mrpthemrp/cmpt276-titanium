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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        flipButton = findViewById(R.id.flipButton);
        coin = findViewById(R.id.coinHeads);
        coinResult = findViewById(R.id.coinFlipResult);

        flipButtonClick();

    }

    //Note to Harris: feel free to change the coin from imageview to anything else!
    //Also once everything is merged, you'll have to create a child icon in the top
    //right corner. - deborah, Oct 21, 2021

    public static Intent makeLaunchIntent(Context c){
        return new Intent(c, CoinActivity.class);
    }

    private void flipTheCoin(){
        Random rand = new Random();
        int coinSide = rand.nextInt(2);
        Animation animation = AnimationUtils.loadAnimation(CoinActivity.this, R.anim.fade);
        coin.startAnimation(animation);

        // heads == 0
        if(coinSide == 0){
            coin.setImageResource(R.drawable.ic_coin_heads);
            coinResult.setVisibility(View.INVISIBLE);
            coinResult.setText(HEADS);
            coinResult.postDelayed(result, 500);
        }

        // tails == 1
        else{
            coin.setImageResource(R.drawable.ic_coin_tails);
            coinResult.setVisibility(View.INVISIBLE);
            coinResult.setText(TAILS);
            coinResult.postDelayed(result, 500);
        }
    }

    private void flipButtonClick(){
        flipButton.setOnClickListener(view -> flipTheCoin());
    }

    Runnable result = () -> coinResult.setVisibility(View.VISIBLE);

}