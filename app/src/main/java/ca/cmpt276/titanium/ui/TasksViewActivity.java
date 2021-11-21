package ca.cmpt276.titanium.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Tasks;

public class TasksViewActivity extends AppCompatActivity {

    private static final String INDEX = "UserClicked";
    private int index;
    private Children children;
    private Tasks taskManager;

    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, TasksViewActivity.class);
        intent.putExtra(INDEX, index);
        return intent;
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        index = intent.getIntExtra(INDEX, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_view);
        this.children = Children.getInstance(this);
        taskManager = Tasks.getInstance();

        extractIntentData();
        displayData();
        setUpButtons();

        Toolbar myToolbar = findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        displayData();
        setUpButtons();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks_view, menu);
        return true;
    }

    private void displayData() {
        TextView childName = findViewById(R.id.childNameText);
        TextView taskName = findViewById(R.id.taskNameText);

        UUID Id = taskManager.getChildID(index);
        String name = children.getChild(Id).getName();
        String task = taskManager.getTask(index);

        childName.setText(name);
        taskName.setText(task);

        // TODO: Add image here from ID Above (UUID Id)
    }

    private void setUpButtons() {
        Button completeTask = findViewById(R.id.completeTaskButton);
        completeTask.setOnClickListener(view -> {

            UUID Id = taskManager.getChildID(index);
            Child nextChild;

            int nextIndex = 0;

            for (int i = 0; i < children.getChildren().size(); i++) {
                if (children.getChildren().get(i).getUniqueID().equals(Id)) {
                    break;
                }
                nextIndex++;
            }

            nextIndex++;

            if (nextIndex >= children.getChildren().size()) {
                nextIndex = 0;
            }

            if (children.getChildren().size() > 0) {
                nextChild = children.getChildren().get(nextIndex);
                taskManager.nextChild(index, nextChild);
            }
            finish();

        });

        Button cancel = findViewById(R.id.cancelTaskButton);
        cancel.setOnClickListener(view -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.taskRemove) {
            launchDeleteTaskPrompt();
            return true;
        } else if (item.getItemId() == R.id.taskEdit) {
            Intent intent = TasksEditActivity.makeIntent(TasksViewActivity.this, index);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchDeleteTaskPrompt() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_delete_black_24)
                .setTitle("Deleting Task: " + taskManager.getTask(index))
                .setMessage("Are you sure? This action cannot be reversed.")
                .setPositiveButton(R.string.prompt_positive, (dialog, which) -> {
                    taskManager.removeTask(index);
                    taskManager.removeChild(index);
                    Toast.makeText(TasksViewActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.prompt_negative, null)
                .show();
    }
}