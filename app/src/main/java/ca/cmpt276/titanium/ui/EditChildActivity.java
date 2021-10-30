package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import java.util.Objects;

public class EditChildActivity extends AppCompatActivity {
    private static final int INVALID_UNIQUE_ID = -1;

    private final Children children = Children.getInstance(this);
    private int childUniqueId;
    private Child childBeingEdited;
    private EditText childName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_child);
        setupActionBar();

        this.childUniqueId = getIntent().getIntExtra("child_unique_id", INVALID_UNIQUE_ID);
        this.childBeingEdited = children.getChild(childUniqueId);
        displayChildInfo();

        setupSaveButton();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.menuEdit);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void displayChildInfo() {
        this.childName = findViewById(R.id.childName);
        this.childName.setText(childBeingEdited.getName());
        this.childName.setEnabled(true);
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.viewFunctionBtn);
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(view -> {
            children.getChild(childUniqueId).setName(childName.getText().toString());
            Toast.makeText(this, R.string.edit_child_toast, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    public static Intent makeIntent(Context context, int childUniqueId) {
        Intent editChildIntent = new Intent(context, EditChildActivity.class);
        editChildIntent.putExtra("child_unique_id", childUniqueId);

        return editChildIntent;
    }
}
