package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class MenuActivity extends AppCompatActivity {
    private int numOfChildren;
    private Children childrenInstance;
    private ArrayList<Child> children;
    private Button flipCoinButton, timerButton;
    private FloatingActionButton mainMenuFAB;
    private TableRow scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAttributes();

        numOfChildren =childrenInstance.getNumOfChildren();

        setupScrollAllChildren();

        setupFAB();
        flipCoinButtonClick();
        timerButtonClick();
    }

    @Override
    protected void onResume() {
        this.mainMenuFAB.setFocusable(false);
        this.scroll.refreshDrawableState();
        super.onResume();
    }

    private void setupAttributes() {
        this.childrenInstance = Children.getInstance(this);
        this.children = childrenInstance.getChildren();
        this.flipCoinButton = findViewById(R.id.menuGoToFlipCoin);
        this.timerButton = findViewById(R.id.menuGoToTimer);
        this.mainMenuFAB = findViewById(R.id.menuFAB);
    }

    private void setupFAB() {
        this.mainMenuFAB.setOnClickListener(view -> {
            this.mainMenuFAB.setFocusable(true);
            Intent intent = AddChildActivity.makeIntent(MenuActivity.this);
            startActivity(intent);
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
        scroll = findViewById(R.id.menuRow);
        for(int i =0; i<numOfChildren;i++){
            final int INDEX = i;
            Button child = new Button(this);
            Child childData = children.get(i);
            child.setId(childData.getUniqueId());
            child.setLayoutParams(new TableRow.LayoutParams(300, 300,1.0f));
            child.setBackground(getResources().getDrawable(R.drawable.ic_baseline_circle_green_24,getTheme()));
            child.setText(childData.getName());
            child.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            child.setOnClickListener(view -> {
                children.get(INDEX).setSelected(true);
                Intent intent = ViewChildActivity.makeIntent(this);
                startActivity(intent);
            });

            scroll.addView(child);
        }
    }
}