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

public class ViewChildActivity extends AppCompatActivity {
    private Children instance = Children.getInstance(this);
    private TextView childName;
    private Child selectedChild;
    private Button view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        setupActionBar();
        setupButton();
        findSelectedChild();
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
                Intent intent1 = EditChildActivity.makeIntent(ViewChildActivity.this);
                startActivity(intent1);
                return true;
            case R.id.optionsRemove:
                Intent intent2 = RemoveChildActivity.makeIntent(ViewChildActivity.this);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        this.childName.setEnabled(true);
        super.onStop();
    }

    private void findSelectedChild() {
        for (int i = 0; i < instance.getNumOfChildren(); i++) {
            if (Children.getChildren().get(i).isSelected()) {
                this.selectedChild = instance.getChild(i);
            }
        }
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);
        this.childName.setText(selectedChild.getName());
        this.childName.setEnabled(false);

        this.view = findViewById(R.id.viewFunctionBtn);
        this.view.setVisibility(View.INVISIBLE);
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, ViewChildActivity.class);
    }

}