package ca.cmpt276.titanium.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class RemoveChildActivity extends AppCompatActivity {
    private Children children = Children.getInstance(this);
    private EditText childName;
    private Child childBeingRemoved;
    private Button remove;
    private static final Gson GSON = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        String childJson = getIntent().getStringExtra("child_json");
        this.childBeingRemoved = GSON.fromJson(childJson, Child.class);

        setupActionBar();
        setupScreenText();
        setupButton();
    }

    private void setupActionBar() {
        Toolbar customMenu = findViewById(R.id.customToolbar);
        setSupportActionBar(customMenu);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.menuRemove);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupButton() {
        remove.setOnClickListener(view -> {
            removeChildNow();
            Intent intent = MenuActivity.makeIntent(RemoveChildActivity.this);
            startActivity(intent);
            finish();
        });
    }

    private void removeChildNow() {
        children.removeChild(childBeingRemoved.getUniqueId());
        children.saveData();
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);
        this.childName.setText(childBeingRemoved.getName());
        this.childName.setEnabled(false);
        findViewById(R.id.viewRemoveMessage).setVisibility(View.VISIBLE);

        this.remove = findViewById(R.id.viewFunctionBtn);
        this.remove.setText(getResources().getString(R.string.viewRemove));
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, RemoveChildActivity.class);
    }
}