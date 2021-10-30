package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class MenuActivity extends AppCompatActivity {
    private Children children;
    private Button flipCoinButton, timerButton;
    private FloatingActionButton mainMenuFAB;
    private TableRow scroll;
    private static final Gson GSON = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.children = Children.getInstance(this);
        this.flipCoinButton = findViewById(R.id.menuGoToFlipCoin);
        this.timerButton = findViewById(R.id.menuGoToTimer);
        this.mainMenuFAB = findViewById(R.id.menuFAB);

        children.loadSavedData();

        setupFAB();
        flipCoinButtonClick();
        timerButtonClick();
        setupScrollAllChildren();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this.mainMenuFAB.setFocusable(false);
        scroll.removeAllViews();
        setupScrollAllChildren();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        children.saveData();
    }

    private void setupFAB() {
        this.mainMenuFAB.setOnClickListener(view -> {
            //this.mainMenuFAB.setFocusable(true);
            Intent intent = AddChildActivity.makeIntent(MenuActivity.this);
            startActivity(intent);
        });

    }

    private void flipCoinButtonClick() {
        flipCoinButton.setOnClickListener(view -> {
            Intent i = CoinActivity.makeLaunchIntent(MenuActivity.this);
            startActivity(i);
        });
    }

    private void timerButtonClick() {
        timerButton.setOnClickListener(view -> {
            Intent i = TimerActivity.makeLaunchIntent(MenuActivity.this);
            startActivity(i);
        });
    }

    private void setupScrollAllChildren() {
        scroll = findViewById(R.id.menuRow);
        for (int i = 0; i < children.getNumOfChildren(); i++) {
            final int INDEX = i;
            Button childButton = new Button(this);
            Child child = children.getChildren().get(INDEX);
            childButton.setLayoutParams(new TableRow.LayoutParams(300, 300, 1.0f));
            childButton.setBackground(getResources().getDrawable(R.drawable.ic_baseline_circle_green_24, getTheme()));
            childButton.setText(child.getName());
            childButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            childButton.setOnClickListener(view -> {
                String childJson = GSON.toJson(child);

                Intent viewChildIntent = ViewChildActivity.makeIntent(this);
                viewChildIntent.putExtra("child_json", childJson);

                startActivity(viewChildIntent);
            });

            scroll.addView(childButton);
        }
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, MenuActivity.class);
    }
}