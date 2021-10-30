package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

public class EditChildActivity extends AppCompatActivity {
    private Children children = Children.getInstance(this);
    private EditText childName;
    private Child childBeingEdited;
    private Button edit;
    private static final Gson GSON = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);

        String childJson = getIntent().getStringExtra("child_json");
        this.childBeingEdited = GSON.fromJson(childJson, Child.class);

        setupActionBar();
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
            updateChildName();
            children.saveData();
            Intent intent = MenuActivity.makeIntent(EditChildActivity.this);
            startActivity(intent);
            finish();
        });
    }

    private void updateChildName(){
        int id = childBeingEdited.getUniqueId();
        children.getChild(id).setName(childName.getText().toString());
    }

    private void setupScreenText() {
        this.childName = findViewById(R.id.childName);
        this.childName.setText(childBeingEdited.getName());

        this.edit = findViewById(R.id.viewFunctionBtn);
        this.edit.setText(getResources().getString(R.string.viewSave));
    }

    public static Intent makeIntent(Context c) {
        return new Intent(c, EditChildActivity.class);
    }

}