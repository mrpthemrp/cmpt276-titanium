package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class EditChildActivity extends AppCompatActivity {
    private Children instance = Children.getInstance(this);
    private EditText childName;
    private Child selectedChild;
    private Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        //setup text watcher!!

        setupActionBar();
        findSelectedChild();
        setupScreenText();
        setupButton();
    }

    private void setupActionBar() {
        Toolbar customMenu = findViewById(R.id.customToolbar);
        setSupportActionBar(customMenu);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.menuEdit);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupButton() {
        edit.setOnClickListener(view -> {
            instance.saveData();
            selectedChild.setSelected(false);
            finish();
        });
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

        this.edit = findViewById(R.id.viewFunctionBtn);
        this.edit.setText(getResources().getString(R.string.viewSave));
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, EditChildActivity.class);
    }

}