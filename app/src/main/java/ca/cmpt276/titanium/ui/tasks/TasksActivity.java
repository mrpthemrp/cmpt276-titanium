package ca.cmpt276.titanium.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import ca.cmpt276.titanium.R;
import ca.cmpt276.titanium.model.Child;
import ca.cmpt276.titanium.model.Children;
import ca.cmpt276.titanium.model.Tasks;

public class TasksActivity extends AppCompatActivity {

    private ArrayList<String> taskList = new ArrayList<>();
    private Tasks taskManager;
    private Children children;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TasksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        setTitle(R.string.whoseTurn);
        this.children = Children.getInstance(this);
        taskManager = Tasks.getInstance();

        taskManager.loadTaskData(this);
        taskManager.loadChildData(this);
        checkTaskList();
        populate();

        Toolbar myToolbar = findViewById(R.id.customToolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        registerClickCallback();
    }

    private void checkTaskList() {
        TextView noTasks = findViewById(R.id.noTasksText);
        if (taskManager.numberOfTasks() == 0) {
            noTasks.setVisibility(View.VISIBLE);
        } else {
            noTasks.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        populate();
        checkTaskList();
        super.onStart();
    }

    @Override
    protected void onResume() {
        taskManager.saveTaskChildData();
        super.onResume();
        checkTaskList();
        populate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskManager.saveTaskChildData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.taskAdd) {
            startActivity(TasksAddActivity.makeIntent(this));
        }
        return super.onOptionsItemSelected(item);
    }

    public void set() {
        taskList = taskManager.getListOfTasks();
    }

    private void populate() {
        set();
        ArrayAdapter<String> adapter = new adapter();
        ListView list = findViewById(R.id.taskListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.taskListView);
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = TasksViewActivity.makeIntent(TasksActivity.this, i);
            startActivity(intent);
        });
    }

    public class adapter extends ArrayAdapter<String> {
        public adapter() {
            super(TasksActivity.this, R.layout.item_task_list, taskList);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View taskItemView = convertView;

            if (taskItemView == null) {
                taskItemView = getLayoutInflater().inflate(R.layout.item_task_list, parent, false);
            }

            String task = taskList.get(position);
            String name;

            if (children.getChildren().size() != 0) {
                if (taskManager.getListOfChildren().size() == 0) {
                    taskManager.clearChildList();
                    for (int i = 0; i < taskManager.getListOfTasks().size(); i++) {
                        Child child = children.getChildren().get(0);
                        taskManager.addChild(child);
                    }
                }

                UUID childID = taskManager.getChildID(position);
                int nextIndex = 0;
                for (int i = 0; i < children.getChildren().size(); i++) {
                    if (children.getChildren().get(i).getUniqueID().equals(childID)) {
                        nextIndex = i;
                    }
                }

                name = children.getChildren().get(nextIndex).getName();

            } else {
                name = "Nobody";
            }

            TextView taskName = taskItemView.findViewById(R.id.taskNameInList);
            taskName.setText(getString(R.string.taskName_start_text, task));

            TextView childName = taskItemView.findViewById(R.id.childNameForTaskInList);
            childName.setText(getString(R.string.childName_start_text, name));

            return taskItemView;
        }
    }
}