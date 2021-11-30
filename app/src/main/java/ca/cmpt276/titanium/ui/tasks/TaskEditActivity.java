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

import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.TaskManager;

/**
 * Allows a user to add and edit a Task object.
 *
 * @author Titanium
 */
public class TaskEditActivity extends AppCompatActivity {
  private static final String INTENT_TYPE_KEY = "intentType";
  private static final String TASK_INDEX_KEY = "taskIndex";
  private static final int INVALID_TASK_INDEX = -1;

  private String intentType;
  private Toast toast; // prevents toast stacking
  private int taskIndex;

  public static Intent makeIntent(Context context, String intentType, int taskIndex) {
    Intent intent = new Intent(context, TaskEditActivity.class);
    intent.putExtra(INTENT_TYPE_KEY, intentType);
    intent.putExtra(TASK_INDEX_KEY, taskIndex);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_edit);

    this.intentType = getIntent().getStringExtra(INTENT_TYPE_KEY);
    setupActionBar(intentType);

    this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    this.taskIndex = getIntent().getIntExtra(TASK_INDEX_KEY, INVALID_TASK_INDEX);

    if (intentType.equals(getString(R.string.title_task_edit)) && taskIndex == INVALID_TASK_INDEX) {
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

  private void setupActionBar(String intentType) {
    setSupportActionBar(findViewById(R.id.ToolBar_task_edit));
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    setTitle(intentType);
  }

  private void setupGUI() {
    if (intentType.equals(getString(R.string.title_task_edit))) {
      TextView titleAddText = findViewById(R.id.TextView_task_edit_task_name_subtitle);
      titleAddText.setText(R.string.subtitle_tasks_task_name);

      TaskManager taskManager = TaskManager.getInstance(this);
      EditText taskNameInput = findViewById(R.id.EditText_task_edit_task_name);
      taskNameInput.setText(taskManager.getTasks().get(taskIndex).getTaskName());
      String oldTask = taskManager.getTasks().get(taskIndex).getTaskName();
      Button saveTaskButton = findViewById(R.id.Button_task_edit_save_task);
      saveTaskButton.setOnClickListener(view -> {
        if (taskNameInput.getText().toString().equals("")) {
          updateToast(getString(R.string.toast_task_edit_name_field_empty));
        } else {
          taskManager.setTaskName(taskIndex, taskNameInput.getText().toString());
          taskManager.editTaskFromHistory(oldTask, taskNameInput.getText().toString());
          finish();
        }
      });
    } else {
      Button saveTaskButton = findViewById(R.id.Button_task_edit_save_task);
      saveTaskButton.setOnClickListener(view -> {
        EditText taskNameInput = findViewById(R.id.EditText_task_edit_task_name);

        if (taskNameInput.getText().toString().equals("")) {
          updateToast(getString(R.string.toast_task_edit_name_field_empty));
        } else {
          TaskManager taskManager = TaskManager.getInstance(this);
          ChildManager childManager = ChildManager.getInstance(this);

          UUID childUniqueID =
              childManager.getChildren().size() > 0
                  ? childManager.getChildren().get(0).getUniqueID()
                  : null;

          taskManager.addTask(taskNameInput.getText().toString(), childUniqueID);

          updateToast(getString(R.string.toast_task_edit_saved));
          finish();
        }
      });
    }
  }

  private void updateToast(String toastText) {
    toast.cancel();
    toast.setText(toastText);
    toast.show();
  }

  private void launchDiscardChangesPrompt() { // TODO: Check for no changes
    new AlertDialog.Builder(this)
        .setIcon(R.drawable.ic_baseline_warning_black_24)
        .setTitle(R.string.prompt_title_task_edit_discard_changes)
        .setMessage(R.string.prompt_message_task_edit_discard_changes)
        .setPositiveButton(R.string.prompt_positive_tasks, (dialog, which) -> {
          updateToast(getString(R.string.toast_task_edit_changes_discarded));
          finish();
        })
        .setNegativeButton(R.string.prompt_negative_tasks, null)
        .show();
  }
}
