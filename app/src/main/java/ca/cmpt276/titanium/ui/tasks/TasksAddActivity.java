package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Tasks;

/**
 * This class allows a user to create new tasks.
 */
public class TasksAddActivity extends AppCompatActivity {

    private Children children;
    private EditText userTaskInput;
    private Tasks taskManager;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TasksAddActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_add);
        taskManager = Tasks.getInstance();

        this.children = Children.getInstance(this);
        userTaskInput = findViewById(R.id.userTaskName);
        setUpButton();

        Toolbar myToolbar = findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        setTitle("Add Task");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setUpButton() {
        Button saveTaskButton = findViewById(R.id.saveTask);
        saveTaskButton.setOnClickListener(view -> {
            if (userTaskInput.getText().toString().isEmpty()) {
                Toast.makeText(TasksAddActivity.this, "Cannot leave task name blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String task = userTaskInput.getText().toString();
            taskManager.addTask(task);
            if (children.getChildren().size() > 0) {
                taskManager.addChild(children.getChildren().get(0));
            }

            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}