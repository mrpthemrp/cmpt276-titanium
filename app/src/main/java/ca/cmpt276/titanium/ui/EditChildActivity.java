package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;

// TODO: Only call launchDiscardChangesPrompt() after user has made changes to a field

/**
 * This activity represents the editing of a single child.
 */
public class EditChildActivity extends AppCompatActivity {
    private final Children children = Children.getInstance(this);
    private UUID childUniqueId;
    private Child childBeingEdited;
    private EditText childName;

    public static Intent makeIntent(Context context, UUID childUniqueId) {
        Intent editChildIntent = new Intent(context, EditChildActivity.class);
        editChildIntent.putExtra("child_unique_id", childUniqueId);

        return editChildIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        setupActionBar();

        this.childUniqueId = (UUID) getIntent().getSerializableExtra("child_unique_id");
        this.childBeingEdited = children.getChild(childUniqueId);
        displayChildInfo();

        setupButtons();
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
        setTitle(R.string.menuEdit);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void displayChildInfo() {
        this.childName = findViewById(R.id.childName);
        childName.setText(childBeingEdited.getName());
        childName.setEnabled(true);
    }

    private void setupButtons() {
        Button saveButton = findViewById(R.id.viewFunctionBtn);
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(view -> {
            children.getChild(childUniqueId).setName(childName.getText().toString());
            children.saveData();
            Toast.makeText(this, R.string.edit_child_toast, Toast.LENGTH_SHORT).show();
            finish();
        });

        childName.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // minimize keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(childName.getWindowToken(), 0);

                childName.clearFocus();
            }

            return false;
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
}
