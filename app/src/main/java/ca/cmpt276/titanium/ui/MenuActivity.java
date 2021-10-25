package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class MenuActivity extends AppCompatActivity {
    private int numOfChildren;
    private Children childrenInstance;
    private ArrayList<Child> children;
    private Button flipCoinButton, timerButton, add, edit, remove;
    private FloatingActionButton mainMenuFAB;
    private LinearLayout fabOptions;
    private boolean optionsOpen;
    private TableRow scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAttributes();

        numOfChildren =childrenInstance.getNumOfChildren();

        setupScrollAllChildren();

        setupFAB();
        setupOptionButtons();

        flipCoinButtonClick();
        timerButtonClick();
    }

    @Override
    protected void onResume() {
        this.fabOptions.setVisibility(View.INVISIBLE);
        this.optionsOpen = false;
        this.scroll.refreshDrawableState();
        super.onResume();
    }

    private void setupAttributes() {
        this.childrenInstance = Children.getInstance(this);
        this.children = childrenInstance.getChildren();
        this.flipCoinButton = findViewById(R.id.menuGoToFlipCoin);
        this.timerButton = findViewById(R.id.menuGoToTimer);
        this.mainMenuFAB = findViewById(R.id.menuFAB);
        this.fabOptions = findViewById(R.id.linearLayoutContainer);
        this.add = findViewById(R.id.fabAdd);
        this.edit = findViewById(R.id.fabEdit);
        this.remove = findViewById(R.id.fabRemove);
        this.optionsOpen = false;
    }

    private void setupFAB() {
        this.mainMenuFAB.setOnClickListener(view -> {
            this.mainMenuFAB.setFocusable(true);
            if(optionsOpen){
                closeOptions();
            }
            else{
                openOptions();
            }
        });

        //add animation for the linearlayout to popup

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

    private void setupOptionButtons() {

        this.add.setOnClickListener(view -> {
            this.add.setFocusable(true);
            Intent intent = AddChildActivity.makeIntent(MenuActivity.this);
            startActivity(intent);
        });

        this.edit.setOnClickListener(view -> {
            if(this.numOfChildren>0){
                this.edit.setFocusable(true);
                Intent intent = EditChildActivity.makeIntent(MenuActivity.this);
                startActivity(intent);
            }else{
                Toast.makeText(this, "No children added, function unavailable.", Toast.LENGTH_SHORT).show();
            }
        });

        this.remove.setOnClickListener(view -> {
            if(this.numOfChildren>0){
                this.remove.setFocusable(true);
                Intent intent = RemoveChildActivity.makeIntent(MenuActivity.this);
                startActivity(intent);
            }else{
                Toast.makeText(this, "No children added, function unavailable.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openOptions(){
        this.mainMenuFAB.setFocusable(true);
        this.fabOptions.setVisibility(View.VISIBLE);
        this.optionsOpen=true;
    }

    private void closeOptions(){
        this.mainMenuFAB.setFocusable(false);
        this.fabOptions.setVisibility(View.INVISIBLE);
        this.optionsOpen=false;
    }
}