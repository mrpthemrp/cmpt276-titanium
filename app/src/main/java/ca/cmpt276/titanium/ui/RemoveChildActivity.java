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

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class RemoveChildActivity extends AppCompatActivity {
    private Children instance = Children.getInstance(this);
    private EditText childName;
    private Child selectedChild;
    private Button remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        setupActionBar();
        findSelectedChild();
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
            finish();
        });
    }

    private void removeChildNow() {
        instance.removeChild(childName.getId());
        instance.saveData();
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
        findViewById(R.id.viewRemoveMessage).setVisibility(View.VISIBLE);

        this.remove = findViewById(R.id.viewFunctionBtn);
        this.remove.setText(getResources().getString(R.string.viewRemove));
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, RemoveChildActivity.class);
    }
}