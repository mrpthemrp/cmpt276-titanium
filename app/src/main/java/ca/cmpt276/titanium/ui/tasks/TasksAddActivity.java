package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.TaskManager;

// TODO: Combine TaskAddActivity, TaskEditActivity, and TaskViewActivity (like with ChildActivity)

/**
 * This class allows a user to create new tasks.
 */
public class TasksAddActivity extends AppCompatActivity {
    private Toast toast; // prevents toast stacking

    public static Intent makeIntent(Context context) {
        return new Intent(context, TasksAddActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_add);
        setTitle(getString(R.string.title_add_task));

        Toolbar toolbar = findViewById(R.id.customToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

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

    private void setupSaveButton() {
        Button saveTaskButton = findViewById(R.id.saveTask);
        saveTaskButton.setOnClickListener(view -> {
            EditText taskNameInput = findViewById(R.id.userTaskName);

            if (taskNameInput.getText().toString().equals("")) {
                updateToast(getString(R.string.toast_name_field_empty));
            } else {
                TaskManager taskManager = TaskManager.getInstance(this);
                Children children = Children.getInstance(this);

                UUID childUniqueID =
                        children.getChildren().size() > 0
                                ? children.getChildren().get(0).getUniqueID()
                                : null;

                taskManager.addTask(taskNameInput.getText().toString(), childUniqueID);

                updateToast(getString(R.string.toast_task_saved));
                finish();
            }
        });
    }

    private void updateToast(String toastText) {
        toast.cancel();
        toast.setText(toastText);
        toast.show();
    }

    private void launchDiscardChangesPrompt() { // TODO: Check for no changes
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_warning_black_24)
                .setTitle(R.string.prompt_title_discard_changes)
                .setMessage(R.string.prompt_message_discard_changes)
                .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                    updateToast(getString(R.string.toast_changes_discarded));
                    finish();
                })
                .setNegativeButton(R.string.prompt_negative, null)
                .show();
    }
}
