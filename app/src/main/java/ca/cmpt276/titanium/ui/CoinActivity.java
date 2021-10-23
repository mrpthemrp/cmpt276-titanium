package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

import ca.cmpt276.titanium.R;

public class CoinActivity extends AppCompatActivity {

    Button flipButton;
    ImageView coinHead, coinTails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        flipButton = findViewById(R.id.flipButton);
        coinHead = findViewById(R.id.coinHeads);
        coinTails = findViewById(R.id.coinTails);

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

        // heads == 0
        if(coinSide == 0){

        }

        // tails == 1


    }

    private void flipButtonClick(){
        flipButton.setOnClickListener(view -> {
            flipTheCoin();
        });
    }


}