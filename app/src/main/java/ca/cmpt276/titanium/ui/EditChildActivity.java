package ca.cmpt276.titanium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * This activity represents the editing of a single child.
 */
public class EditChildActivity extends AppCompatActivity {
    private final Children children = Children.getInstance(this);
    private UUID childUniqueId;
    private Child childBeingEdited;
    private EditText childName;
    private boolean changesAccepted = true;

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
        childName.setCursorVisible(true);
    }

    private boolean nameContainsOnlyLetters(String name) {
        char[] nameChars = name.toCharArray();

        for (char nameChar : nameChars) {
            if (!Character.isLetter(nameChar)) {
                return false;
            }
        }

        return true;
    }

    private void setupButtons() {
        Button saveButton = findViewById(R.id.viewFunctionBtn);
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(view -> {
            if (nameContainsOnlyLetters(childName.getText().toString())) {
                children.setChildName(childUniqueId, childName.getText().toString());
                Toast.makeText(this, R.string.edit_child_toast, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Name must contain only letters", Toast.LENGTH_SHORT).show();
            }
        });

        childName.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // minimize keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(childName.getWindowToken(), 0);

                childName.clearFocus();
                childName.setText(childName.getText());
            }

            return false;
        });

        childName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changesAccepted = childName.getText().toString().equals(childBeingEdited.getName());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void launchDiscardChangesPrompt() {
        if (!changesAccepted) {
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
        } else {
            finish();
        }
    }
}
