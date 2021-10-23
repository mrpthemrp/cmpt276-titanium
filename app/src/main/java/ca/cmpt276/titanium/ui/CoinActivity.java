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

import java.util.Random;

import ca.cmpt276.titanium.R;

public class CoinActivity extends AppCompatActivity {

    Button flipButton;
    ImageView coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        flipButton = findViewById(R.id.flipButton);
        coin = findViewById(R.id.coinHeads);

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
        }

        // tails == 1
        else{
            coin.setImageResource(R.drawable.ic_coin_tails);
        }
    }

    private void flipButtonClick(){
        flipButton.setOnClickListener(view -> {
            flipTheCoin();
        });
    }


}