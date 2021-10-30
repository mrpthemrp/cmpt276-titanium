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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;
import java.util.Objects;

public class AddChildActivity extends AppCompatActivity {
    private final Children children = Children.getInstance(this);
    private EditText childName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        setupActionBar();

        setupInputFields();

        setupSaveButton();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            launchDiscardChangesPrompt();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        launchDiscardChangesPrompt();
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.menuAdd);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupInputFields() {
        this.childName = findViewById(R.id.childName);
        this.childName.setEnabled(true);
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.viewFunctionBtn);
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(view -> {
            children.addChild(childName.getText().toString());
            Toast.makeText(this, R.string.add_child_toast, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void launchDiscardChangesPrompt() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_warning_black_24)
                .setTitle(R.string.discard_changes_title)
                .setMessage(R.string.discard_changes_message)
                .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                    Toast.makeText(this, R.string.changes_discarded_toast, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.prompt_negative, null)
                .show();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddChildActivity.class);
    }
}
