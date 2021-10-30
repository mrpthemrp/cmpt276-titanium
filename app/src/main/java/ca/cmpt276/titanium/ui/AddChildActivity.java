package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class AddChildActivity extends AppCompatActivity {
    private Children children = Children.getInstance(this);
    private EditText childName;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        childName = findViewById(R.id.childName);

        setupActionBar();
        setupScreenText();
        setupButton();
    }

    private void setupActionBar() {
        Toolbar customMenu = findViewById(R.id.customToolbar);
        setSupportActionBar(customMenu);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.menuAdd);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupButton() {
        add.setOnClickListener(view -> {
            addChild(childName.getText().toString());
            finish();
        });
    }

    private void addChild(String name) {
        children.addChild(name);
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);

        this.add = findViewById(R.id.viewFunctionBtn);
        this.add.setText(getResources().getString(R.string.viewSave));
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, AddChildActivity.class);
    }
}