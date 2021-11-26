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

import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.ChildManager;
import ca.cmpt276.titanium.model.Task;
import ca.cmpt276.titanium.model.TaskManager;

/**
 * This class displays the details for a single task.
 */
public class TasksViewActivity extends AppCompatActivity {
    private static final String TASK_INDEX_KEY = "taskIndex";
    private static final int INVALID_TASK_INDEX = -1;

    private TaskManager taskManager;
    private ChildManager childManager;
    private Toast toast; // prevents toast stacking
    private int taskIndex;

    public static Intent makeIntent(Context context, int taskIndex) {
        Intent intent = new Intent(context, TasksViewActivity.class);
        intent.putExtra(TASK_INDEX_KEY, taskIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_view);

        Toolbar toolbar = findViewById(R.id.ToolBar_tasks_view);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.taskManager = TaskManager.getInstance(this);
        this.childManager = ChildManager.getInstance(this);
        this.toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        this.taskIndex = getIntent().getIntExtra(TASK_INDEX_KEY, INVALID_TASK_INDEX);

        if (taskIndex == INVALID_TASK_INDEX) {
            finish();
        }

        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks_view, menu);
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
        } else if (item.getItemId() == R.id.menu_item_tasks_view_edit_task) {
            startActivity(TasksEditActivity.makeIntent(this, taskIndex));
            return true;
        } else if (item.getItemId() == R.id.menu_item_tasks_view_delete_task) {
            launchDeleteTaskPrompt();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void displayTaskData() {
        Task task = taskManager.getTasks().get(taskIndex);

        String taskName = task.getTaskName();
        String childName =
                task.getChildUniqueID() == null
                        ? getString(R.string.default_name_no_children)
                        : childManager.getChild(task.getChildUniqueID()).getName();

        TextView taskNameText = findViewById(R.id.TextView_tasks_view_task_name);
        taskNameText.setText(taskName);

        TextView childNameText = findViewById(R.id.TextView_tasks_view_child_name);
        childNameText.setText(getString(R.string.tasks_next_child_name, childName));

        ImageView childPortraitView = findViewById(R.id.ImageView_tasks_view_child_portrait);

        if (task.getChildUniqueID() == null) {
            childPortraitView.setImageResource(R.drawable.ic_default_portrait_green);
        } else {
            childPortraitView.setImageDrawable(childManager.getChild(task.getChildUniqueID()).getPortrait(getResources()));
        }
    }

    private void setupButtons() {
        Button completeTaskButton = findViewById(R.id.Button_tasks_view_complete_task);
        completeTaskButton.setOnClickListener(view -> {
            UUID currentChildUniqueID = taskManager.getTasks().get(taskIndex).getChildUniqueID();

            if (currentChildUniqueID != null) {
                int nextChildIndex = (childManager.getChildren().indexOf(childManager.getChild(currentChildUniqueID)) + 1) % childManager.getChildren().size();
                UUID nextChildUniqueID = childManager.getChildren().get(nextChildIndex).getUniqueID();
                taskManager.setChildUniqueID(taskIndex, nextChildUniqueID);
            }

            updateToast(getString(R.string.toast_task_completed));
            finish();
        });
    }

    private void updateToast(String toastText) {
        toast.cancel();
        toast.setText(toastText);
        toast.show();
    }

    private void launchDeleteTaskPrompt() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_delete_black_24)
                .setTitle(getString(R.string.prompt_title_delete_task))
                .setMessage(getString(R.string.prompt_delete_task_message))
                .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                    taskManager.removeTask(taskIndex);
                    updateToast(getString(R.string.toast_task_deleted));
                    finish();
                })
                .setNegativeButton(R.string.prompt_negative, null)
                .show();
    }
}