package ca.cmpt276.titanium.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MenuActivity extends AppCompatActivity {
    private Children children;
    private Button flipCoinButton, timerButton;
    private FloatingActionButton mainMenuFAB;
    private TableRow scroll;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupScrollAllChildren();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scroll.removeAllViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        children.saveData();
    }

    private void setupFAB() {
        this.mainMenuFAB.setOnClickListener(view -> {
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
            Button childButton = new Button(this);
            Child child = children.getChildren().get(i);
            childButton.setLayoutParams(new TableRow.LayoutParams(300, 300, 1.0f));
            childButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_circle_green_24, getTheme()));
            childButton.setText(child.getName());
            childButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            childButton.setOnClickListener(view -> {
                Intent viewChildIntent = ViewChildActivity.makeIntent(this, child.getUniqueId());
                startActivity(viewChildIntent);
            });

            scroll.addView(childButton);
        }
    }
}
