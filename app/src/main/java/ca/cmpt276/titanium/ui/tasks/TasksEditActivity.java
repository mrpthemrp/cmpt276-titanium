package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.TaskManager;

/**
 * This class displays the details for a single task and allows the user to edit task data.
 */
public class TasksEditActivity extends AppCompatActivity {
    private static final String TASK_INDEX_KEY = "taskIndex";
    private static final int INVALID_TASK_INDEX = -1;

    private Toast toast; // prevents toast stacking
    private int taskIndex;

    public static Intent makeIntent(Context context, int taskIndex) {
        Intent intent = new Intent(context, TasksEditActivity.class);
        intent.putExtra(TASK_INDEX_KEY, taskIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_add);
        setTitle(getString(R.string.title_edit_task));

        Toolbar myToolbar = findViewById(R.id.ToolBar_tasks_add);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.taskIndex = getIntent().getIntExtra(TASK_INDEX_KEY, INVALID_TASK_INDEX);

        if (taskIndex == INVALID_TASK_INDEX) {
            finish();
        }

        setupGUI();
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

    private void setupGUI() {
        TextView titleAddText = findViewById(R.id.TextView_tasks_add_task_name_subtitle);
        titleAddText.setText(R.string.subtitle_task_name);

        TaskManager taskManager = TaskManager.getInstance(this);
        EditText taskNameInput = findViewById(R.id.EditText_tasks_add_task_name);
        taskNameInput.setText(taskManager.getTasks().get(taskIndex).getTaskName());

        Button saveTaskButton = findViewById(R.id.Button_tasks_add_save);
        saveTaskButton.setOnClickListener(view -> {
            if (taskNameInput.getText().toString().equals("")) {
                updateToast(getString(R.string.toast_name_field_empty));
            } else {
                taskManager.setTaskName(taskIndex, taskNameInput.getText().toString());
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
