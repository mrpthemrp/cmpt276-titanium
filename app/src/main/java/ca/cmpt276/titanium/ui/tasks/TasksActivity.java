package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.TaskManager;

/**
 * This class displays all tasks.
 */
public class TasksActivity extends AppCompatActivity {
    public static Intent makeIntent(Context context) {
        return new Intent(context, TasksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        setTitle(R.string.title_view_all_tasks);

        Toolbar toolbar = findViewById(R.id.customToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateTaskList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.toolbar_button_add_task) {
            startActivity(TasksAddActivity.makeIntent(this));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void populateTaskList() {
        TaskManager taskManager = TaskManager.getInstance(this);
        TaskListAdapter taskListAdapter = new TaskListAdapter(this, taskManager.getTasks());

        ListView taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(taskListAdapter);
        taskListView.setOnItemClickListener((adapterView, view, i, l) -> startActivity(TasksViewActivity.makeIntent(this, i)));

        TextView noTasksMessage = findViewById(R.id.noTasksText);
        noTasksMessage.setVisibility(taskManager.getTasks().size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }
}
