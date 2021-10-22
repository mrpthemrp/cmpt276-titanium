package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TableRow;
import android.widget.Toast;

import ca.cmpt276.titanium.R;

public class MenuActivity extends AppCompatActivity {
    private int numOfAddedChildren; // will change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numOfAddedChildren=10;//will change
        setupScrollAllChildren();
    }

    private void setupScrollAllChildren() {
        TableRow scroll = findViewById(R.id.menuRow);
        for(int i =0; i<numOfAddedChildren;i++){
            View child = LayoutInflater.from(this).inflate(R.layout.child_icon,null);
            //onclick not working, fix if possible otherwise leave.
            scroll.addView(child);
        }
    }
}