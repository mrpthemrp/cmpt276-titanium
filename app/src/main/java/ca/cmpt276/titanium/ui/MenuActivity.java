package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;

public class MenuActivity extends AppCompatActivity {
    private int numOfChildren;
    private Children childrenData;
    private Button flipCoinButton, timerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up children data
        childrenData = Children.getInstance(this);
        //numOfChildren =childrenData.getNumOfChildren();
        numOfChildren = 5;
        setupScrollAllChildren();

        flipCoinButton = findViewById(R.id.menuGoToFlipCoin);
        flipCoinButtonClick();
        timerButton = findViewById(R.id.menuGoToTimer);
        timerButtonClick();
    }

    private void flipCoinButtonClick(){
        flipCoinButton.setOnClickListener(view -> {
            Intent i = CoinActivity.makeLaunchIntent(MenuActivity.this);
            startActivity(i);
        });
    }

    private void timerButtonClick(){
        timerButton.setOnClickListener(view -> {
            Intent i = TimerActivity.makeLaunchIntent(MenuActivity.this);
            startActivity(i);
        });
    }

    private void setupScrollAllChildren() {
        TableRow scroll = findViewById(R.id.menuRow);
        for(int i =0; i<numOfChildren;i++){
            Button child = new Button(this);
            child.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f
            ));
            child.setText("TEST VALUE");
            //need to get child


            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MenuActivity.this, "Child Clicked!", Toast.LENGTH_SHORT).show();
                }
            });

            scroll.addView(child);
        }
    }
}