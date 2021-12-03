package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.Task;
import ca.cmpt276.titanium.model.TaskManager;

/**
 * This class displays the details for a single task.
 */
public class TaskManageActivity extends AppCompatActivity {
  private static final String TASK_INDEX_KEY = "taskIndex";
  private static final int INVALID_TASK_INDEX = -1;

  private TaskManager taskManager;
  private ChildManager childManager;
  private Toast toast; // prevents toast stacking
  private int taskIndex;
  private Task task;
  private Child child;
  TextView taskNameText;

  public static Intent makeIntent(Context context, int taskIndex) {
    Intent intent = new Intent(context, TaskManageActivity.class);
    intent.putExtra(TASK_INDEX_KEY, taskIndex);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_manage);

    Toolbar toolbar = findViewById(R.id.ToolBar_task_manage);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    this.taskManager = TaskManager.getInstance(this);
    this.childManager = ChildManager.getInstance(this);
    this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    this.taskIndex = getIntent().getIntExtra(TASK_INDEX_KEY, INVALID_TASK_INDEX);

    if (taskIndex == INVALID_TASK_INDEX) {
      finish();
    }

    this.task = taskManager.getTasks().get(taskIndex);

    if (task.getChildUniqueID() != null) {
      this.child = childManager.getChild(task.getChildUniqueID());
    }

    setupButtons();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_task_manage, menu);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    displayTaskData();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_item_task_manage_edit) {
      startActivity(TaskEditActivity.makeIntent(
          this, getString(R.string.title_task_edit), taskIndex));
      return true;
    } else if (item.getItemId() == R.id.menu_item_task_manage_delete) {
      launchDeleteTaskPrompt();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  private void displayTaskData() {
    taskNameText = findViewById(R.id.TextView_task_manage_task_name);
    taskNameText.setText(taskManager.getTasks().get(taskIndex).getTaskName());

    TextView childNameText = findViewById(R.id.TextView_task_manage_child_name);

    if (task.getChildUniqueID() == null) {
      childNameText.setText(getString(R.string.empty_state_tasks_no_children));
    } else {
      childNameText.setText(getString(R.string.tasks_next_child_name, child.getName()));
    }

    ImageView childPortraitView = findViewById(R.id.ImageView_task_manage_child_portrait);

    if (task.getChildUniqueID() == null) {
      childPortraitView.setImageResource(R.drawable.ic_default_portrait_green);
    } else {
      childPortraitView.setImageDrawable(child.getPortrait(getResources()));
    }
  }

  private void setupButtons() {
    Button completeTaskButton = findViewById(R.id.Button_task_manage_complete_task);
    completeTaskButton.setOnClickListener(view -> {
      ArrayList<Child> children = childManager.getChildren();

      if (task.getChildUniqueID() != null) {
        String taskName = taskNameText.getText().toString();
        UUID childID = task.getChildUniqueID();
        taskManager.addHistoryTask(taskName, childID);

        int nextChildIndex = (children.indexOf(child) + 1) % children.size();
        UUID nextChildUniqueID = children.get(nextChildIndex).getUniqueID();
        taskManager.setChildUniqueID(taskIndex, nextChildUniqueID);
      }
      updateToast(getString(R.string.toast_task_manage_completed));
      finish();
    });

    Button historyTaskButton = findViewById(R.id.Button_task_manage_history);
    historyTaskButton.setOnClickListener(view -> startActivity(TaskHistoryActivity.makeIntent(TaskManageActivity.this, taskIndex)));
  }

  private void updateToast(String toastText) {
    toast.cancel();
    toast.setText(toastText);
    toast.show();
  }

  private void launchDeleteTaskPrompt() {
    new AlertDialog.Builder(this)
        .setIcon(R.drawable.ic_baseline_delete_black_24)
        .setTitle(getString(R.string.prompt_title_task_manage_delete))
        .setMessage(getString(R.string.prompt_message_task_manage_delete))
        .setPositiveButton(R.string.prompt_positive_tasks, (dialog, which) -> {
          taskManager.taskDeletedFromHistory(task.getTaskName());
          taskManager.removeTask(taskIndex);
          updateToast(getString(R.string.toast_task_manage_deleted));
          finish();
        })
        .setNegativeButton(R.string.prompt_negative_tasks, null)
        .show();
  }
}
