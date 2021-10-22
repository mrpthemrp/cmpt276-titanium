package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ca.cmpt276.titanium.R;

public class CoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
    }

    //Note to Harris: feel free to change the coin from imageview to anything else!
    //Also once everything is merged, you'll have to create a child icon in the top
    //right corner. - deborah, Oct 21, 2021
}