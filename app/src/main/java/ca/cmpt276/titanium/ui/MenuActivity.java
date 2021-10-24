package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;

public class MenuActivity extends AppCompatActivity {
    private int numOfChildren;
    private Children childrenData;
    private Button flipCoinButton, timerButton;
    private FloatingActionButton mainMenuFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAttributes();

        //set up children data
        //numOfChildren =childrenData.getNumOfChildren();
        numOfChildren = 5;
        setupScrollAllChildren();
        setupFAB();

        flipCoinButtonClick();
        timerButtonClick();
    }

    private void setupAttributes() {
        this.childrenData = Children.getInstance(this);
        this.flipCoinButton = findViewById(R.id.menuGoToFlipCoin);
        this.timerButton = findViewById(R.id.menuGoToTimer);
        this.mainMenuFAB = findViewById(R.id.menuFAB);
    }

    private void setupFAB() {
        this.mainMenuFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popup = MenuActivity.this.getLayoutInflater().inflate(R.layout.fab_pop_up, null);

                Toast.makeText(MenuActivity.this,"FAB clicked.",Toast.LENGTH_SHORT).show();
            }
        });
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
            //need to get child

            child.setLayoutParams(new TableRow.LayoutParams(300, 300,1.0f));
            child.setBackground(getResources().getDrawable(R.drawable.ic_baseline_circle_green_24,getTheme()));
            child.setText("TEST VALUE");
            child.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            child.setOnClickListener(view -> Toast.makeText(MenuActivity.this, "Child Clicked!", Toast.LENGTH_SHORT).show());

            scroll.addView(child);
        }
    }
}