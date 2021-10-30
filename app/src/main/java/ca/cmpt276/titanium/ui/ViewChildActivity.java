package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import com.google.gson.Gson;

public class ViewChildActivity extends AppCompatActivity {
    private Children children;
    private TextView childName;
    private Child childBeingViewed;
    private Button view;
    private String childJson;
    private static final Gson GSON = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        this.childJson = getIntent().getStringExtra("child_json");
        this.childBeingViewed = GSON.fromJson(childJson, Child.class);
        this.children = Children.getInstance(this);

        setupActionBar();
        setupButton();
        setupScreenText();
    }

    private void setupButton() {
        this.view = findViewById(R.id.viewFunctionBtn);
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.child_menu_options, menu);
        return true;
    }

    private void setupActionBar() {
        Toolbar customMenu = findViewById(R.id.customToolbar);
        setSupportActionBar(customMenu);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.menuViewChild);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionsEdit:
                Intent editChildIntent = EditChildActivity.makeIntent(ViewChildActivity.this);
                editChildIntent.putExtra("child_json", childJson);
                startActivity(editChildIntent);
                return true;
            case R.id.optionsRemove:
                Intent removeChildIntent = RemoveChildActivity.makeIntent(ViewChildActivity.this);
                removeChildIntent.putExtra("child_json", childJson);
                startActivity(removeChildIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);
        this.childName.setText(childBeingViewed.getName());
        this.childName.setEnabled(false);

        this.view = findViewById(R.id.viewFunctionBtn);
        this.view.setVisibility(View.INVISIBLE);
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, ViewChildActivity.class);
    }

}