package ca.cmpt276.titanium.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
    private static final int CHILD_BUTTON_HEIGHT = 300;
    private static final int CHILD_BUTTON_WIDTH = 300;
    private static final float CHILD_BUTTON_ALPHA = 1.0f;

    private Children children;
    private TableRow childScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.children = Children.getInstance(this);
        this.childScrollView = findViewById(R.id.menuRow);

        if(this.children.getChildren().size()>0){
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        }
        children.loadSavedData();

        FloatingActionButton addChildButton = findViewById(R.id.menuFAB);
        addChildButton.setOnClickListener(view -> startActivity(AddChildActivity.makeIntent(this)));

        Button coinFlipButton = findViewById(R.id.menuGoToFlipCoin);
        coinFlipButton.setOnClickListener(view -> startActivity(CoinActivity.makeIntent(this)));

        Button timerButton = findViewById(R.id.menuGoToTimer);
        timerButton.setOnClickListener(view -> startActivity(TimerActivity.makeIntent(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.childScrollView.removeAllViews();
        displayChildren();

        if(this.children.getChildren().size()>0){
            findViewById(R.id.menuTextChildrenList).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.menuTextChildrenList).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        children.saveData();
    }

    private void displayChildren() {
        for (int i = 0; i < children.getChildren().size(); i++) {
            Child child = children.getChildren().get(i);

            Button childButton = new Button(this);
            childButton.setLayoutParams(new TableRow.LayoutParams(CHILD_BUTTON_WIDTH, CHILD_BUTTON_HEIGHT, CHILD_BUTTON_ALPHA));
            childButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_circle_green_24, getTheme()));

            childButton.setText(child.getName());
            childButton.setTextColor(getResources().getColor(R.color.black,getTheme()));
            childButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            childButton.setOnClickListener(view -> {
                Intent viewChildIntent = ViewChildActivity.makeIntent(this, child.getUniqueId());
                startActivity(viewChildIntent);
            });

            childScrollView.addView(childButton);
        }
    }
}
