package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ca.cmpt276.titanium.R;

public class RemoveChildActivity extends AppCompatActivity {
    private TextView childName;
    private int childIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        setupScreenText();
    }

    private void setupScreenText() {

    }

    public static Intent makeIntent(Context c){
        return new Intent(c, RemoveChildActivity.class);
    }
}